plugins {
    kotlin("jvm") version "2.2.20"
    application
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.gitguard"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
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
// Task principal para criar JAR executável com todas as dependências
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.gitguard.MainKt"
        )
    }

    // Incluir todas as dependências no JAR
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
}
tasks.register("distribution") {
    dependsOn("installDist")

    doLast {
        println(" Distribution created successfully!")
        println(" Executables location:")
        println("   - Linux/macOS: build/install/${project.name}/bin/${project.name}")
        println("   - Windows:     build/install/${project.name}/bin/${project.name}.bat")
        println("\n Add to PATH or copy to /usr/local/bin (Linux/macOS)")
    }
}

// Alias para facilitar
tasks.register("fatJar") {
    dependsOn("jar")
}