VERSION := $(shell cat VERSION)
checkstyle:
	./gradlew checkstyleMain

test:
	./gradlew test -i

build-release:
	./gradlew assemble

build-cli:
	./gradlew shadowJar

javadoc:
	mkdir documentation
	mkdir documentation/core/
	mkdir documentation/geojson/
	mkdir documentation/turf/
	mkdir documentation/services/
	./gradlew :services-geojson:javadoc; mv services-geojson/build/docs/javadoc/ ./documentation/geojson/javadoc/ ; \
	./gradlew :services-turf:javadoc; mv services-turf/build/docs/javadoc/ ./documentation/turf/javadoc/ ; \

local-publish:
	./gradlew -PVERSION=$(VERSION) publishToMavenLocal


prepare-publish: javadoc local-publish checksums-tuft checksums-geojson
	@echo "All files are prepared for publishing"

checksums-tuft:
	cd ~/.m2/repository/io/github/track-asia/android-sdk-turf/$(VERSION)/ && \
	md5sum android-sdk-turf-$(VERSION).pom | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION).pom.md5 && \
	md5sum android-sdk-turf-$(VERSION).jar | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION).jar.md5 && \
	md5sum android-sdk-turf-$(VERSION)-sources.jar | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION)-sources.jar.md5 && \
	md5sum android-sdk-turf-$(VERSION).module | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION).module.md5 && \
	md5sum android-sdk-turf-$(VERSION)-javadoc.jar | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION)-javadoc.jar.md5 && \
	sha1sum android-sdk-turf-$(VERSION).pom | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION).pom.sha1 && \
	sha1sum android-sdk-turf-$(VERSION).jar | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION).jar.sha1 && \
	sha1sum android-sdk-turf-$(VERSION).module | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION).module.sha1 && \
	sha1sum android-sdk-turf-$(VERSION)-javadoc.jar | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION)-javadoc.jar.sha1 && \
	sha1sum android-sdk-turf-$(VERSION)-sources.jar | cut -d ' ' -f 1 > android-sdk-turf-$(VERSION)-sources.jar.sha1

checksums-geojson:
	cd ~/.m2/repository/io/github/track-asia/android-sdk-geojson/$(VERSION)/ && \
	md5sum android-sdk-geojson-$(VERSION).pom | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION).pom.md5 && \
	md5sum android-sdk-geojson-$(VERSION).jar | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION).jar.md5 && \
	md5sum android-sdk-geojson-$(VERSION)-sources.jar | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION)-sources.jar.md5 && \
	md5sum android-sdk-geojson-$(VERSION).module | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION).module.md5 && \
	md5sum android-sdk-geojson-$(VERSION)-javadoc.jar | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION)-javadoc.jar.md5 && \
	sha1sum android-sdk-geojson-$(VERSION).pom | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION).pom.sha1 && \
	sha1sum android-sdk-geojson-$(VERSION).jar | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION).jar.sha1 && \
	sha1sum android-sdk-geojson-$(VERSION).module | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION).module.sha1 && \
	sha1sum android-sdk-geojson-$(VERSION)-javadoc.jar | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION)-javadoc.jar.sha1 && \
	sha1sum android-sdk-geojson-$(VERSION)-sources.jar | cut -d ' ' -f 1 > android-sdk-geojson-$(VERSION)-sources.jar.sha1


publish:
	./gradlew publishReleasePublicationToSonatypeRepository closeAndReleaseSonatypeStagingRepository

clean:
	./gradlew clean
