import os
from .context import RequestCtx, Policy, PDP, PIP, Decision, GENERATED_DIR

# Internals
from tests.resources.generated.sql.ast import Subject, SQLEntity
from tests.resources.generated.sql.decoder import ASTFactory


###############################################################################
# Implementation of the abstract classes
###############################################################################
class MySQLEntity(SQLEntity):
    @property
    def id_(self):
        return self.database + "." + self.table + "." + self.column

class MySubject(Subject):
    @property
    def id_(self):
        return self.name

    @property
    def ip(self):
        if self.name == "admin":
            return "127.0.0.1"
        return "8.8.8.8"

class MyFactory(ASTFactory):
    def createSQLEntity(self, obj):
        return MySQLEntity(**obj)
    def createSubject(self, obj):
        return MySubject(**obj)
###############################################################################

###############################################################################
# Global test objects
###############################################################################
with open(os.path.join(GENERATED_DIR, "sql", "scheme.json")) as f:
    SCHEME = f.read()
with open(os.path.join(GENERATED_DIR, "sql", "error_update_policy.lua")) as f:
    ERROR_UPDATE_POLICY = f.read()
with open(os.path.join(GENERATED_DIR, "sql", "policy.lua")) as f:
    POLICY = f.read()
with open(os.path.join(GENERATED_DIR, "sql", "policyset_policy.lua")) as f:
    POLICY_SET = f.read()
with open(os.path.join(GENERATED_DIR, "sql", "dynamic_policy.lua")) as f:
    DYNAMIC_POLICY = f.read()
FACTORY = MyFactory()
###############################################################################


def test_smoke_error_policy():
    policy = Policy(POLICY)
    pdp = PDP(policy.text)
    external = """
    {
        "entities": [
            {"type": "Subject", "name": "user1", "level": 10, "tags": ["secret", "nonsecret"]},
            {"type": "Subject", "name": "user2", "level": 100, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "c", "level": 20, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "d", "level": 5, "tags": ["secret", "nonsecret"]}
        ]
    }
    """
    pip = PIP.from_json(SCHEME, external, FACTORY)

    request = RequestCtx(
        MySubject("user1"),
        [
            MySQLEntity("a", "b", "c"),
            MySQLEntity("a", "b", "d")
        ],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Indeterminate

    policy = Policy(ERROR_UPDATE_POLICY)
    pdp = PDP(policy.text)
    external = """
    {
        "entities": [
            {"type": "Subject", "name": "user1", "level": 10, "tags": ["secret", "nonsecret"]},
            {"type": "Subject", "name": "user2", "level": 100, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "c", "level": 20, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "d", "level": 5, "tags": ["secret", "nonsecret"]}
        ]
    }
    """
    pip = PIP.from_json(SCHEME, external, FACTORY)

    request = RequestCtx(
        MySubject("user1"),
        [
            MySQLEntity("a", "b", "c"),
            MySQLEntity("a", "b", "d")
        ],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.NotApplicable


def test_smoke_policy_set():
    policy = Policy(POLICY_SET)
    pdp = PDP(policy.text)
    external = """
    {
        "entities": [
            {"type": "Subject", "name": "user1", "level": 10, "tags": ["secret", "nonsecret"]},
            {"type": "Subject", "name": "user2", "level": 100, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "c", "level": 20, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "d", "level": 5, "tags": ["secret", "nonsecret"]}
        ]
    }
    """
    pip = PIP.from_json(SCHEME, external, FACTORY)

    request = RequestCtx(
        MySubject("user1"),
        [
            MySQLEntity("a", "b", "c"),
            MySQLEntity("a", "b", "d")
        ],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    request = RequestCtx(
        MySubject("user2"),
        [
            MySQLEntity("a", "b", "c"),
            MySQLEntity("a", "b", "d")
        ],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit


def test_dynamic_property():
    policy = Policy(DYNAMIC_POLICY)
    pdp = PDP(policy.text)
    external = """
    {
        "entities": [
            {"type": "Subject", "name": "user", "level": 10, "tags": ["secret", "nonsecret"]},
            {"type": "Subject", "name": "admin", "level": 100, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "c", "level": 20, "tags": ["secret", "nonsecret"]}
        ]
    }
    """
    pip = PIP.from_json(SCHEME, external, FACTORY)
    user = MySubject(name="user")
    admin = MySubject(name="admin")
    assert pip.attrs["admin"].ip == admin.ip
    assert pip.attrs["user"].ip == user.ip

    request = RequestCtx(
        user,
        [MySQLEntity("a", "b", "c")],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    request = RequestCtx(
        admin,
        [MySQLEntity("a", "b", "c")],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit
