# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Specify the author/maintainer of the image
LABEL maintainer="lediopapa@protonmail.com"

# Optional: Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file from your target directory to the container
COPY /target/challenge-0.0.1-SNAPSHOT.jar /app/tml-coding-challenge.jar

# Set the active Spring profile to 'cl'
ENV SPRING_PROFILES_ACTIVE=era
ENV DEBUG=false

# Run the JAR file using the OpenJDK 17 JRE
ENTRYPOINT ["java", "-jar", "/app/tml-coding-challenge.jar"]