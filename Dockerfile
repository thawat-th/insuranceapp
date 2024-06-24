# Use Amazon Corretto as the base image
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file to the container
COPY target/insuranceapp-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
