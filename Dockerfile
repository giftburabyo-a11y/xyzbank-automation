# Base image with Java 17 + Maven
FROM maven:3.9.6-eclipse-temurin-17

# Install Google Chrome using the NEW correct method
# apt-key is deprecated — we now use /etc/apt/keyrings/ instead
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    ca-certificates \
    curl \
    --no-install-recommends \
    && install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
       | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg \
    && chmod a+r /etc/apt/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] \
       http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Verify Chrome installed correctly
RUN google-chrome --version

# Set working directory
WORKDIR /app

# Copy pom first — Docker caches dependency downloads if pom unchanged
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Run all tests headlessly when container starts
CMD ["mvn", "test", "-Dheadless=true", "-B"]