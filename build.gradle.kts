import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jsonschema2pojo.gradle.GenerateJsonSchemaJavaTask

val jaxb: Configuration by configurations.creating

plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("org.jsonschema2pojo") version "1.2.1"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "actions.github.cctray"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime")
    jaxb("org.glassfish.jaxb:jaxb-xjc:2.3.3")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    implementation("org.jsonschema2pojo:jsonschema2pojo-core:1.2.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    dependsOn("generateJaxb")
    dependsOn("generateJsonSchema2Pojo")
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    main {
        java {
//            srcDir("${projectDir}/src/main/kotlin")
            srcDir("${buildDir}/generated/src/main/kotlin")
        }
    }
}

tasks {
    register<JavaExec>("generateJaxb") {
        group = "Code Generation"
        description = "Generates Java classes from XSD using JAXB"

        val outputDir = file("build/generated/src/main/kotlin").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val packageName = "actions.github.cctray.githubactionscctray.model"
        val xsdDir = file("src/main/resources/xsd")

        classpath = jaxb
        mainClass.set("com.sun.tools.xjc.XJCFacade")
        args(
            xsdDir,
            "-d", outputDir,
            "-p", packageName,
            "-extension"
        )
    }
}

jsonSchema2Pojo {
    sourceFiles = listOf(file("src/main/resources/json/list-workflow-runs.json"))
    targetDirectory = file("${buildDir}/generated/src/main/kotlin")
    targetPackage = "actions.github.cctray.githubactionscctray.model"
}
