from os import path

# Project directory
BASE_DIR = path.dirname(path.abspath(__file__))

# Path to the lua library file used by lua policy rules
LUA_LIB_PATH = path.join("angine", "alfa")