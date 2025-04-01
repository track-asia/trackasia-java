#!/bin/bash

VERSION="2.0.1"
GPG_KEY_ID="A0455BA3"

# Cấu hình thư mục Maven local
MAVEN_REPO=~/.m2/repository/io/github/track-asia

# Hàm ký và tạo checksums cho một file
sign_file() {
    local file=$1
    echo "Signing and creating checksums for: $file"
    
    # Tạo md5
    md5sum "$file" | cut -d ' ' -f 1 > "$file.md5"
    echo "Created MD5: $file.md5"
    
    # Tạo sha1
    sha1sum "$file" | cut -d ' ' -f 1 > "$file.sha1"
    echo "Created SHA1: $file.sha1"
    
    # Ký file với passphrase
    gpg --batch --yes --no-tty --pinentry-mode=loopback --passphrase "secret" --armor --detach-sign --digest-algo=SHA512 --local-user "$GPG_KEY_ID" "$file"
    
    if [ -f "$file.asc" ]; then
        echo "Created signature: $file.asc"
    else
        echo "Failed to create signature for $file"
    fi
}

# Xử lý thư viện turf
TURF_DIR="$MAVEN_REPO/turf/$VERSION"
if [ -d "$TURF_DIR" ]; then
    echo "Processing turf library..."
    for file in "$TURF_DIR"/turf-$VERSION.{pom,module,jar} "$TURF_DIR"/turf-$VERSION-{javadoc,sources}.jar "$TURF_DIR"/turf-$VERSION-kotlin-tooling-metadata.json; do
        if [ -f "$file" ]; then
            sign_file "$file"
        fi
    done
fi

# Xử lý thư viện geojson
GEOJSON_DIR="$MAVEN_REPO/geojson/$VERSION"
if [ -d "$GEOJSON_DIR" ]; then
    echo "Processing geojson library..."
    for file in "$GEOJSON_DIR"/geojson-$VERSION.{pom,module,jar} "$GEOJSON_DIR"/geojson-$VERSION-{javadoc,sources}.jar "$GEOJSON_DIR"/geojson-$VERSION-kotlin-tooling-metadata.json; do
        if [ -f "$file" ]; then
            sign_file "$file"
        fi
    done
fi

# Xử lý các biến thể khác
for variant in turf-{iosarm64,iossimulatorarm64,iosx64,js,jvm,wasm-js} geojson-{iosarm64,iossimulatorarm64,iosx64,js,jvm,wasm-js}; do
    VARIANT_DIR="$MAVEN_REPO/$variant/$VERSION"
    if [ -d "$VARIANT_DIR" ]; then
        echo "Processing $variant library..."
        for file in "$VARIANT_DIR"/$variant-$VERSION.{pom,module,jar,klib} "$VARIANT_DIR"/$variant-$VERSION-{javadoc,sources,metadata}.jar; do
            if [ -f "$file" ]; then
                sign_file "$file"
            fi
        done
    fi
done

echo "All artifacts have been signed and checksums created." 