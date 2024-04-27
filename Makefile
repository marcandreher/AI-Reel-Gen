#!/usr/bin/env make

build:
	cd ai-reel-gen/ && mvn clean package shade:shade;
	mv ai-reel-gen/target/build-jar-with-dependencies.jar build.jar

run:
	@if screen -ls | grep -q "builder-ws"; then \
        echo "Screen session 'builder-ws' is running."; \
		exit 1; \
    else \
        echo "Screen session 'builder-ws' is not running."; \
    fi

	screen -S builder-ws java -jar build.jar;

restart:
	@if screen -ls | grep -q "builder-ws"; then \
        echo "Screen session 'builder-ws' is running."; \
    else \
        echo "Screen session 'builder-ws' is not running."; \
		exit 1; \
    fi

	make stop
	screen -S builder-ws java -jar build.jar;

restart-b:
	make build;
	make restart;

run-b:
	make build;
	make run;

go:
	screen -x builder-ws

stop:
	screen -S builder-ws -X quit; \

no-screen:
	java -jar build.jar

