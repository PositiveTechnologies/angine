import lupa
import enum
import typing

from .pip import EvaluationCtx
from .handlers import *


class Decision(enum.IntEnum):
    """ Decision described possible return values of the Lua runtime """
    Permit = 0
    Deny = 1
    NotApplicable = 2
    Indeterminate = 3


class Status(enum.Enum):
    MissingAttribute = "Missing attribute"
    Ok = "ok"
    ProcessingError = "Processing error"
    SyntaxError = "Syntax Error"


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

    def evaluate(self, ctx_list: typing.List[EvaluationCtx], format_results: bool = False):
        """ Evaluates the @self.lua_policy for a given all given context

        :param ctx_list: list of EvaluationCtx to pass to the policy
        :return if @format_results to True returns list of decisions in a format
            described in `self.get_response` otherwise returns list of Decision enums
        """
        handlers = Handlers()
        decisions = [
            Decision(
                self.lua_policy(ctx, self.actions, handlers)
            ) for ctx in ctx_list
        ]
        if all(d == decisions[0] for d in decisions):
            final_decision = decisions[0]
        else:
            final_decision = Decision.Indeterminate
        if format_results:
            return self.format_response(final_decision)
        return final_decision

    @staticmethod
    def format_response(decision: Decision, status: str = None):
        return {
            "result": {
                "decision": decision.name.lower(),
                "status": status
            }
        }
