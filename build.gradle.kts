
plugins {
    application
    `java-library`
    `maven-publish`
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    //jcenter()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    api("org.apache.commons:commons-math3:3.6.1")
    implementation("org.apache.commons:commons-text:1.13.0")

    implementation("com.github.stewsters:stewsters-util:0.20")

    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("com.google.code.gson:gson:2.12.1")

    implementation("commons-io:commons-io:2.18.0")

    implementation("org.hexworks.zircon:zircon.core-jvm:2021.1.0-RELEASE")
    implementation("org.hexworks.zircon:zircon.jvm.swing:2021.1.0-RELEASE")
    implementation("org.hexworks.zircon:zircon.jvm.libgdx:2021.1.0-RELEASE")

    // Use JUnit test framework
    // For SOME reason it's required to be on the regular compile classpath, not sure why
    implementation("junit:junit:4.13")
    testImplementation("junit:junit:4.13")
}

application {
    mainClass = "com.zygon.rl.game.example.BloodRLMain"
}


java {
    withJavadocJar()
    withSourcesJar()
}

group = "com.lds.blood.core"
version = "0.1"

val javaComponent = components["java"] as AdhocComponentWithVariants

publishing {
    publications {
        // Use: ./gradlew publishToMavenLocal
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            afterEvaluate {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }
        }
    }
}
