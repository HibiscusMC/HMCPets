plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "com.hibiscusmc"
version = version("0.1.0")

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenCentral()

        // Command-Flow Repo
        maven("https://repo.unnamed.team/repository/unnamed-releases/")
        // HibiscusCommons
        maven("https://repo.hibiscusmc.com/releases/")
    }

    dependencies {
        // Lombok
        annotationProcessor("org.projectlombok:lombok:1.18.36")
        compileOnly("org.projectlombok:lombok:1.18.36")
    }

    tasks {
        processResources {
            filteringCharset = "UTF-8"

            filesMatching("plugin.yml") {
                expand("hmcpets" to rootProject)
            }
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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

        archiveFileName.set("HMCPets-${version}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        downloadPlugins {
            url("https://repo.hibiscusmc.com/releases/me/lojosho/HibiscusCommons/0.6.4/HibiscusCommons-0.6.4.jar")
            url("https://ci.dmulloy2.net/job/ProtocolLib/lastStableBuild/artifact/build/libs/ProtocolLib.jar")
            url("https://download.luckperms.net/1595/bukkit/loader/LuckPerms-Bukkit-5.5.10.jar")
        }

        minecraftVersion("1.21.4")
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
        else ""
    } catch (e: Exception) {
        "no-commit"
    }
}

enum class VersionType {
    RELEASE,
    DEVELOPMENT
}