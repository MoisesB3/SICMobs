plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.0.0"
}

group = "com.sic.mobs"
version = "1.0.28"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots/") // Purpur Maven repository
}

dependencies {
    implementation("com.mojang:authlib:1.5.25");  // Add this line
    implementation("org.purpurmc.purpur:purpur-api:1.21.3-R0.1-SNAPSHOT") // Update version if needed
}
