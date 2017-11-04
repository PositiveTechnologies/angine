import os, sys
from angine.__main__ import main, create_parser
GENERATED_PATH = "resources/generated/"
FIXTURE_PATH = "resources/fixtures/"


def test_cli():
    args = create_parser().parse_args([
        "--target", "java",
        "--interfaces", os.path.join(sys.path[0], FIXTURE_PATH, "http", "http.spec"),
        "--policies", os.path.join(sys.path[0], FIXTURE_PATH, "http", "policy.alfa"),
        "--output", os.path.join(sys.path[0], GENERATED_PATH, "http"),
        "--package", "angine.generated"
    ])
    main(args)

    '''
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "policy.lua"))
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "scheme.json"))
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "decoder.py"))
    assert os.path.exists(os.path.join(GENERATED_PATH, "http", "ast.py"))
    '''

test_cli()