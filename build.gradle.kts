plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "com.gitguard"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")

    // For colored console output
    implementation("com.github.ajalt.mordant:mordant:2.3.0")

}
application {
    mainClass.set("com.gitguard.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
// Task para criar executável
tasks.register<Jar>("fatJar") {
    archiveBaseName.set("gitguard")
    archiveVersion.set("1.0.0")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.gitguard.MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}