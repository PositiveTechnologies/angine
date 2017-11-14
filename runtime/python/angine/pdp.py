import lupa
import typing
from .handlers import *
from .context import ResponseCtx, EvaluationCtx
from .results import Decision, Result


class PDP(object):
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

    def evaluate(self, ctx_list: typing.List[EvaluationCtx]) -> ResponseCtx:
        """ Evaluates the @self.lua_policy for all given contexts

        :param ctx_list: list of EvaluationCtx to pass to the policy
        :return if @format_results to True returns list of decisions in a format
            described in `self.get_response` otherwise returns list of Decision enums
        """
        handlers = Handlers()
        results = [
            Result(
                Decision(
                    self.lua_policy(ctx, self.actions, handlers)
                )
            ) for ctx in ctx_list
        ]
        return ResponseCtx(results)
