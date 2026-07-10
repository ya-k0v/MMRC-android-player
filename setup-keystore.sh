#!/bin/bash
# Script to generate keystore and encode for GitHub Secrets

echo "=== Generating Keystore ==="
keytool -genkey -v \
  -keystore keystore.jks \
  -alias mmrc \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass mmrc123 \
  -keypass mmrc123 \
  -dname "CN=MMRC, OU=VideoControl, O=VideoControl, L=Moscow, ST=Moscow, C=RU"

echo ""
echo "=== Encoding for GitHub Secrets ==="
echo "Add these secrets to your GitHub repository:"
echo ""
echo "KEYSTORE_BASE64:"
base64 -i keystore.jks | tr -d '\n'
echo ""
echo ""
echo "KEYSTORE_PASSWORD: mmrc123"
echo "KEY_ALIAS: mmrc"
echo "KEY_PASSWORD: mmrc123"
echo ""
echo "=== Done ==="
