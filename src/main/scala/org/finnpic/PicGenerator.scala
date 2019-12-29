package org.finnpic

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

    var candidatePic: Either[String, Pic] = Pic(picString)

    // Filter out candidates with an impossible birth date.
    // Empirical experimentation has shown that there are
    // about 2 % of these.
    candidatePic match {
      case Left(_) => generateOne()(random.nextLong())
      case Right(pic) => pic
    }
  }

  def generateMany(n: Int)(implicit seed: Long = Random.nextLong()): Seq[Pic] = {
    generateInfinite()(seed).take(n)
  }

  def generateInfinite()(implicit seed: Long = Random.nextLong()): Stream[Pic] = {
    val random = new Random(seed)
    generateOne()(random.nextLong()) #:: generateInfinite()(random.nextLong())
  }

  private def formatInt2(input: Int): String = {
    f"${input}%02d"
  }

  private def formatInt3(input: Int): String = {
    f"${input}%03d"
  }
}
