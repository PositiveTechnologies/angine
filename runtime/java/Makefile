JAVA_RUNTIME = runtime/java/angine
JAVA_TEST = runtime/java/test



test:
	cd ${JAVA_RUNTIME}; mvn clean install
	cd ${JAVA_RUNTIME}; mvn dependency:copy-dependencies
	cd ${JAVA_RUNTIME}/target; cp anginetest-1.0-SNAPSHOT.jar ../../test/libs
	cd ${JAVA_RUNTIME}; mvn clean
	@echo "cp finish"
	

