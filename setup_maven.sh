#!/bin/bash

# 1. Define variables
MAVEN_VERSION="3.9.12"
MAVEN_ZIP="apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/$MAVEN_ZIP"
WRAPPER_PROP=".mvn/wrapper/maven-wrapper.properties"

echo "Setting up Maven for restricted environment..."

# 2. Download the zip manually using curl (with -k to bypass SSL if needed)
if [ ! -f "$MAVEN_ZIP" ]; then
    echo "Downloading Maven $MAVEN_VERSION..."
    curl -k -L "$MAVEN_URL" -o "$MAVEN_ZIP"
else
    echo "Maven zip already exists, skipping download."
fi

# 3. Get the absolute path for the zip (Windows style for the properties file)
# Git Bash 'pwd -W' gives the C:/... style path Maven needs
ABS_PATH=$(pwd -W)
LOCAL_URL="file:///$ABS_PATH/$MAVEN_ZIP"

# 4. Update the wrapper properties to point to the local file
if [ -f "$WRAPPER_PROP" ]; then
    echo "Updating $WRAPPER_PROP to use local zip..."
    # Use sed to replace the distributionUrl line
    sed -i "s|distributionUrl=.*|distributionUrl=$LOCAL_URL|" "$WRAPPER_PROP"
    echo "Done!"
else
    echo "Error: $WRAPPER_PROP not found. Are you in the project root?"
    exit 1
fi

# 5. Run the version check
HOME=$(pwd) ./mvnw --version
