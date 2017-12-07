TOOL_TEST_RESOURCES=tests/resources
PYTHON_RUNTIME = runtime/python
PYTHON_TEST_RESOURCES=$(PYTHON_RUNTIME)/tests/resources

PYTHON=python3.6

JAVA_RUNTIME = runtime/java/angine
JAVA_TEST = runtime/java/test
JAVA_TEST_RES = ${JAVA_TEST}/fixtures/http
JAVA_TEST_GEN = ${JAVA_TEST}/generated
JAVA_TEST_CLASS_FOLDER = ${JAVA_TEST}/src/main/java/generated
JAVA_TEST_RES_FOLDER = ${JAVA_TEST}/src/main/resources


clear-java:
	cd ${JAVA_RUNTIME}; mvn clean
	cd ${JAVA_TEST}; mvn clean
	rm -rf $(JAVA_TEST_GEN)
	rm -rf ${JAVA_TEST}/libs
	rm -rf ${JAVA_TEST_CLASS_FOLDER}
	rm ${JAVA_TEST_RES_FOLDER}/scheme.json
	rm ${JAVA_TEST_RES_FOLDER}/policy.lua
	rm -rf ${JAVA_TEST_RES_FOLDER}/angine    
	@echo "folders are cleared"      
	

compile-java-angine:
	cd ${JAVA_RUNTIME}; mvn clean install

	
deploy-angine-to-test:
	mkdir -p ${JAVA_TEST}/libs
	cp ${JAVA_RUNTIME}/target/angine-0.1-SNAPSHOT.jar ${JAVA_TEST}/libs


generate-angine-for-java:
	mkdir -p $(JAVA_TEST_GEN)
	${PYTHON} -m angine -t java -i $(JAVA_TEST_RES)/http.spec   \
	                            -o $(JAVA_TEST_GEN)             \
	                            -p $(JAVA_TEST_RES)/policy.alfa \
	                            --package generated
	                                             	
	
deploy-generated-to-test:          
	mkdir -p ${JAVA_TEST_CLASS_FOLDER}                  
	cp $(JAVA_TEST_GEN)/AST.java ${JAVA_TEST_CLASS_FOLDER}
	cp $(JAVA_TEST_GEN)/Decoder.java ${JAVA_TEST_CLASS_FOLDER}
	cp $(JAVA_TEST_GEN)/policy.lua ${JAVA_TEST_RES_FOLDER}
	cp $(JAVA_TEST_GEN)/scheme.json ${JAVA_TEST_RES_FOLDER}
	mkdir -p ${JAVA_TEST_RES_FOLDER}/angine
	cp angine/lib/alfa.lua ${JAVA_TEST_RES_FOLDER}/angine
	
	
	
run-java-test:
	cd ${JAVA_TEST}; mvn clean install
	cd ${JAVA_TEST}; mvn dependency:copy-dependencies
	clear
	cd ${JAVA_TEST}/target; java -cp http-1.0-SNAPSHOT.jar:dependency/* Main
	@echo "test finished"
	
test-java: compile-java-angine deploy-angine-to-test generate-angine-for-java deploy-generated-to-test run-java-test
	
	
	


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
	apt-get install liblua5.3-dev
	@echo "Python dependencies installation:"
	apt-get install python3-dev
	${PYTHON} -m pip install -r requirements.txt


lib:
	mkdir -p /usr/local/share/lua/5.3/angine/
	cp angine/lib/alfa.lua /usr/local/share/lua/5.3/angine/


install: packages lib


examples:
	${PYTHON} -m angine -t python -i fixtures/sql/sql.spec   -o generated/sql  -p fixtures/sql/policy.alfa
	${PYTHON} -m angine -t python -i fixtures/http/http.spec -o generated/http -p fixtures/http/policy.alfa


init:
	@echo "export PYTHONPATH=${PYTHONPATH}:`pwd`"


all: init test


.PHONY: clean test
