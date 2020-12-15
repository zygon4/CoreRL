
plugins {
    application
    `java-library`
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    api("org.apache.commons:commons-math3:3.6.1")

    implementation("com.github.stewsters:stewsters-util:0.19")

    implementation("com.google.guava:guava:30.0-jre")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation("org.apache.commons:commons-math3:3.6.1")

    implementation("org.hexworks.zircon:zircon.core-jvm:2020.2.0-RELEASE")
    implementation("org.hexworks.zircon:zircon.jvm.swing:2020.2.0-RELEASE")
    implementation("org.hexworks.zircon:zircon.jvm.libgdx:2020.2.0-RELEASE")

    // Use JUnit test framework
    testImplementation("junit:junit:4.13")
}

application {
    mainClassName = "com.zygon.rl.game.example.BloodRLMain"
}
