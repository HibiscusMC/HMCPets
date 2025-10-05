plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

dependencies {
    // Paper dev-bundle
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    // Lombok
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    compileOnly("org.projectlombok:lombok:1.18.42")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}