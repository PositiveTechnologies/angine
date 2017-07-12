import os
import sys
import inspect
from angine import RequestCtx, Policy, PDP, PIP, Decision


CURRENT_DIR = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
PARENT_DIR = os.path.dirname(CURRENT_DIR)
GENERATED_DIR = os.path.join(CURRENT_DIR, "resources", "generated")
sys.path.insert(0, PARENT_DIR)
