plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

allprojects {
    group = "com.hibiscusmc.hmcpets"
    version = version("0.1.0")

    repositories {
        mavenCentral()

        // Command-Flow
        maven("https://repo.unnamed.team/repository/unnamed-releases/")
        // PlaceholderAPI
        maven("https://repo.extendedclip.com/releases/")
        // HibiscusCommons
        maven("https://repo.hibiscusmc.com/releases/")
    }
}

dependencies {
    implementation(project(":plugin"))
}

tasks {
    shadowJar {
        dependsOn(clean)

        val main = "${rootProject.group}.${rootProject.name}.libs"

        relocate("dev.triumphteam.gui", "$main.gui")
        relocate("me.fixeddev.commandflow", "$main.commandflow")

        archiveFileName.set("HMCPets-${version}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        downloadPlugins {
            url("https://repo.hibiscusmc.com/releases/me/lojosho/HibiscusCommons/0.8.0-3c107b51/HibiscusCommons-0.8.0-3c107b51.jar")
            url("https://github.com/dmulloy2/ProtocolLib/releases/download/5.4.0/ProtocolLib.jar")
            url("https://download.luckperms.net/1605/bukkit/loader/LuckPerms-Bukkit-5.5.16.jar")
        }

        minecraftVersion("1.21.8")
    }
}

fun version(ver: String, type: VersionType? = VersionType.DEVELOPMENT): String {
    return ver + if (type == VersionType.DEVELOPMENT) {
        "-dev." + fetchCommit()
    } else ""
}

fun fetchCommit(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .redirectErrorStream(true)
            .start()

        val hash = process.inputStream
            .bufferedReader().use { it.readLine().trim() }

        if (hash.startsWith("fatal:")) throw Exception()
        else hash
    } catch (e: Exception) {
        "no-commit"
    }
}

enum class VersionType {
    RELEASE,
    DEVELOPMENT
}