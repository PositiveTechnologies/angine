import lupa
from typing import List
from .handlers import *
from .context import ResponseCtx, EvaluationCtx
from .results import Decision, Status, Result, AbstractResult


class PDP:
    LUA_ENTRY = "__main"

    def __init__(self, lua_policy: str):
        """
        :param lua_policy: generated Lua policy
        """
        lua = lupa.LuaRuntime(
            attribute_handlers=(getter, setter),
            unpack_returned_tuples=True
        )
        if lua is None:
            raise RuntimeError("Lua runtime can't be initialized")

        self.lua_runtime = lua
        self.actions = self.lua_runtime.table_from({
            e.name.lower(): e.value for e in Decision
        })

        # Look for the policy entry point
        lua.execute(lua_policy)
        g = lua.globals()
        self.lua_policy = None
        for gname, gfunc in dict(g).items():
            if gname.startswith(self.LUA_ENTRY):
                self.lua_policy = gfunc
                break
        else:
            raise ValueError("ALFA policy entry point not found")

    def evaluate(self, evaluation_ctx: EvaluationCtx) -> ResponseCtx:
        """ Evaluates the @self.lua_policy for all given contexts

        :param evaluation_ctx: context, containing all necessary information for evaluation
        :return if @format_results to True returns list of decisions in a format
            described in `self.get_response` otherwise returns list of Decision enums
        """
        ctx_list = evaluation_ctx.access_requests
        handlers = Handlers()
        results = [
            Result(
                Decision(
                    self.lua_policy(ctx, self.actions, handlers)
                )
            ) for ctx in ctx_list
        ]
        if evaluation_ctx.combined_decision:
            return ResponseCtx(
                [PDP.get_xacml3_combined_decision(results)]
            )
        else:
            return ResponseCtx(results)

    @staticmethod
    def get_xacml3_combined_decision(results: List[AbstractResult]) -> AbstractResult:
        """
        Implements XACML3 Multiple Decision Profile combining method.
        """
        if all(result.decision == results[0].decision for result in results):
            return results[0]
        else:
            return AbstractResult(
                Decision.Indeterminate, Status(Status.Code.ProcessingError)
            )
