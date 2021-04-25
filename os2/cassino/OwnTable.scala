package os2.cassino

import scala.collection.mutable.Buffer

class OwnTable {

  //represents the table's cards
  private var currentCards = Buffer[Card]()

  def cards = currentCards

  //when placing a card on the table, a card is added
  def addCard(card: Card) = this.cards += card

  //when the first four cards are dealt at the start of a game, four cards are added
  def addCards(cards: Vector[Card]) = this.cards ++= cards

  //when cards are taken of the table, cards are removed
  def removeCards(cards: Vector[Card]) = {
    for (card <- cards) {
      if (this.cards.contains(card)) this.cards -= card
    }
  }

}
