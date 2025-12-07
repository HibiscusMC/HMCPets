plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

dependencies {
    // Paper dev-bundle
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    // HMCPets API
    implementation(project(":api"))

    // TriumphGUI
    implementation("dev.triumphteam:triumph-gui:3.1.13") {
        exclude("net.kyori")
        exclude("com.google.gson")
    }
    // Command-Flow
    implementation("me.fixeddev:commandflow-bukkit:0.6.0") {
        exclude("net.kyori")
    }
    // Inject
    implementation("team.unnamed:inject:2.0.1")

    // HibiscusCommons
    compileOnly("me.lojosho:HibiscusCommons:0.8.0-3c107b51")
    // Configurate
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")
    // H2
    compileOnly("com.h2database:h2:2.4.240")

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

tasks {
    processResources {
        filteringCharset = "UTF-8"

        filesMatching("plugin.yml") {
            expand("hmcpets" to rootProject)
        }
    }
}