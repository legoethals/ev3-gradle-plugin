plugins {
    java
    id("com.legoethals.ev3.d3v")
}

ev3 {
    customData {
        welcomeMessage.set("Hi there!!!")
    }
}
//
//templateExampleConfig {
//    message.set("Just trying this gradle plugin...")
//}
//
//buildscript {
//    repositories {
//
//    }
//    dependencies {
//        classpath("com.legoethals:ev3-gradle-plugin:1.0.0")
////        'com.equeo.gradle.plugins:gradle-plugins:1.0-SNAPSHOT'
//    }
//}

//apply plugin: 'com.equeo.gradle.plugins'

//plugins {
//    id("com.legoethals.ev3.d3v")
//}
//
//apply<Ev3
//ev3Config {
//
//}

//ev3Config {
//    customData {
//        welcomeMessage.set("Hi folks!")
//    }
//}
