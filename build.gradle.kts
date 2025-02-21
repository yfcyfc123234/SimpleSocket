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
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}