plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.yfc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") // 添加协程核心库
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1") // 如果需要支持 JDK 8 的协程

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
//    implementation("org.slf4j:slf4j-simple:2.0.17")
// https://mvnrepository.com/artifact/org.slf4j/slf4j-reload4j
    implementation("org.slf4j:slf4j-reload4j:2.0.17")

//    // https://poi.apache.org/components/document/index.html
//    api("org.apache.poi:poi:5.4.1")
//    api("org.apache.poi:poi-ooxml:5.4.1") { exclude(group = "org.apache.poi", module = "poi-ooxml-lite") }
//    api("org.apache.poi:poi-ooxml-full:5.4.1")
////    api 'org.apache.poi:poi-examples:5.4.1'

    // JAXB-based Java library for Word docx, Powerpoint pptx, and Excel xlsx files
    // https://github.com/plutext/docx4j
    implementation("org.docx4j:docx4j-JAXB-MOXy:11.5.4")

    // https://mvnrepository.com/artifact/org.docx4j/docx4j-export-fo
    implementation("org.docx4j:docx4j-export-fo:11.5.4")

    // Gson is a Java library that can be used to convert Java Objects into their JSON representation.
    // It can also be used to convert a JSON string to an equivalent Java object.
    // Gson can work with arbitrary Java objects including pre-existing objects that you do not have source-code of.
    // https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.13.1")

    // https://doc.hutool.cn/pages/index/#exclude%E6%96%B9%E5%BC%8F
    implementation("cn.hutool:hutool-all:5.8.26")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}