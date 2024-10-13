# First Stage: Build and Create Custom JRE
FROM openjdk:17-jdk-slim AS build

# Install binutils for objcopy
RUN apt-get update && apt-get install -y --no-install-recommends binutils && rm -rf /var/lib/apt/lists/*

# Set up working directory
WORKDIR /app

# Copy source and library files
COPY src/ src/
COPY lib/ lib/

# Compile the Java application
RUN javac -cp "lib/*" src/ArtistServer.java -d target

# Use jdeps to determine necessary JDK modules (without Jackson)
RUN jdeps --ignore-missing-deps \
          --print-module-deps \
          --multi-release 17 \
          target/ArtistServer.class > modules.txt

# Create the custom JRE using jlink based on jdeps output
RUN jlink --module-path $JAVA_HOME/jmods \
          --add-modules $(cat modules.txt) \
          --output /custom-jre \
          --strip-debug \
          --no-man-pages \
          --no-header-files \
          --compress=2

# Second Stage: Build Minimal Runtime Image
FROM debian:stable-slim

# Set environment variables for the custom JRE
ENV JAVA_HOME=/custom-jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the custom JRE from the build stage
COPY --from=build /custom-jre /custom-jre

# Copy application files
COPY --from=build /app/target /app/target
COPY --from=build /app/lib /app/lib

# Set working directory
WORKDIR /app/target

# Run the Java application
CMD ["java", "-cp", ".:../lib/*", "ArtistServer"]
