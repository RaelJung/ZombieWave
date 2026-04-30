plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.gamja.zombiewave"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

tasks.jar {
    from(sourceSets.main.get().resources)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    doLast {
        copy {
            from(archiveFile)
            into("C:\\Users\\a\\Documents\\my-server\\plugins")
        }
    }
}