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
        mavenLocal()

        // ACF
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")

        // PlaceholderAPI
        maven("https://repo.extendedclip.com/releases/")
        // HibiscusCommons
        maven("https://repo.hibiscusmc.com/releases/")

        //Nexo
        maven("https://repo.nexomc.com/releases")

        maven("https://mvn.lumine.io/repository/maven-public/")

        //MythicMobs
        maven("https://mvn.lumine.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":plugin"))

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    compileOnly("com.nexomc:nexo:1.15")

    // Inject
    implementation("team.unnamed:inject:2.0.1")

    implementation("dev.triumphteam:triumph-gui:3.1.13") {
        exclude("net.kyori")
        exclude("com.google.gson")
    }

    //ModelEngine
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.4")

    //MythicMobs
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
}

tasks {
    shadowJar {
        dependsOn(jar)

        val main = "${rootProject.group}.libs"

        relocate("dev.triumphteam.gui", "$main.gui")
        relocate("co.aikar.commands", "$main.acf")
        relocate("co.aikar.locales", "$main.acf.locales")
        relocate("team.unnamed.inject", "$main.inject")

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
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
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