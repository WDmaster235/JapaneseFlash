pluginManagement {
    repositories {
        google()  // Add this to include Google repository
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Ensure that project-level repositories are ignored
    repositories {
        google()  // Add this here as well
        mavenCentral()
    }
}

rootProject.name = "JapaneseFlash"
include(":app")
include(":app:libs")
