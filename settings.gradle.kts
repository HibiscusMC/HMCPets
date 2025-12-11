rootProject.name = "hmcpets"
include("plugin")
include("api")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}