package com.legoethals.ev3

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Nested


//Managed types = abstract class or interface with no fields and whose properties are all managed
//A named managed type is a managed type that additionally has an abstract property "name" of type String. Named managed types are especially useful as the element type of NamedDomainObjectContainer (see below).
abstract class Ev3PluginExtension {

    abstract val outputDir: RegularFileProperty

    @get:Nested
    abstract val customData: CustomData

//    open fun customData(action: Action<in CustomData>) {
//        action.execute(customData)
//    }

    fun customData(config: CustomData.() -> Unit) {
        customData.config()
    }
//    fun getResources(): NamedDomainObjectContainer<SomeConfig>

//    private val someConfig: SomeConfig
//    @Inject
//    constructor(objectFactory: ObjectFactory){
//        someConfig = objectFactory.newInstance(SomeConfig::class.java)
//    }

//    val message: Property<String>
//
//    init {
//        message.convention("Hello there!")
//    }
}
//
//interface SomeConfig {
//    //Type must have a read-only 'name' property
//    fun getName(): String
//    fun getSomeCoolProperty(): Property<String>
//
//    fun getNestedConfig(): NamedDomainObjectContainer<SomeNestedConfig>
//
//}
//
//interface  SomeNestedConfig {
//    fun getSomeOtherProperty(): Property<Int>
//}