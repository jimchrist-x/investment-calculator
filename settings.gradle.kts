rootProject.name = "investment-calculator"

// Lets Gradle download the JDK 21 toolchain requested in build.gradle.kts when it is not
// installed locally, instead of failing with "No matching toolchains found".
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
