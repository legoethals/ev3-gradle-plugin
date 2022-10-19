import com.legoethals.ev3.Ev3PluginExtension

plugins {
    id("com.example.plugin.greeting")
//    id("com.legoethals.ev3")
}

repositories {
    mavenCentral()
}

the<Ev3PluginExtension>().message.set("Lol")

//configure<Ev3PluginExtension> {
//    message.set("Haha")
//}

/**
 * Plugin types:
 * * Project -> 'Move buildscript code to plugin'
 * * Settings -> Apply logic in settings script
 * * Gradle -> Influence bootstrapping of gradle
 */

/**
 * Plaatsing van plugin:
 * !!!
 * 1) Scripts
 * vs.
 * 2) Binaries
 */
