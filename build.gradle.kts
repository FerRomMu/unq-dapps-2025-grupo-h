plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("plugin.serialization") version "1.9.25"
	jacoco
}

group = "unq.dda"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// -------------------------------------------------------------------------
	// SPRING BOOT CORE & WEB
	// Essential Spring Boot starters for building web applications and core features.
	// -------------------------------------------------------------------------
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// -------------------------------------------------------------------------
	// SECURITY & AUTHENTICATION
	// Spring Security components for securing your application, including OAuth2.
	// -------------------------------------------------------------------------
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	// -------------------------------------------------------------------------
	// DATA PERSISTENCE & CACHING
	// Dependencies for database interaction (JPA) and caching mechanisms.
	// -------------------------------------------------------------------------
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.hsqldb:hsqldb:2.7.2")

	// -------------------------------------------------------------------------
	// VALIDATION & API DOCUMENTATION
	// For input validation and generating OpenAPI (Swagger) documentation.
	// -------------------------------------------------------------------------
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// -------------------------------------------------------------------------
	// KOTLIN COROUTINES & SERIALIZATION
	// Libraries for asynchronous programming with Kotlin Coroutines and JSON serialization.
	// -------------------------------------------------------------------------
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

	// -------------------------------------------------------------------------
	// WEB SCRAPING & AUTOMATION (Selenium/Jsoup)
	// Dependencies for interacting with web pages, likely for scraping or browser automation.
	// -------------------------------------------------------------------------
	implementation("org.jsoup:jsoup:1.17.2")
	implementation("org.seleniumhq.selenium:selenium-java:4.31.0")

	// -------------------------------------------------------------------------
	// DEVELOPMENT TOOLS
	// Tools that are useful during development but not needed in production.
	// -------------------------------------------------------------------------
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// -------------------------------------------------------------------------
	// TESTING
	// Libraries for unit, integration, and security testing.
	// -------------------------------------------------------------------------
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core:5.3.1")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}