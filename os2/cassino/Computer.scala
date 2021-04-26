package os2.cassino

import scala.collection.mutable.Buffer

class Computer(name: String, handCards: Buffer[Card], pileCards: Buffer[Card]) extends Player(name, handCards, pileCards) {


  //the final evaluate method that evaluates the best move by the maximum value of the different moves
   //returns the cards to be taken, an empty buffer if a card is to be placed
  def evaluate(tableCards: Buffer[Card], cardsInGame: Buffer[Card], players: Int): Buffer[Card] = {
    val combinations = tableCombinations(tableCards)
    val possibleCombinations = posCombinations(combinations, handCards)
    val placeEvaluations = evaluatePlace(combinations, players, cardsInGame, tableCards)
    val takeEvaluations = evaluateTake(possibleCombinations, tableCards, cardsInGame, players)
    val move = (placeEvaluations ++ takeEvaluations).maxBy( _._2)._1
    playCard(move._1) //sets the best move's card to be one the opponent wants to play
    move._2
  }

  //all possible combinations of the table
  def tableCombinations(tableCards: Buffer[Card]) = {
    var combinations = Buffer[Buffer[Card]]()
    for (i <- 1 to tableCards.size) {
      for (combination: Buffer[Card] <- tableCards.combinations(i).toBuffer) {
        combinations += combination
      }
    }
    combinations
  }

  //matching the opponents hand cards to combinations it can take of the table
  def posCombinations(combinations: Buffer[Buffer[Card]], cardsInHand: Buffer[Card]) = {
    var possibleCombinations = Buffer[(Card, Buffer[Card])]()
    for (card <- cardsInHand) {
      for (combination <- combinations) {
        if (check(card, combination.toVector)) possibleCombinations += card -> combination
      }
    }
    possibleCombinations
  }

  //evaluating the possible take moves
  def evaluateTake(possibleCombinations: Buffer[(Card, Buffer[Card])], tableCards: Buffer[Card], cardsInGame: Buffer[Card], players: Int) = {
    var takeEvaluations = Map[(Card, Buffer[Card]), Double]()
    var leftOver = Buffer[Card]() ++ tableCards
    for (combination <- possibleCombinations) {
      var points: Double = 0
      points += combination._2.size * (1.0 / 52.0)                         //equation 1 in the document (Algorithms: Computer Opponent)
      points += combination._2.count( _.suit == "s" ) * (1.0 / 13.0) * 2   //equation 2
      points += combination._2.count( _.number == 1 )                      //equation 3
      if (combination._2.contains(Card(2, "s"))) points += 1               //equation 3
      if (combination._2.contains(Card(10, "d"))) points += 2              //equation 3
      points += evaluateTakingCard(combination._1)                         //evaluating individually the card that takes these cards
      for (card <- combination._2) {
        leftOver -= card
      }
      points -= sweepSub(leftOver, cardsInGame, players)                   //equation 4
      takeEvaluations += combination -> points
    }
    takeEvaluations
  }

  //evaluating individually the card that takes cards off the table
  private def evaluateTakingCard(card: Card) = {
    var points = 0.0
    if (card == (Card(2, "s"))) points += 1            //equation 3
    if (card == (Card(10, "d"))) points += 2           //equation 3
    if (card.number == 1) points += 1                  //equation 3
    if (card.suit == "s") points += (1.0 / 13.0) * 2   //equation 2
    points
  }

  //evaluating the possibility for a sweep and what it would mean in points
  private def sweepSub(leftOver: Buffer[Card], cardsInGame: Buffer[Card], players: Int): Double = {
    val sum = leftOver.map( _.number ).sum
    val possibleCards = cardsInGame.filter( _.number == sum )
    (possibleCards.size.toDouble / cardsInGame.size.toDouble) * handCards.size  //the right player has to have the right card
  }

  //evaluating the possible place moves
  def evaluatePlace(combinations: Buffer[Buffer[Card]], players: Int, cardsInGame: Buffer[Card], tableCards: Buffer[Card]) = {
    var placeEvaluations = Map[(Card, Buffer[Card]), Double]()
    for (card <- handCards) {
      var points: Double = 0
      if (card == (Card(2, "s"))) points -= 1                              //equation 5
      if (card == (Card(10, "d"))) points -= 2                             //equation 5
      if (card.number == 1) points -= 1                                    //equation 5
      if (card.suit == "s") points -= (1.0 / 13.0) * 2                     //equation 6
      points += giveSpecialCards(combinations, card, players, cardsInGame) //equation 7
      points -= card.number.toDouble * (0.0001)                            //equation 8
      placeEvaluations += (card, Buffer[Card]()) -> points
    }
    placeEvaluations
  }

  //evaluating if the placed card makes possible combinations for special cards with the rest of the table cards
  //and if these special cards are still in the game.
  private def giveSpecialCards(combinations: Buffer[Buffer[Card]], card: Card, players: Int, cardsInGame: Buffer[Card]): Double = {
    var minusPoints: Double = 0
    val aces = cardsInGame.filter( _.number == 1 )
    for (combination <- combinations) {
      val sum = card.number + combination.map( _.number ).sum
      if (sum == 14) minusPoints -= (1 * aces.size)
      if (sum == 15 && cardsInGame.contains(Card(2, "s"))) minusPoints -= 1
      if (sum == 16 && cardsInGame.contains(Card(10, "d"))) minusPoints -= 2
    }
    (minusPoints / cardsInGame.size.toDouble) * handCards.size  //the next player would need to have the special card
  }

}
