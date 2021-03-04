import scala.collection.mutable.Buffer

class Player(name: String, handCards: Buffer[Card], pileCards: Buffer[Card]) {

  private var currentCard = Card(1, "s")

  def playCard(card: Card) = currentCard = card

  def placeCard(card: Card) = this.handCards -= card

  def addCard(card: Card) = this.handCards += card

  def takeCards(cards: Vector[Card]) = {
    this.pileCards += currentCard
    for (card <- cards) {
      this.pileCards += card
    }
  }


}
