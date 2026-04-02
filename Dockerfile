# Start with Amazon Corretto 17 Alpine base image
FROM amazoncorretto:17-alpine

# Install curl on Alpine
RUN apk update && apk add --no-cache curl

# Copy the jar file into the image
COPY /app/target/SP2-1.0-shaded.jar /app/app.jar

# Expose the port your app runs on
EXPOSE 9191

# Command to run your app
CMD ["java", "-jar", "/app.jar"]