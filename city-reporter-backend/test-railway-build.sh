#!/bin/bash

echo "🧪 Testing Railway deployment locally..."
echo ""

# Test 1: Check if required files exist
echo "✓ Checking required files..."
files=("nixpacks.toml" "railway.json" "Procfile" "gradlew")
for file in "${files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "❌ Missing file: $file"
        exit 1
    fi
    echo "  ✅ $file exists"
done

# Test 2: Check gradlew permissions
echo ""
echo "✓ Checking gradlew permissions..."
if [ -x "./gradlew" ]; then
    echo "  ✅ gradlew is executable"
else
    echo "  ⚠️  gradlew is not executable, fixing..."
    chmod +x ./gradlew
    echo "  ✅ Fixed gradlew permissions"
fi

# Test 3: Try to build
echo ""
echo "✓ Testing build..."
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "📦 JAR location: build/libs/city-reporter-backend-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "🚀 Ready for Railway deployment!"
    echo ""
    echo "Next steps:"
    echo "  1. Commit and push to GitHub"
    echo "  2. Railway will automatically deploy"
    echo "  3. Don't forget to set environment variables:"
    echo "     - SPRING_PROFILES_ACTIVE=prod"
    echo "     - JWT_SECRET=<your-secret>"
else
    echo ""
    echo "❌ Build failed! Fix errors before deploying to Railway."
    exit 1
fi
