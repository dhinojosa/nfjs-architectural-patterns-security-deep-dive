
#!/bin/bash

# Create the lib directory if it doesn't exist
mkdir -p lib

# Download the jackson-databind JAR using curl and save it in the lib directory
curl -o lib/jackson-databind-2.18.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.18.0/jackson-databind-2.18.0.jar
curl -o lib/jackson-core-2.18.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.18.0/jackson-core-2.18.0.jar
curl -o lib/jackson-annotations-2.18.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.18.0/jackson-annotations-2.18.0.jar

# Alternatively, you can use wget to fetch the JAR file
# wget -P lib https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.18.0/jackson-databind-2.18.0.jar

echo "Dependency downloaded and saved in the lib directory."
