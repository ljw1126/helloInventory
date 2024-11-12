plugins {
    id("org.sonarqube")
}

sonar {
    properties {
        property("sonar.organization", "leejinwoo1126")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
