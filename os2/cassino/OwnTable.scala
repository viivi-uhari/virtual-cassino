package os2.cassino

import scala.collection.mutable.Buffer

class OwnTable {

  var cards = Buffer[Card]()

  def addCard(card: Card) = this.cards += card

  def addCards(cards: Vector[Card]) = this.cards ++= cards

  def removeCards(cards: Vector[Card]) = {
    for (card <- cards) {
      if (this.cards.contains(card)) this.cards -= card
    }
  }

}