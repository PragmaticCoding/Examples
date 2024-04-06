plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

//group "ca.pragmaticcoding"
//version "1.0-SNAPSHOT"
val junitVersion = "5.10.0"
repositories {
    mavenCentral()
}

//tasks.withType(JavaCompile) {
//    options.encoding = "UTF-8"
//}

application {
    mainModule = "ca.pragmaticcoding.examples"
    mainClass = "ca.pragmaticcoding.examples.hexeditor.HexEditorApplication"
}

kotlin {
    jvmToolchain(21)
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    imageZip = project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip")
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "app"
    }
}

tasks.jlinkZip {
    group = "distribution"
}