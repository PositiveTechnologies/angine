import json
from typing import List
from .results import Decision, Status, AbstractResult, Result


class RequestCtx:
    """ Request context is the one we have at PDP (in our case in PDP before the
        call to Lua runtime). It contains all the necessary information about the
        action we are going to deny or permit. Here necessary means all the IDs
        or info needed by PIP to resolve all other entity attributes

        :param subject: an object which describes the one who wants to perform an action
        :param entities: list of objects describing entities affected by the subject's
            action
        :param action: an object which describes subject's action
        :param combined_decision: returned response must contain combined decision
    """
    def __init__(self, subject, entities, action, combined_decision=True) -> None:
        self.subject = subject
        self.entities = entities
        self.action = action
        self.combined_decision = combined_decision


class AccessRequest:
    """ It is created from a request context for every (subject, entity, action) triad
        by just filling up all the attributes resolving them via PIP.

        :param subject: an object which describes the one who wants to perform an action
        :param entity: an object which describes an entity affected by the action
        :param action: an object which describes the subject on the object action
    """
    def __init__(self, subject, entity, action) -> None:
        self.subject = subject
        self.entity = entity
        self.action = action


class EvaluationCtx:
    """ Represents evaluation context. """
    def __init__(self, access_requests: List[AccessRequest], combined_decision=True) -> None:
        self.access_requests = access_requests
        self.combined_decision = combined_decision


class ResponseCtx:
    """ Represents response from PDP to PEP. """
    results: List[AbstractResult]

    def __init__(self, results: List[AbstractResult]) -> None:
        self.results = results

    def add_result(self, result: AbstractResult) -> None:
        self.results.append(result)

    def __str__(self) -> str:
        if self.results:
            result = dict(results=[item.encode() for item in self.results])
        else:
            result = Result(
                Decision.Indeterminate,
                Status(Status.Code.ProcessingError, "Response must contain at least one Result")
            ).encode()
        return json.dumps(result)


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
