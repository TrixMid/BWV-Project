#!/bin/bash

MAVEN_VERSION="3.9.12"
MAVEN_ZIP="apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/$MAVEN_ZIP"
WRAPPER_PROP=".mvn/wrapper/maven-wrapper.properties"
CERT_FILE="school-proxy.crt"
TRUSTSTORE="./school-truststore.jks"
TRUSTSTORE_PASS="changeit"

echo "Setting up Maven for restricted environment..."

if [ ! -f "pom.xml" ]; then
    echo "Error: pom.xml not found. Please run this script from the project root."
    exit 1
fi

ABS_PATH=$(pwd -W 2>/dev/null || pwd)
REPO_PATH="$ABS_PATH/.maven-repo"
mkdir -p "$REPO_PATH"
mkdir -p .mvn

# Set MVN_EXEC
export MVN_EXEC="$ABS_PATH/mvnw"
echo "MVN_EXEC set to: $MVN_EXEC"

# 1. Grab the proxy certificate
echo ""
echo "Fetching school proxy certificate from repo.maven.apache.org..."
openssl s_client -connect repo.maven.apache.org:443 -showcerts </dev/null 2>/dev/null \
    | openssl x509 -outform PEM > "$CERT_FILE"

if [ ! -s "$CERT_FILE" ]; then
    echo "Error: Failed to fetch certificate. Is openssl available?"
    exit 1
fi

echo "--- Certificate preview ---"
openssl x509 -in "$CERT_FILE" -text -noout | head -20
echo "---------------------------"

# 2. Try importing into Java's truststore (needs admin)
JAVA_BIN=$(which java)
JAVA_HOME_DETECTED=$(dirname $(dirname $(readlink -f "$JAVA_BIN" 2>/dev/null || echo "$JAVA_BIN")))
SYSTEM_CACERTS="$JAVA_HOME_DETECTED/lib/security/cacerts"

echo ""
echo "Attempting to import cert into system truststore: $SYSTEM_CACERTS"
keytool -importcert \
    -keystore "$SYSTEM_CACERTS" \
    -storepass "$TRUSTSTORE_PASS" \
    -alias school-proxy \
    -file "$CERT_FILE" \
    -noprompt 2>/dev/null

if [ $? -eq 0 ]; then
    echo "Successfully imported into system truststore!"
    TRUSTSTORE_ARGS=""
else
    echo "System truststore import failed (no admin rights) — using local truststore instead."

    # Delete old alias if it exists to avoid duplicate errors
    keytool -delete \
        -keystore "$TRUSTSTORE" \
        -storepass "$TRUSTSTORE_PASS" \
        -alias school-proxy 2>/dev/null

    keytool -importcert \
        -keystore "$TRUSTSTORE" \
        -storepass "$TRUSTSTORE_PASS" \
        -alias school-proxy \
        -file "$CERT_FILE" \
        -noprompt

    if [ $? -ne 0 ]; then
        echo "Error: Failed to create local truststore."
        exit 1
    fi

    echo "Local truststore created: $TRUSTSTORE"
    TRUSTSTORE_ARGS="-Djavax.net.ssl.trustStore=$ABS_PATH/school-truststore.jks -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASS"
fi

# 3. Write jvm.config
echo ""
echo "Writing .mvn/jvm.config..."
cat > .mvn/jvm.config << EOF
-Dmaven.wagon.http.ssl.insecure=true
-Dmaven.wagon.http.ssl.allowall=true
-Dmaven.wagon.http.ssl.ignore.validity.dates=true
-Dmaven.repo.local=$REPO_PATH
$TRUSTSTORE_ARGS
EOF

echo "--- jvm.config contents ---"
cat .mvn/jvm.config
echo "---------------------------"

# 4. Set MAVEN_OPTS as well (belt and suspenders)
export MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true $TRUSTSTORE_ARGS"

# 5. Download Maven zip
if [ ! -f "$MAVEN_ZIP" ]; then
    echo ""
    echo "Downloading Maven $MAVEN_VERSION..."
    curl -k -L "$MAVEN_URL" -o "$MAVEN_ZIP"
    if [ $? -ne 0 ]; then
        echo "Error: Failed to download Maven."
        exit 1
    fi
else
    echo "Maven zip already exists, skipping download."
fi

# 6. Update wrapper properties
if [ -f "$WRAPPER_PROP" ]; then
    echo ""
    echo "Updating $WRAPPER_PROP..."
    LOCAL_URL="file:///$ABS_PATH/$MAVEN_ZIP"
    tr -d '\r' < "$WRAPPER_PROP" \
        | sed "s|distributionUrl=.*|distributionUrl=$LOCAL_URL|" \
        > "$WRAPPER_PROP.tmp"
    echo "" >> "$WRAPPER_PROP.tmp"
    mv "$WRAPPER_PROP.tmp" "$WRAPPER_PROP"
    echo "--- wrapper properties ---"
    cat "$WRAPPER_PROP"
    echo "--------------------------"
fi

# 7. Verify
echo ""
echo "Verifying Maven wrapper..."
"$MVN_EXEC" --version || echo "Warning: Could not verify Maven version."

echo ""
echo "Setup complete! Run your project with:"
echo '  $MVN_EXEC spring-boot:run'