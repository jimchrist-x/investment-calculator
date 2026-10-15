plugins {
  application
}
java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(22))
  }
}

repositories {
  mavenCentral()
}

application {
  mainClass.set("art.stalaverse.investment.App")
}
