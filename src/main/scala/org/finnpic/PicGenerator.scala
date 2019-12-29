package org.finnpic

import java.time.DateTimeException

import scala.collection.mutable.ListBuffer
import scala.util.Random

object PicGenerator {
  @scala.annotation.tailrec
  def generateOne()(implicit seed: Long = Random.nextLong()): Pic = {
    val random = new Random(seed)
    val day: Int = random.nextInt(31) + 1
    val month: Int = random.nextInt(12) + 1
    val year: Int = random.nextInt(100)
    val sign: Char = List('+', '-', 'A')(random.nextInt(3))
    val individualNumber: Int = random.nextInt(1000)
    val birthDatePart: String = formatInt2(day) +
      formatInt2(month) +
      formatInt2(year)
    val numericPartsString = birthDatePart + formatInt3(individualNumber)
    val controlCharacter: Char = Pic.calculateExpectedControlCharacter(numericPartsString.toLong)
    val picString = birthDatePart +
      sign.toString +
      formatInt3(individualNumber) +
      controlCharacter.toString

    var candidatePic: Pic = Pic.fromStringUnsafe(picString)

    // Filter out candidates with an impossible birth date.
    // Empirical experimentation has shown that there are
    // about 2 % of these.
    try {
      candidatePic.birthDate
      candidatePic
    } catch {
      case (_: DateTimeException) => {
        generateOne()(random.nextLong())
      }
    }
  }

  def generateMany(n: Int)(implicit seed: Long = Random.nextLong()): Seq[Pic] = {
    val random = new Random(seed)
    val buffer: ListBuffer[Pic] = new ListBuffer
    for (_ <- 0 until n) {
      buffer += generateOne()(random.nextLong())
    }
    buffer.toList
  }

  private def formatInt2(input: Int): String = {
    f"${input}%02d"
  }

  private def formatInt3(input: Int): String = {
    f"${input}%03d"
  }
}
