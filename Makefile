TOOL_TEST_RESOURCES=tests/resources
PYTHON_RUNTIME = runtime/python
PYTHON_TEST_RESOURCES=$(PYTHON_RUNTIME)/tests/resources

PYTHON=python3.6

clean:
	@echo "Cleaning:"
	find . -type f -name "*.py[co]" -delete
	find . -type d -name "__pycache__" -delete


tests-for-python-runtime:
	${PYTHON} -m angine -t python -i $(PYTHON_TEST_RESOURCES)/fixtures/sql/sql.spec                  \
	                              -o $(PYTHON_TEST_RESOURCES)/generated/sql/                         \
	                              -p $(PYTHON_TEST_RESOURCES)/fixtures/sql/policy.alfa               \
	                                 $(PYTHON_TEST_RESOURCES)/fixtures/sql/error_update_policy.alfa  \
	                                 $(PYTHON_TEST_RESOURCES)/fixtures/sql/policyset_policy.alfa     \
	                                 $(PYTHON_TEST_RESOURCES)/fixtures/sql/dynamic_policy.alfa       \

	${PYTHON} -m angine -t python -i $(PYTHON_TEST_RESOURCES)/fixtures/http/http.spec                \
	                              -o $(PYTHON_TEST_RESOURCES)/generated/http/                        \
	                              -p $(PYTHON_TEST_RESOURCES)/fixtures/http/policy.alfa

	${PYTHON} -m angine -t python -i $(PYTHON_TEST_RESOURCES)/fixtures/http_api/http_api.spec        \
	                              -o $(PYTHON_TEST_RESOURCES)/generated/http_api/                    \
	                              -p $(PYTHON_TEST_RESOURCES)/fixtures/http_api/policy.alfa

	mkdir -p $(PYTHON_TEST_RESOURCES)/generated/angine/
	cp -r $(PYTHON_RUNTIME)/angine/*  $(PYTHON_TEST_RESOURCES)/generated/angine/

clean-generated-code:
	@echo "Cleaning after testing:"
	rm -rf $(PYTHON_TEST_RESOURCES)/generated
	rm -rf $(TOOL_TEST_RESOURCES)/generated


tests: tests-for-python-runtime clean run-tests clean-generated-code
	@echo "Angine testing:"


run-tests:
	@echo "Run tests:"
	nosetests tests/test_*.py
	nosetests runtime/python/tests/test_*.py


packages:
	@echo "Lua dependencies installation:"
	apt-get install liblua5.2-dev
	@echo "Python dependencies installation:"
	apt-get install python3-dev
	${PYTHON} -m pip install -r requirements.txt


lib:
	mkdir -p /usr/local/share/lua/5.2/angine/
	cp angine/lib/alfa.lua /usr/local/share/lua/5.2/angine/


install: packages lib


examples:
	${PYTHON} -m angine -t python -i fixtures/sql/sql.spec   -o generated/sql  -p fixtures/sql/policy.alfa
	${PYTHON} -m angine -t python -i fixtures/http/http.spec -o generated/http -p fixtures/http/policy.alfa


init:
	@echo "export PYTHONPATH=${PYTHONPATH}:`pwd`"


all: init test


.PHONY: clean test