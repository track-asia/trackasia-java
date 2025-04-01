# Định nghĩa biến môi trường cho GPG
export GPG_TTY := $(shell tty)

# Cập nhật định nghĩa lệnh GPG với các tùy chọn cụ thể hơn
GPG_CMD = gpg --batch --yes --armor --detach-sign --digest-algo=SHA512 --no-tty
ifdef SIGNING_KEY_PATH
    GPG_CMD += --no-default-keyring --keyring $(SIGNING_KEY_PATH)
endif
ifdef GPG_KEY_ID
    GPG_CMD += --local-user $(GPG_KEY_ID)
endif
ifdef GPG_PASSPHRASE
    GPG_CMD += --pinentry-mode loopback --passphrase "$(GPG_PASSPHRASE)"
endif

# Tắt tty nếu chạy trong môi trường CI
ifeq ($(CI),true)
    export GPG_TTY = /dev/null
endif

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
	./gradlew :services-core:javadoc; mv services-core/build/docs/javadoc/ ./documentation/core/javadoc/ ; \
	./gradlew :services-geojson:javadoc; mv services-geojson/build/docs/javadoc/ ./documentation/geojson/javadoc/ ; \
	./gradlew :services-turf:javadoc; mv services-turf/build/docs/javadoc/ ./documentation/turf/javadoc/ ; \
	./gradlew :services:javadoc; mv services/build/docs/javadoc/ ./documentation/services/javadoc/ ; \

local-publish:
	./gradlew -PVERSION="$(VERSION)" \
		:services-geojson:publishToMavenLocal \
		:services-turf:publishToMavenLocal \
		publishToMavenLocal

cleanup-metadata:
	find ~/.m2/repository/io/github/track-asia -name "*maven-metadata-local.xml*" -delete
	find ~/.m2/repository/io/github/track-asia -name "*metadata-local*" -delete
	@echo "Cleaned up local metadata files"

prepare-publish: javadoc local-publish cleanup-metadata checksums-turf checksums-geojson
	@echo "All files are prepared for publishing"

clean:
	./gradlew clean

checksums-turf: checksums-turf-base checksums-turf-ios checksums-turf-js checksums-turf-jvm checksums-turf-wasm-js

checksums-geojson: checksums-geojson-base checksums-geojson-ios checksums-geojson-js checksums-geojson-jvm checksums-geojson-wasm-js

checksums-turf-base:
	cd ~/.m2/repository/io/github/track-asia/turf/$(VERSION)/ && \
	for file in turf-$(VERSION).{pom,module,jar} turf-$(VERSION)-{javadoc,sources}.jar turf-$(VERSION)-kotlin-tooling-metadata.json; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			rm -f $$file.asc && \
			$(GPG_CMD) $$file; \
			gpg --verify $$file.asc $$file || echo "WARNING: Invalid signature for $$file"; \
		fi; \
	done

checksums-geojson-base:
	cd ~/.m2/repository/io/github/track-asia/geojson/$(VERSION)/ && \
	for file in geojson-$(VERSION).{pom,module,jar} geojson-$(VERSION)-{javadoc,sources}.jar geojson-$(VERSION)-kotlin-tooling-metadata.json; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi; \
	done

checksums-turf-ios: checksums-turf-iosarm64 checksums-turf-iossimulatorarm64 checksums-turf-iosx64

checksums-turf-iosarm64:
	cd ~/.m2/repository/io/github/track-asia/turf-iosarm64/$(VERSION)/ && \
	for file in turf-iosarm64-$(VERSION).{pom,module,klib} turf-iosarm64-$(VERSION)-{javadoc,sources,metadata}.jar turf-iosarm64-$(VERSION)-kotlin-tooling-metadata.json; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-turf-iossimulatorarm64:
	cd ~/.m2/repository/io/github/track-asia/turf-iossimulatorarm64/$(VERSION)/ && \
	for file in turf-iossimulatorarm64-$(VERSION).{pom,module,klib} turf-iossimulatorarm64-$(VERSION)-{javadoc,sources,metadata}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-turf-iosx64:
	cd ~/.m2/repository/io/github/track-asia/turf-iosx64/$(VERSION)/ && \
	for file in turf-iosx64-$(VERSION).{pom,module,klib} turf-iosx64-$(VERSION)-{javadoc,sources,metadata}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-turf-js:
	cd ~/.m2/repository/io/github/track-asia/turf-js/$(VERSION)/ && \
	for file in turf-js-$(VERSION).{pom,module,klib} turf-js-$(VERSION)-{javadoc,sources}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-turf-jvm:
	cd ~/.m2/repository/io/github/track-asia/turf-jvm/$(VERSION)/ && \
	for file in turf-jvm-$(VERSION).{pom,module,jar} turf-jvm-$(VERSION)-{javadoc,sources}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-turf-wasm-js:
	cd ~/.m2/repository/io/github/track-asia/turf-wasm-js/$(VERSION)/ && \
	for file in turf-wasm-js-$(VERSION).{pom,module,klib} turf-wasm-js-$(VERSION)-{javadoc,sources}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-turf: checksums-turf-base checksums-turf-ios checksums-turf-js checksums-turf-jvm checksums-turf-wasm-js

checksums-geojson-ios: checksums-geojson-iosarm64 checksums-geojson-iossimulatorarm64 checksums-geojson-iosx64

checksums-geojson-iosarm64:
	cd ~/.m2/repository/io/github/track-asia/geojson-iosarm64/$(VERSION)/ && \
	for file in geojson-iosarm64-$(VERSION).{pom,module,klib} geojson-iosarm64-$(VERSION)-{javadoc,sources,metadata}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-geojson-iossimulatorarm64:
	cd ~/.m2/repository/io/github/track-asia/geojson-iossimulatorarm64/$(VERSION)/ && \
	for file in geojson-iossimulatorarm64-$(VERSION).{pom,module,klib} geojson-iossimulatorarm64-$(VERSION)-{javadoc,sources,metadata}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

checksums-geojson-iosx64:
	cd ~/.m2/repository/io/github/track-asia/geojson-iosx64/$(VERSION)/ && \
	for file in geojson-iosx64-$(VERSION).{pom,module,klib} geojson-iosx64-$(VERSION)-{javadoc,sources,metadata}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

# JS variants
checksums-geojson-js:
	cd ~/.m2/repository/io/github/track-asia/geojson-js/$(VERSION)/ && \
	for file in geojson-js-$(VERSION).{pom,module,klib} geojson-js-$(VERSION)-{javadoc,sources}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

# JVM variant
checksums-geojson-jvm:
	cd ~/.m2/repository/io/github/track-asia/geojson-jvm/$(VERSION)/ && \
	for file in geojson-jvm-$(VERSION).{pom,module,jar} geojson-jvm-$(VERSION)-{javadoc,sources}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

# WASM JS variant
checksums-geojson-wasm-js:
	cd ~/.m2/repository/io/github/track-asia/geojson-wasm-js/$(VERSION)/ && \
	for file in geojson-wasm-js-$(VERSION).{pom,module,klib} geojson-wasm-js-$(VERSION)-{javadoc,sources}.jar; do \
		if [ -f $$file ]; then \
			md5sum $$file | cut -d ' ' -f 1 > $$file.md5 && \
			sha1sum $$file | cut -d ' ' -f 1 > $$file.sha1 && \
			$(GPG_CMD) $$file; \
		fi \
	done

check-keyfile:
	@echo "Checking key file..."
	gpg --batch --import $(SIGNING_KEY_PATH)
	gpg --list-secret-keys --keyid-format LONG
