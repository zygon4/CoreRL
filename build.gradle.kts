
plugins {
    // Apply the java-library plugin to add support for Java Library
    `java-library`
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    api("org.apache.commons:commons-math3:3.6.1")

    implementation("com.google.guava:guava:30.0-jre")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation("org.hexworks.zircon:zircon.core-jvm:2020.1.8-PREVIEW")
    implementation("org.hexworks.zircon:zircon.jvm.swing:2020.1.8-PREVIEW")

    // Use JUnit test framework
    testImplementation("junit:junit:4.13")
}
