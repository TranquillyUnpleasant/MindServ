# MindServ
A java server for mindustry content. Processes map and schematic files over a local http connection.

The code for content handling is a slightly modified version of the handling in [corebot](https://github.com/anuken/corebot)

Instructions to run:
- For compiling use `gradlew dist` for windows and `./gradlew dist` for linux and mac.
- To run the server, you need the [assets](https://github.com/Anuken/Mindustry/tree/master/core/assets) and [assets-raw](https://github.com/Anuken/Mindustry/tree/master/core/assets-raw) folders in the same directory as the compiled jar file. Then run `java -jar server.jar` in a terminal to start the server.
- Gradle 7 and jdk 16 is required to compile, and jre 16 is required to run the server.
