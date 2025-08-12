plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

dependencies {
    // Paper dev-bundle
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    // TriumphGUI
    implementation("dev.triumphteam:triumph-gui:3.1.11") {
        exclude("net.kyori")
        exclude("com.google.gson")
    }
    // Command-Flow
    implementation("me.fixeddev:commandflow-bukkit:0.6.0") {
        exclude("net.kyori")
    }
    // Inject
    implementation("team.unnamed:inject:2.0.1")

    // Caffeine
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.2.0")
    // HibiscusCommons
    compileOnly("me.lojosho:HibiscusCommons:0.7.1-6c4e262c")
    // Configurate
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
    // H2
    compileOnly("com.h2database:h2:2.3.232")
}