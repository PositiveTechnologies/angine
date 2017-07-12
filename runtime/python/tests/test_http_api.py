import os
from .context import RequestCtx, Policy, PDP, PIP, Decision, GENERATED_DIR

# Internals
from tests.resources.generated.http_api.ast import Subject, UrlEntity
from tests.resources.generated.http_api.decoder import ASTFactory


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
with open(os.path.join(GENERATED_DIR, "http_api", "scheme.json")) as f:
    SCHEME = f.read()
with open(os.path.join(GENERATED_DIR, "http_api", "policy.lua")) as f:
    POLICY = f.read()
FACTORY = MyFactory()
###############################################################################


def test_http_api():
    policy = Policy(POLICY)
    pdp = PDP(policy.text)

    external = """
        {
            "entities": [
                {"type": "Subject", "name": "guest", "roles": ["guest", "null"]},
                {"type": "Subject", "name": "admin", "roles": ["admin"]},
                {"type": "Subject", "name": "user", "roles": ["user"]},
                {"type": "Subject", "name": "anon", "roles": []},
                {"type": "UrlEntity", "path":"/admin"},
                {"type": "UrlEntity", "path": "/stats"},
                {"type": "UrlEntity", "path": "/motd"},
                {"type": "UrlEntity", "path": "/index.html"}
            ]
        }
    """
    pip = PIP.from_json(SCHEME, external, FACTORY)

    # Permit guest to index.html
    request = RequestCtx(
        MySubject("guest"),
        [
            MyUrlEntity("/index.html"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    print(response)
    assert response == Decision.Permit

    # Permit admin to index.html
    request = RequestCtx(
        MySubject("admin"),
        [
            MyUrlEntity("/index.html"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    # Permit user to index.html
    request = RequestCtx(
        MySubject("user"),
        [
            MyUrlEntity("/index.html"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    # Deny non-existent user to index.html
    request = RequestCtx(
        MySubject("anon"),
        [
            MyUrlEntity("/index.html"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    # Permit admin to motd via POST only
    request = RequestCtx(
        MySubject("admin"),
        [
            MyUrlEntity("/motd"),
        ],
        "POST"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    # Deny guest to motd via GET
    request = RequestCtx(
        MySubject("guest"),
        [
            MyUrlEntity("/motd"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    # Permit user to motd
    request = RequestCtx(
        MySubject("user"),
        [
            MyUrlEntity("/motd"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    # Deny non-existent user to motd
    request = RequestCtx(
        MySubject("anon"),
        [
            MyUrlEntity("/motd"),
        ],
        "POST"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    # Deny non-existent user to motd
    request = RequestCtx(
        MySubject("anon"),
        [
            MyUrlEntity("/motd"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    # Permit local access to stats via GET
    request = RequestCtx(
        MySubject("admin"),
        [
            MyUrlEntity("/stats"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Permit

    # Deny local access to stats via POST
    request = RequestCtx(
        MySubject("admin"),
        [
            MyUrlEntity("/stats"),
        ],
        "POST"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    # Deny access to stats
    request = RequestCtx(
        MySubject("user"),
        [
            MyUrlEntity("/stats"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny

    # Deny access to stats for non-existent users
    request = RequestCtx(
        MySubject("anon"),
        [
            MyUrlEntity("/stats"),
        ],
        "GET"
    )
    evaluation_ctxs = pip.create_ctx(request)
    response = pdp.evaluate(evaluation_ctxs)
    assert response == Decision.Deny