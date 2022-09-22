pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://maven.appspector.com/artifactory/android-sdk")
        maven(url = "https://oss.jfrog.org/libs-snapshot")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "xx Messenger"

include(":app")

// Feature modules
include(":feature:registration")

// Data modules
include(":data:session")

// Core modules
include(":core:ui")
include(":core:preferences")

// Resolve dependencies across modules
include(":depconstraints")

// Cmix/Client modules
include(":xxclient")
project(":xxclient").projectDir = File("elixxir-dapps-sdk-kotlin/xxclient")

include(":cmix")
project(":cmix").projectDir = File("elixxir-dapps-sdk-kotlin/cmix")

// Protobuf
include(":proto")