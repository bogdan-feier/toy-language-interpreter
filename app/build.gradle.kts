plugins {
    application
    checkstyle
    id("com.diffplug.spotless") version "8.5.1"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "interpreter.Main"
}

spotless {
    java {
        googleJavaFormat().aosp()
    }
}

checkstyle {
    toolVersion = "14.1.0"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
