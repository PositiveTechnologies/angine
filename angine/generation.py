"""
aNgine uses Aule parser so ensure that you have it installed or added to
your python path
"""
import enum
from typing import List
from aule import ASTParserFactory
from aule.codegen import GeneratorFactory, Language

from .config import LUA_LIB_PATH


class GenerationLanguage(enum.Enum):
    python = Language.pythonIDL
    java = Language.javaIDL


def parse_interfaces(spec: str) -> List[object]:
    """ Parses provided interfaces specification in IDL and returns
        resulting Aule.UST.
    """
    ast_parser = ASTParserFactory.create("idl")
    return ast_parser.parse(spec)


def generate_policy_runtime(policy: str) -> str:
    """ Generates Lua runtime for PDP module.

    :param policy: policy description in ALFA language
    :return Lua code for the PDP runtime
    """
    import_line = "local lib = require('{}')".format(LUA_LIB_PATH)
    ast_parser = ASTParserFactory.create("alfa")
    tree = ast_parser.parse(policy)
    if tree is None:
        raise ValueError("Can't parse provided policy")
    generator = GeneratorFactory.create(language=Language.lua)
    generator.use_tree(tree, mutableAST=True)
    return import_line + '\n' + generator.generate()


def generate_interfaces(runtime: str, tree: List[object]) -> str:
    """ Generates interface classes in runtime language.

    :param runtime: target language of the generated interfaces
    :param tree: list of Aule.UST nodes representing the interfaces
    :return: Code of the generated interface objects in runtime language
    """
    generator = GeneratorFactory.create(language=GenerationLanguage[runtime].value)
    generator.use_tree(tree, mutableAST=False)
    return generator.generate()


def generate_decoder(runtime: str, tree: List[object]) -> str:
    """ Generates json hook to transform interfaces data from JSON to the
    runtime objects itself.

    :param runtime: target language of the generated interfaces
    :param tree: list of Aule.UST nodes representing the interfaces
    :return: Code of the generated decoder in runtime language
    """
    generator = GeneratorFactory.create(language=GenerationLanguage[runtime].value)
    generator.use_tree(tree, mutableAST=False)
    return generator.generate_decoder()


def generate_scheme(tree: List[object]) -> str:
    """ Generates JSON scheme that will be used during PIP initialization.

    :param tree: list of Aule.UST nodes representing the interfaces
    :return: Code of the generated interface objects in runtime language
    """
    generator = GeneratorFactory.create(language=Language.jsonScheme)
    generator.use_tree(tree, mutableAST=False)
    return generator.generate()
