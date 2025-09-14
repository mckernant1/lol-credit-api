plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mckernant1.lol"
version = "0.0.1-SNAPSHOT"
description = "Lol community credit scrore application"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
    maven(uri("https://mvn.mckernant1.com/release"))
}

configurations.implementation {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude("org.springframework.grpc", "spring-boot-starter-logging")
    exclude(group = "ch.qos.logback")
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Utils
    implementation("com.mckernant1.commons:kotlin-utils:0.2.1")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.33.8"))
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    // Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
