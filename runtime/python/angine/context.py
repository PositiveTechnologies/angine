import json
from typing import List
from .results import Decision, Status, AbstractResult, Result


class RequestCtx(object):
    """ Request context is the one we have at PDP (in our case in PDP before the
        call to Lua runtime). It contains all the necessary information about the
        action we are going to deny or permit. Here necessary means all the IDs
        or info needed by PIP to resolve all other entity attributes

        :param subject: an object which describes the one who wants to perform an action
        :param entities: list of objects describing entities affected by the subject's
            action
        :param action: an object which describes subject's action
    """
    def __init__(self, subject, entities, action):
        self.subject = subject
        self.entities = entities
        self.action = action


class EvaluationCtx(object):
    """ Evaluation context is build from a request context for every single entity
        by just filling up all the attributes resolving them via PIP.

        :param subject: an object which describes the one who wants to perform an action
        :param entities: an object which describes an entity affected by the action
        :param action: an object which describes subject's action
    """
    def __init__(self, subject, entity, action):
        self.subject = subject
        self.entity = entity
        self.action = action


class ResponseCtx:
    """
    Represents response from PDP to PEP.
    """
    results: List[AbstractResult]

    def __init__(self, results: List[AbstractResult]) -> None:
        self.results = results

    def add_result(self, result: AbstractResult) -> None:
        self.results.append(result)

    def __str__(self) -> str:
        length = len(self.results)
        if length == 1:
            result = self.results[0].encode()
        elif length > 1:
            result = ResponseCtx.get_combined_decision(self.results).encode()
            result.update(dict(results=[item.encode() for item in self.results]))
        else:
            result = Result(
                Decision.Indeterminate,
                Status(Status.Code.ProcessingError, "Response must have at least one Result")
            ).encode()
        return json.dumps(result)

    @staticmethod
    def get_combined_decision(results: List[AbstractResult]) -> AbstractResult:
        """
        Implements custom simple combining decision method.
        """
        at_least_one_deny = False
        for result in results:
            if result.decision in [Decision.Indeterminate, Decision.NotApplicable]:
                return AbstractResult(
                    Decision.Indeterminate, Status(Status.Code.Ok)
                )
            if result.decision == Decision.Deny:
                at_least_one_deny = True
        if at_least_one_deny:
            return AbstractResult(
                    Decision.Deny, Status(Status.Code.Ok)
                )
        return AbstractResult(
                Decision.Permit, Status(Status.Code.Ok)
            )


class ResponseCtxFactory:
    """
    Returns specified instance of Response.
    """
    @staticmethod
    def indeterminate(status_code: Status.Code, status_message: str) -> ResponseCtx:
        status = Status(status_code, status_message)
        response_ctx = ResponseCtxFactory.from_result(Result(Decision.Indeterminate, status))
        return response_ctx

    @staticmethod
    def from_result(result: AbstractResult) -> ResponseCtx:
        return ResponseCtx([result])

    @staticmethod
    def from_results(results: List[AbstractResult]) -> ResponseCtx:
        return ResponseCtx(results)