# Use a standard OpenJDK runtime
FROM openjdk:17-jdk-slim

# Set up the application directory
WORKDIR /app

# Copy compiled classes and libraries into the container
COPY target/ /app/target/
COPY lib/ /app/lib/

# Set the working directory to the target directory with compiled classes
WORKDIR /app/target

# Run the Java application, setting the classpath to include the lib directory
CMD ["java", "-cp", ".:../lib/*", "ArtistServer"]