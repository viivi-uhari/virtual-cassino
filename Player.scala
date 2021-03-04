import scala.collection.mutable.Buffer

class Player(var name: String, var handCards: Buffer[Card], var pileCards: Buffer[Card]) {

  private var currentCard = Card(1, "s")

  def playCard(card: Card) = currentCard = card

  def placeCard(card: Card) = this.handCards -= card

  def addCard(card: Card) = this.handCards += card

  def addCards(cards: Vector[Card]) = this.handCards ++= cards

  def takeCards(cards: Vector[Card]) = {
    this.pileCards += currentCard
    for (card <- cards) {
      this.pileCards += card
    }
    this.handCards -= currentCard
  }

}
