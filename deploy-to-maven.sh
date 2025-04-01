#!/bin/bash

VERSION="2.0.1"
USERNAME="sangnguyen"
PASSWORD="@AbcSang070992"
STAGING_PROFILE_ID="31754de92605f9"

# Thư mục Maven local
MAVEN_REPO=~/.m2/repository/io/github/track-asia

# Function to deploy artifact to Maven Central
deploy_artifact() {
    local artifact_dir=$1
    local artifact_id=$2
    local group_id="io.github.track-asia"
    
    echo "Deploying $artifact_id to Maven Central..."
    
    # List of file extensions to deploy
    local extensions=("pom" "jar" "module" "klib" "javadoc.jar" "sources.jar" "metadata.jar" "kotlin-tooling-metadata.json")
    
    for ext in "${extensions[@]}"; do
        local file_path="$artifact_dir/$artifact_id-$VERSION.$ext"
        
        # Skip if file doesn't exist
        if [ ! -f "$file_path" ]; then
            continue
        fi
        
        # Verify that md5, sha1 and signature exist
        if [ ! -f "$file_path.md5" ] || [ ! -f "$file_path.sha1" ] || [ ! -f "$file_path.asc" ]; then
            echo "WARNING: Missing checksum or signature for $file_path, skipping..."
            continue
        fi
        
        echo "Uploading $file_path with checksums and signature..."
        
        # Upload to Maven Central using curl
        curl -v -u "$USERNAME:$PASSWORD" \
            --upload-file "$file_path" \
            "https://oss.sonatype.org/service/local/staging/deployByProfileId/$STAGING_PROFILE_ID/$group_id/${artifact_id//-//}/$VERSION/$artifact_id-$VERSION.$ext"
            
        # Upload MD5
        curl -v -u "$USERNAME:$PASSWORD" \
            --upload-file "$file_path.md5" \
            "https://oss.sonatype.org/service/local/staging/deployByProfileId/$STAGING_PROFILE_ID/$group_id/${artifact_id//-//}/$VERSION/$artifact_id-$VERSION.$ext.md5"
            
        # Upload SHA1
        curl -v -u "$USERNAME:$PASSWORD" \
            --upload-file "$file_path.sha1" \
            "https://oss.sonatype.org/service/local/staging/deployByProfileId/$STAGING_PROFILE_ID/$group_id/${artifact_id//-//}/$VERSION/$artifact_id-$VERSION.$ext.sha1"
            
        # Upload signature
        curl -v -u "$USERNAME:$PASSWORD" \
            --upload-file "$file_path.asc" \
            "https://oss.sonatype.org/service/local/staging/deployByProfileId/$STAGING_PROFILE_ID/$group_id/${artifact_id//-//}/$VERSION/$artifact_id-$VERSION.$ext.asc"
    done
}

# Deploy base artifacts
deploy_artifact "$MAVEN_REPO/turf/$VERSION" "turf"
deploy_artifact "$MAVEN_REPO/geojson/$VERSION" "geojson"

# Deploy platform-specific artifacts
for variant in turf-{iosarm64,iossimulatorarm64,iosx64,js,jvm,wasm-js} geojson-{iosarm64,iossimulatorarm64,iosx64,js,jvm,wasm-js}; do
    if [ -d "$MAVEN_REPO/$variant/$VERSION" ]; then
        deploy_artifact "$MAVEN_REPO/$variant/$VERSION" "$variant"
    fi
done

echo "Deployment completed. Now you need to log in to https://oss.sonatype.org/, go to Staging Repositories, and Release the repository." 