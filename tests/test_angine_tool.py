import os
from angine.__main__ import main, create_parser
GENERATED_PATH = "tests/resources/generated/"
FIXTURE_PATH = "tests/resources/fixtures/"


def test_cli():
    args = create_parser().parse_args([
        "--target", "python",
        "--interfaces", os.path.join(FIXTURE_PATH, "http", "http.spec"),
        "--policies", os.path.join(FIXTURE_PATH, "http", "policy.alfa"),
        "--output", os.path.join(GENERATED_PATH, "http")
    ])
    main(args)
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "policy.lua"))
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "scheme.json"))
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "decoder.py"))
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "ast.py"))
