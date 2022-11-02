package com.legoethals.ev3

import lejos.hardware.BrickFinder
import lejos.hardware.Button
import lejos.hardware.Sound
import lejos.hardware.ev3.EV3
import lejos.hardware.lcd.LCD
import lejos.internal.ev3.EV3LED

fun main (args: Array<String>){
    val eV3 = BrickFinder.getLocal() as EV3
    Sound.beep()
    val ev3Led = eV3.led as EV3LED
    ev3Led.setPattern(EV3LED.PATTERN_HEARTBEAT * 3 + EV3LED.COLOR_RED)
    LCD.clear()
    LCD.drawString("Enter to Exit", 0, 2)
    Button.ENTER.waitForPressAndRelease()
    println("brol2.3")
}