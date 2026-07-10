#!/bin/bash
# Generate secure keystore for APK signing

echo "=== Generating Keystore ==="

# Generate random passwords
STORE_PASS=$(openssl rand -base64 32 | tr -dc 'a-zA-Z0-9' | head -c 20)
KEY_PASS=$(openssl rand -base64 32 | tr -dc 'a-zA-Z0-9' | head -c 20)
KEY_ALIAS="mmrc-$(openssl rand -hex 4)"

# Generate keystore
keytool -genkey -v \
  -keystore keystore.jks \
  -alias $KEY_ALIAS \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass $STORE_PASS \
  -keypass $KEY_PASS \
  -dname "CN=MMRC, OU=VideoControl, O=VideoControl, L=Moscow, ST=Moscow, C=RU"

# Encode for GitHub
STORE_BASE64=$(base64 -i keystore.jks | tr -d '\n')

echo ""
echo "=== Add these secrets to GitHub repo → Settings → Secrets ==="
echo ""
echo "KEYSTORE_BASE64=$STORE_BASE64"
echo ""
echo "KEYSTORE_PASSWORD=$STORE_PASS"
echo "KEY_ALIAS=$KEY_ALIAS"
echo "KEY_PASSWORD=$KEY_PASS"
echo ""
echo "=== Keep these passwords safe! ==="
