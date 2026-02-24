# Base image already has Java 17 + Maven installed
FROM maven:3.9.6-eclipse-temurin-17

# Install Google Chrome for headless Selenium tests
RUN apt-get update && apt-get install -y wget gnupg \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
       >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# All project files go in /app inside the container
WORKDIR /app

# Copy pom.xml first so Maven dependency downloads are cached
# If pom.xml hasn't changed Docker reuses this layer → faster builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# When container runs → execute all tests in headless Chrome
CMD ["mvn", "test", "-Dheadless=true", "-B"]