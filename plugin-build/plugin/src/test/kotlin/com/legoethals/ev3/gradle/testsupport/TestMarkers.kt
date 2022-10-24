package com.legoethals.ev3.gradle.testsupport

object TestMarkers {
    val GIVEN: (description: (String?) -> Any) -> Any = {}
    val WHEN: (description: (String?) -> Any) -> Any = {}
    val THEN: (description: (String?) -> Any) -> Any = {}
    val EXPECT: (description: (String?) -> Any) -> Any = {}
    val AND: (description: (String?) -> Any) -> Any = {}
}
