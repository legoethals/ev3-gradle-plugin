package com.legoethals.ev3.gradle.testsupport

import org.junit.jupiter.api.Assertions
import kotlin.reflect.KClass

// usage: name shouldBe "test"
infix fun <A> A.shouldBe(that: A) = Assertions.assertEquals(that, this)

// usage: name shouldNotBe "test"
infix fun <A> A.shouldNotBe(that: A) = Assertions.assertNotEquals(that, this)

// usage: listOf(1, 2, 3) shouldContainAll listOf(1, 2)
infix fun <E> E.shouldBeIn(that: Collection<E>) = Assertions.assertTrue(that.contains(this)) { "'$this' is not in '$that'" }

// usage: listOf(1, 2, 3) shouldContainAll listOf(1, 2)
infix fun <E> E.shouldNotBeIn(that: Collection<E>) = Assertions.assertFalse(that.contains(this)) { "'$this' is in '$that'" }

// usage: "unittest" shouldContain "test"
infix fun String.shouldContain(that: String) = Assertions.assertTrue(this.contains(that)) { "'$this' does not contain '$that'" }

// usage: listOf(1, 2, 3) shouldHaveSize 3
infix fun Collection<Any>.shouldHaveSize(size: Int) = Assertions.assertEquals(size, this.size) { "'$this' does not have size '$size', it has size '${this.size}'" }

infix fun Collection<Any>.shouldEqualCollection(that: Collection<Any>) {
    this shouldHaveSize that.size
    this shouldContainAll(that)
}

// usage: listOf(1, 2, 3) shouldContain 2
infix fun <E> Collection<E>.shouldContain(that: E) = Assertions.assertTrue(this.contains(that)) { "'$this' does not contain '$that'" }

// usage: listOf(1, 2, 3) shouldNotContain 4
infix fun <E> Collection<E>.shouldNotContain(that: E) = Assertions.assertFalse(this.contains(that)) { "'$this' does contain '$that'" }

// usage: listOf(1, 2, 3) shouldContainAll listOf(1, 2)
infix fun <E> Collection<E>.shouldContainAll(that: Collection<E>) = Assertions.assertTrue(this.containsAll(that)) { "'$this' does not contain all'$that'" }

// usage: listOf(1, 2, 3).shouldBeEmpty
fun <E> Collection<E>.shouldBeEmpty() = Assertions.assertTrue(this.isEmpty()) { "'$this' should be an empty collection" }

// usage: listOf(1, 2, 3).shouldNotBeEmpty
fun <E> Collection<E>.shouldNotBeEmpty() = Assertions.assertTrue(this.isNotEmpty()) { "'$this' should not be an empty collection" }

// usage: num shouldBeGreaterThan 0
infix fun <A : Comparable<A>> A.shouldBeGreaterThan(that: A) = Assertions.assertTrue(this > that) { "'$this' does not greater than '$that'" }

// usage: num shouldBeGreaterThanOrEqualTo 1
infix fun <A : Comparable<A>> A.shouldBeGreaterThanOrEqualTo(that: A) = Assertions.assertTrue(this >= that) { "'$this' does not greater than or equal to '$that'" }

// usage: num shouldBeLessThan 0
infix fun <A : Comparable<A>> A.shouldBeLessThan(that: A) = Assertions.assertTrue(this < that) { "'$this' does not greater than '$that'" }

// usage: num shouldBeLessThanOrEqualTo 1
infix fun <A : Comparable<A>> A.shouldBeLessThanOrEqualTo(that: A) = Assertions.assertTrue(this <= that) { "'$this' does not greater than or equal to '$that'" }

// usage: val ex = { throw IllegalStateException() } shouldThrow IllegalArgumentException::class
infix fun <T : Throwable> (() -> Any?).shouldThrow(t: KClass<T>): T {
    return Assertions.assertThrows(t.java) { this() }
}

// usage: val result = { "test" } shouldNotThrow IllegalArgumentException::class
infix fun <T : Throwable, R> (() -> R).shouldNotThrow(t: KClass<T>): R? {
    return try {
        this()
    } catch (e: Exception) {
        when {
            e.javaClass == t.java -> {
                Assertions.fail<Any>("Exception of type $t should not be thrown  but got '$e'.")
                null
            }
            else -> {
                null
            }
        }
    }
}

// usage: val result = { "test" }.shouldNotThrowAnyException()
fun <R> (() -> R).shouldNotThrowAnyException(): R? {
    return try {
        this()
    } catch (e: Exception) {
        Assertions.fail<Any>("Expects no exception to be thrown but got '$e'.")
        null
    }
}
