package os2.cassino

import scala.collection.mutable.Buffer

class Player(var name: String, var handCards: Buffer[Card], var pileCards: Buffer[Card]) {

  private var cardToBePlayed = Card(1, "s")   //the card the player wants to play, initialized with ace of spades
  private var points = 0

  def addPoints(toBeAdded: Int) = this.points += toBeAdded

  def tellPoints = this.points

  def currentCard = this.cardToBePlayed

  //sets the given card to be the card the player wants to play
  def playCard(card: Card) = this.cardToBePlayed = card

  //placing a card on the table takes this card away from the player's hand
  def placeCard(card: Card) = this.handCards -= card

  //when the player is given a card from the deck after it's move
  def addCard(card: Card) = this.handCards += card

  //when the player is dealt cards at the beginning of the game
  def addCards(cards: Vector[Card]) = this.handCards ++= cards

  //adding cards to the player's pile at the end of the game if this player was the lastPlayer in the Game class
  def addAtTheEnd(cards: Vector[Card]) = this.pileCards ++= cards

  //taking cards off the table places these cards to the player's pile
  //takes the current card away from the player's hand
  def takeCards(cards: Vector[Card]) = {
    this.pileCards += currentCard
    for (card <- cards) {
      this.pileCards += card
    }
    this.handCards -= currentCard
  }

  //the cheking algorithm to check if the move to take cards of the table in valid or not
  //the played card uses its value when in hand
  def check(playedCard: Card, wantedCards: Vector[Card]): Boolean = {
    var result = true
    val originalWantedNumbers = wantedCards.map( _.number ).toBuffer
    var wantedNumbers = Buffer[Int]()
    for (n <- originalWantedNumbers) {
      if (n > playedCard.handNumber) {  //if one of the cards if bigger than the played one, the move is invalid
        result = false
      } else if (n < playedCard.handNumber) {  //adding the cards that are smaller than the played one to the wantedNumbers for the next step
        wantedNumbers += n                       //(eliminates the cards with the same value as the played card)
      }
    }
    //if the cards now consist of only one card, the move is invalid
    if ((wantedNumbers.size == 1) && (wantedNumbers.head != playedCard.handNumber)) result = false

    //otherwise we can proceed to counting sums of the cards
    if (result) {
      var originalList = wantedNumbers
      while (wantedNumbers.nonEmpty && result) {
        val sum = wantedNumbers.head + wantedNumbers(1)
        if (sum == playedCard.handNumber) {  //if the sum is as big as the played card, we can eliminate the sum's cards...
          wantedNumbers --= wantedNumbers.take(2)
          if (wantedNumbers.size == 1) result = false  //...then if only one is left, again the move is invalid
          originalList = wantedNumbers
        } else if (sum < playedCard.handNumber) {  //if the sum is smaller than the played card, we add more cards to it
          if (wantedNumbers.size > 2) {
            wantedNumbers --= wantedNumbers.take(2)
            wantedNumbers = sum +: wantedNumbers
            originalList = wantedNumbers
          } else {  //but if no cards are left, the move is invalid
            result = false
          }
        } else {  //if the sum is bigger than the played card, we change the order of the cards, until all of the cards are eliminated...
          val second = wantedNumbers(1)
          wantedNumbers = wantedNumbers.head +: wantedNumbers.drop(2)
          wantedNumbers += second
          if (wantedNumbers == originalList) result = false //...or have the same order than in the start in which case the move is invalid
        }
      }
    }
    result
  }

}
