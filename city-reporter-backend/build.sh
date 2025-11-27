#!/bin/bash

echo "🔨 Building City Reporter Backend..."

# Clean previous build
./gradlew clean

# Build the project
./gradlew build -x test

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "📦 JAR location: build/libs/city-reporter-backend-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "🚀 To run locally:"
    echo "   java -jar build/libs/city-reporter-backend-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "🌍 To run with production profile:"
    echo "   java -jar -Dspring.profiles.active=prod build/libs/city-reporter-backend-0.0.1-SNAPSHOT.jar"
else
    echo "❌ Build failed!"
    exit 1
fi
