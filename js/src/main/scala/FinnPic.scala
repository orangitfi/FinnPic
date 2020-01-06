import org.finnpic.{Female, Male, Pic}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

class FinnPic(pic: Pic) {
  @JSExport
  def getGender: String = {
    pic.gender match {
      case Male => "male"
      case Female => "female"
    }
  }

  @JSExport
  def getBirthDay: Int = {
    pic.birthDay
  }

  @JSExport
  def getBirthMonth: Int = {
    pic.birthMonth
  }

  @JSExport
  def getBirthYear: Int = {
    pic.birthYear
  }
}

@JSExportTopLevel("FinnPic")
object FinnPic {
  @JSExport
  def create(input: String): FinnPic = {
    new FinnPic(Pic.fromStringUnsafe(input))
  }
}
