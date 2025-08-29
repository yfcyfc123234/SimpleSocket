plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.yfc"
version = "1.0-SNAPSHOT"

tasks.named<Jar>("jar") {
    archiveBaseName.set("docx-util")
    archiveVersion.set(version.toString())

    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to "test.docx.DocxUtil",
            "Implementation-Version" to version,
        )
    }
    from("src/main/resources") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

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

    // JAXB-based Java library for Word docx, PowerPoint pptx, and Excel xlsx files
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

    // https://poi.apache.org/components/document/index.html
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3") { exclude(group = "org.apache.poi", module = "poi-ooxml-lite") }
    implementation("org.apache.poi:poi-ooxml-full:5.2.3")
    implementation("org.apache.poi:poi-examples:5.2.3")
// https://mvnrepository.com/artifact/org.apache.poi/poi-scratchpad
    implementation("org.apache.poi:poi-scratchpad:5.2.3")

    // https://mvnrepository.com/artifact/fr.opensagres.xdocreport/fr.opensagres.poi.xwpf.converter.pdf
    implementation("fr.opensagres.xdocreport:fr.opensagres.poi.xwpf.converter.pdf:2.1.0")
    // https://mvnrepository.com/artifact/fr.opensagres.xdocreport/fr.opensagres.xdocreport.itext.extension
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.itext.extension:2.1.0")

    // HTTP is the way modern applications network. It’s how we exchange data & media. Doing HTTP efficiently makes your stuff load faster and saves bandwidth.
    // OkHttp is an HTTP client that’s efficient by default:
    // HTTP/2 support allows all requests to the same host to share a socket.
    // Connection pooling reduces request latency (if HTTP/2 isn’t available).
    // Transparent GZIP shrinks download sizes.
    // Response caching avoids the network completely for repeat requests.
    // OkHttp perseveres when the network is troublesome: it will silently recover from common connection problems.
    // If your service has multiple IP addresses, OkHttp will attempt alternate addresses if the first connect fails.
    // This is necessary for IPv4+IPv6 and services hosted in redundant data centers.
    // OkHttp supports modern TLS features (TLS 1.3, ALPN, certificate pinning). It can be configured to fall back for broad connectivity.
    // Using OkHttp is easy. Its request/response API is designed with fluent builders and immutability.
    // It supports both synchronous blocking calls and async calls with callbacks.
    // https://github.com/square/okhttp
    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.1.0"))
    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.okhttp3:okhttp-sse")
    testImplementation("com.squareup.okhttp3:mockwebserver")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}