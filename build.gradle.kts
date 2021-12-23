
plugins {
    application
    `java-library`
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    api("org.apache.commons:commons-math3:3.6.1")
    implementation("org.apache.commons:commons-text:1.9")

    implementation("com.github.stewsters:stewsters-util:0.19")

    implementation("com.google.guava:guava:30.0-jre")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation("commons-io:commons-io:2.8.0")

    implementation("org.hexworks.zircon:zircon.core-jvm:2021.1.0-RELEASE")
    implementation("org.hexworks.zircon:zircon.jvm.swing:2021.1.0-RELEASE")
    implementation("org.hexworks.zircon:zircon.jvm.libgdx:2021.1.0-RELEASE")

    // Use JUnit test framework
    // For SOME reason it's required to be on the regular compile classpath, not sure why
    implementation("junit:junit:4.13")
    testImplementation("junit:junit:4.13")
}

application {
    mainClassName = "com.zygon.rl.game.example.BloodRLMain"
}
