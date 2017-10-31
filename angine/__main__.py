import os
import sys
import argparse
from argparse import ArgumentParser

from .generation import *
from .__metadata__ import __version__, __description__, __runtimes__


if sys.version_info < (3, 6):
    sys.exit("Python < 3.6 is not supported")


# Add arguments declaration
def create_parser() -> ArgumentParser:
    """
    Creates and returns CLI parser.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument("--version", "-v",
                        action="store_true",
                        help="Display version.")
    parser.add_argument("--interfaces", "-i",
                        action="store",
                        type=str,
                        required=True,
                        help="Specify input IDL interfaces file.")
    parser.add_argument("--policies", "-p",
                        action="store",
                        type=str,
                        nargs="+",
                        required=True,
                        help="Specify ALFA policies files.")
    parser.add_argument("--output", "-o",
                        action="store",
                        type=str,
                        required=True,
                        help="Specify output directory.")
    parser.add_argument("--target", "-t",
                        choices=__runtimes__,
                        action="store",
                        type=str,
                        required=True,
                        help="Specify a target language")
    return parser


def main(args) -> None:
    # Get the target specification
    policies = []

    for policy_file in args.policies:
        with open(policy_file) as f:
            policies.append({
                "name": os.path.basename(policy_file).split(".")[0],
                "alfa": f.read(),
                "lua": None
            })

    with open(args.interfaces) as f:
        interfaces_spec = f.read()

    # Construct names for all generating parts
    runtime = args.target
    output_dir = args.output
    ####################################################
    # TODO: That guys should have runtime-specific name
    interfaces_file = interfaces_filename[runtime]
    decoder_file = decoder_filename[runtime]
    ###################################################
    json_scheme_file = "scheme.json"

    # Prepare target directory
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    # Parse specifications and generate all necessary code
    interfaces_ast = parse_interfaces(interfaces_spec)
    interface_classes = generate_interfaces(runtime, interfaces_ast)
    decoder = generate_decoder(runtime, interfaces_ast)
    json_scheme = generate_scheme(interfaces_ast)

    for policy in policies:
        policy["lua"] = generate_policy_runtime(policy["alfa"])

    # Write it to the destination
    with open(os.path.join(output_dir, interfaces_file), "w") as f:
        f.write(interface_classes)
    with open(os.path.join(output_dir, decoder_file), "w") as f:
        ###############################################
        # TODO: runtime-specific also
        import_line = "from .{} import *\n\n".format(
            os.path.splitext(os.path.basename(interfaces_file))[0]
        )
        ###############################################
        f.write(import_line + decoder)
    with open(os.path.join(output_dir, json_scheme_file), "w") as f:
        f.write(json_scheme)

    for policy in policies:
        lua_runtime_file = policy["name"] + ".lua"
        with open(os.path.join(output_dir, lua_runtime_file), "w") as f:
            f.write(policy["lua"])

interfaces_filename = {
    "python" : "ast.py",
    "java" : "AST.java"
}

decoder_filename = {
    "python" : "decoder.py",
    "java" : "Decoder.java"
}


if __name__ == "__main__":
    args = create_parser().parse_args()
    if args.version:
        print("{} version {}".format(__description__, __version__))
        exit(0)
    main(args)
