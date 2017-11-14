import os
from .context import RequestCtx, Policy, PDP, PIP, Decision

# Internals
from tests.resources.generated.sql.ast import Subject, SQLEntity
from tests.resources.generated.sql.decoder import ASTFactory
from .context import GENERATED_DIR


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
with open(os.path.join(GENERATED_DIR, "sql", "policy.lua")) as f:
    POLICY = f.read()
FACTORY = MyFactory()
###############################################################################


def test_features():
    policy = Policy(POLICY)
    assert policy.text
    assert type(policy.text) == str

    subject = MySubject("user1", 10)
    assert subject.id_ == "user1"
    assert subject.level == 10
    entity = MySQLEntity("a", "b", "c", 20)
    assert entity.id_ == "a.b.c"
    assert entity.level == 20

    external = """
    {
        "entities": [
            {"type": "Subject", "name": "user1", "level": 10, "tags": ["secret", "nonsecret"]},
            {"type": "Subject", "name": "user2", "level": 100, "tags": ["secret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "c", "level": 20, "tags": ["secret", "nonsecret"]},
            {"type": "SQLEntity", "database": "a", "table": "b", "column": "d", "level": 5, "tags": ["secret"]}
        ]
    }
    """
    pip = PIP.from_json(SCHEME, external, FACTORY)
    subject = pip.attrs["user1"]
    assert subject.name == "user1"
    assert subject.level == 10
    assert subject.tags == ["secret", "nonsecret"]
    subject = pip.attrs["user2"]
    assert subject.name == "user2"
    assert subject.level == 100
    assert subject.tags == ["secret"]
    entity = pip.attrs["a.b.c"]
    assert entity.database == "a"
    assert entity.table == "b"
    assert entity.column == "c"
    assert entity.level == 20
    assert entity.tags == ["secret", "nonsecret"]
    entity = pip.attrs["a.b.d"]
    assert entity.database == "a"
    assert entity.table == "b"
    assert entity.column == "d"
    assert entity.level == 5
    assert subject.tags == ["secret"]

    request = RequestCtx(
        MySubject("user1"),
        [
            MySQLEntity("a", "b", "c"),
            MySQLEntity("a", "b", "d")
        ],
        "select"
    )
    evaluation_ctxs = pip.create_ctx(request)
    assert len(evaluation_ctxs) == 2
    assert evaluation_ctxs[0].subject.level == 10
    assert evaluation_ctxs[0].entity.level == 20
    assert evaluation_ctxs[0].entity.tags == ["secret", "nonsecret"]
    assert evaluation_ctxs[0].action == "select"
    assert evaluation_ctxs[1].subject.level == 10
    assert evaluation_ctxs[1].entity.level == 5
    assert evaluation_ctxs[1].entity.tags == ["secret"]
    assert evaluation_ctxs[1].action == "select"
    pdp = PDP(policy.text)
    response = pdp.evaluate(evaluation_ctxs)
    assert response.results[0].decision == Decision.Indeterminate

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
    assert response.results[0].decision == Decision.Permit
