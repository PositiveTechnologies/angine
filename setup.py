import sys
from setuptools import setup, find_packages

from .angine.__metadata__ import *

if sys.version_info < (3, 6):
    sys.exit("Python < 3.6 is not supported")

"""
aNgine uses Aule parser so ensure that you have it installed or added to
your python path
"""

setup(
    name=str.lower(__title__),
    version=__version__,
    description=__description__,
    author=__author__,
    author_email=__author_email__,
    url='https://gitlab.ptsecurity.ru/dkolegov/angine/',
    license=license,
    packages=find_packages(exclude=('tests', 'docs')),
    install_requires=[
        "antlr4-python3-runtime>=4.6",
        "lupa>=1.4",
        "jsl==0.2.4",
        "jsonschema>=2.6.0",
        "aule==0.2.0",
    ],
)