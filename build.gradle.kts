import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot")        version "2.5.5"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm")                         version "1.5.31"
  kotlin("plugin.spring")               version "1.5.31"
}

group   = "de.smartsteuer"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  implementation    (group = "org.springframework.boot",     name = "spring-boot-starter-actuator")
  implementation    (group = "org.springframework.boot",     name = "spring-boot-starter-thymeleaf")
  implementation    (group = "org.springframework.boot",     name = "spring-boot-starter-web")
  implementation    (group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin")
  implementation    (group = "org.jetbrains.kotlin",         name = "kotlin-reflect")
  implementation    (group = "org.jetbrains.kotlin",         name = "kotlin-stdlib-jdk8")
  implementation    (group = "com.google.guava",             name = "guava", version = "31.0.1-jre")
  implementation    (group = "org.springframework.cloud",    name = "spring-cloud-starter-consul-all")
  developmentOnly   (group = "org.springframework.boot",     name = "spring-boot-devtools")
  runtimeOnly       (group = "io.micrometer",                name = "micrometer-registry-prometheus")
  testImplementation(group = "org.springframework.boot",     name = "spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    exclude(module = "mockito-core")
  }
  testImplementation(group = "com.ninja-squad",              name = "springmockk", version = "3.0.1")
  testImplementation(group  = "io.kotest",                   name = "kotest-assertions-core-jvm",  version = "4.6.3") {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
  }
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.3")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
