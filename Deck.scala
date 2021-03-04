
import scala.collection.mutable.Buffer
import scala.util.Random


class Deck {

  var cards = Buffer[Card]()

  def removeCard = if (this.cards.nonEmpty) this.cards.drop(1)

  def shuffle() = {
    this.cards = Random.shuffle(this.cards)
  }

  def restack() = this.cards = Buffer(
    Card(1, "s"),
    Card(2, "s"),
    Card(3, "s"),
    Card(4, "s"),
    Card(5, "s"),
    Card(6, "s"),
    Card(7, "s"),
    Card(8, "s"),
    Card(9, "s"),
    Card(10, "s"),
    Card(11, "s"),
    Card(12, "s"),
    Card(13, "s"),
    Card(1, "d"),
    Card(2, "d"),
    Card(3, "d"),
    Card(4, "d"),
    Card(5, "d"),
    Card(6, "d"),
    Card(7, "d"),
    Card(8, "d"),
    Card(9, "d"),
    Card(10, "d"),
    Card(11, "d"),
    Card(12, "d"),
    Card(13, "d"),
    Card(1, "c"),
    Card(2, "c"),
    Card(3, "c"),
    Card(4, "c"),
    Card(5, "c"),
    Card(6, "c"),
    Card(7, "c"),
    Card(8, "c"),
    Card(9, "c"),
    Card(10, "c"),
    Card(11, "c"),
    Card(12, "c"),
    Card(13, "c"),
    Card(1, "h"),
    Card(2, "h"),
    Card(3, "h"),
    Card(4, "h"),
    Card(5, "h"),
    Card(6, "h"),
    Card(7, "h"),
    Card(8, "h"),
    Card(9, "h"),
    Card(10, "h"),
    Card(11, "h"),
    Card(12, "h"),
    Card(13, "h"),
  )

}
