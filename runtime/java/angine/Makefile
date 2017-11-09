test:
	mvn clean install
	mvn dependency:copy-dependencies
	cd target; java -cp anginetest-1.0-SNAPSHOT.jar:dependency/* Main
	@echo "Test finished, cleaning"
	mvn clean
