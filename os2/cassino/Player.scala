package os2.cassino

import scala.collection.mutable.Buffer

class Player(var name: String, var handCards: Buffer[Card], var pileCards: Buffer[Card]) {

  var currentCard = Card(1, "s")   //Some?
  var points = 0

  def playCard(card: Card) = currentCard = card

  def placeCard(card: Card) = this.handCards -= card

  def addCard(card: Card) = this.handCards += card

  def addCards(cards: Vector[Card]) = this.handCards ++= cards

  def addAtTheEnd(cards: Vector[Card]) = this.pileCards ++= cards

  def takeCards(cards: Vector[Card]) = {
    this.pileCards += currentCard
    for (card <- cards) {
      this.pileCards += card
    }
    this.handCards -= currentCard
  }

  def check(playedCard: Card, wantedCards: Vector[Card]) = {
    var result = true
    val originalWantedNumbers = wantedCards.map( _.number ).toBuffer
    var wantedNumbers = Buffer[Int]()
    for (n <- originalWantedNumbers) {
      if (n > playedCard.handNumber) {
        result = false
      } else if (n < playedCard.handNumber) {
        wantedNumbers += n
      }
    }
    if ((wantedNumbers.size == 1) && (wantedNumbers.head != playedCard.handNumber)) result = false

    if (result) {
      var originalList = wantedNumbers
      while (wantedNumbers.nonEmpty && result) {
        val sum = wantedNumbers.head + wantedNumbers(1)
        if (sum == playedCard.handNumber) {
          wantedNumbers --= wantedNumbers.take(2)
          if (wantedNumbers.size == 1) result = false
          originalList = wantedNumbers
        } else if (sum < playedCard.handNumber) {
          if (wantedNumbers.size > 2) {
            wantedNumbers --= wantedNumbers.take(2)
            wantedNumbers = sum +: wantedNumbers
            originalList = wantedNumbers
          } else {
            result = false
          }
        } else {
          val second = wantedNumbers(1)
          wantedNumbers = wantedNumbers.head +: wantedNumbers.drop(2)
          wantedNumbers += second
          if (wantedNumbers == originalList) result = false
        }
      }
    }
    result
  }

}
