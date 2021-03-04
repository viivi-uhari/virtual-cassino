


case class Card(val number: Int, val suit: String) {

  def handNumber = {
    this match {
      case Card(1, s: String) => 14
      case Card(2, "s") => 15
      case Card(10, "d") => 16
      case Card(n: Int, s: String) => n
    }
  }

}
