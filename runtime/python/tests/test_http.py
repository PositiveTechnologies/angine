import os
from .context import RequestCtx, Policy, PDP, PIP, Decision, GENERATED_DIR

# Internals
from tests.resources.generated.http.ast import Subject, UrlEntity
from tests.resources.generated.http.decoder import ASTFactory


###############################################################################
# Implementation of the abstract classes
###############################################################################
class MyUrlEntity(UrlEntity):
    @property
    def id_(self):
        return self.path


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
    def createUrlEntity(self, obj):
        return MyUrlEntity(**obj)
    def createSubject(self, obj):
        return MySubject(**obj)
###############################################################################

###############################################################################
# Global test objects
###############################################################################
with open(os.path.join(GENERATED_DIR, "http", "scheme.json")) as f:
    SCHEME = f.read()
with open(os.path.join(GENERATED_DIR, "http", "policy.lua")) as f:
    POLICY = f.read()
FACTORY = MyFactory()
###############################################################################


def test_http():
    policy = Policy(POLICY)
    pdp = PDP(policy.text)
    external = """
        {
            "entities": [
                {"type": "Subject", "name": "user1", "level": 10, "tags": ["secret", "nonsecret"]},
                {"type": "Subject", "name": "user2", "level": 5, "tags": ["secret", "nonsecret"]},
                {"type": "Subject", "name": "admin", "level": 10, "tags": ["secret", "nonsecret"]},
                {"type": "UrlEntity", "path":"/admin/", "level": 20, "tags": ["nonsecret"]},
                {"type": "UrlEntity", "path": "/index.html", "level": 5, "tags": ["nonsecret"]}
            ]
        }
        """
    pip = PIP.from_json(SCHEME, external, FACTORY)

    request = RequestCtx(
        MySubject("user1"),
        [
            MyUrlEntity("/index.html"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    request = RequestCtx(
        MySubject("user2"),
        [
            MyUrlEntity("/admin/"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    request = RequestCtx(
        MySubject("admin"),
        [
            MyUrlEntity("/index.html"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny
