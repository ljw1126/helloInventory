plugins {
    java
    idea
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()

    testLogging.events("passed", "skipped", "failed")

    maxHeapSize = "2g"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
    testRuntimeOnly("org.junit.platform:junit-platform-commons:_") // Ensure platform commons is included

    // Mockito
    testImplementation("org.mockito:mockito-core:_")
    testImplementation("org.mockito:mockito-junit-jupiter:_")

    project.afterEvaluate {
        if(project.pluginManager.hasPlugin("custom.spring-conventions")) {
            testImplementation(Spring.boot.test)
        }
    }
}
