import enum
from typing import Dict


class Decision(enum.IntEnum):
    """ Possible return values. """
    Permit = 0
    Deny = 1
    NotApplicable = 2
    Indeterminate = 3


class Status:
    """ Possible status values. """

    class Code(enum.IntEnum):
        """ Possible code values. """
        Ok = 0
        ProcessingError = 1
        SyntaxError = 2
        MissingAttribute = 3

    code: Code
    message: str

    def __init__(self, code: Code = Code.Ok, message: str = None) -> None:
        self.code = code
        self.message = message


class Result:
    """
    Result of decision point.
    """
    decision: Decision
    status: Status

    def __init__(self, decision: Decision, status: Status = Status(Status.Code.Ok, "")) -> None:
        self.decision = decision
        self.status = status

    def encode(self) -> Dict:
        return dict(
            decision=self.decision.name.lower(),
            status=dict(code=self.status.code.name.lower(), message=self.status.message)
        )


class ResultFactory:
    """
    Returns specified instance of AbstractResult.

    Currently it returns only one type of result (Result) based on decision and status.
    """

    @staticmethod
    def create(decision: Decision, status: Status) -> Result:
        return Result(decision, status)
