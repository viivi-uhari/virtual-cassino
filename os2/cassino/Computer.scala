package os2.cassino

import scala.collection.mutable.Buffer

class Computer(name: String, handCards: Buffer[Card], pileCards: Buffer[Card]) extends Player(name, handCards, pileCards) {


  def evaluate(tableCards: Buffer[Card], cardsInGame: Buffer[Card], players: Int): Buffer[Card] = {
    val combinations = tableCombinations(tableCards)
    val possibleCombinations = posCombinations(combinations, handCards)
    if (possibleCombinations.isEmpty) {
      val placeEvaluations = evaluatePlace(combinations, players, cardsInGame, tableCards)
      val move = (placeEvaluations.maxBy( _._2 )._1, Buffer[Card]())
      this.currentCard = move._1
      move._2
    } else {
      val takeEvaluations = evaluateTake(possibleCombinations, tableCards, cardsInGame, players)
      val move = takeEvaluations.maxBy( _._2 )._1
      this.currentCard = move._1
      move._2
    }
  }

  def tableCombinations(tableCards: Buffer[Card]) = {
    var combinations = Buffer[Buffer[Card]]()
    for (i <- 1 to tableCards.size) {
      for (combination: Buffer[Card] <- tableCards.combinations(i).toBuffer) {
        combinations += combination
      }
    }
    combinations
  }

  def posCombinations(combinations: Buffer[Buffer[Card]], cardsInHand: Buffer[Card]) = {
    var possibleCombinations = Buffer[(Card, Buffer[Card])]()
    for (card <- cardsInHand) {
      for (combination <- combinations) {
        if (check(card, combination.toVector)) possibleCombinations += card -> combination
      }
    }
    possibleCombinations
  }

  def evaluateTake(possibleCombinations: Buffer[(Card, Buffer[Card])], tableCards: Buffer[Card], cardsInGame: Buffer[Card], players: Int) = {
    var takeEvaluations = Map[(Card, Buffer[Card]), Double]()
    for (combination <- possibleCombinations) {
      var points: Double = 0
      points += combination._2.size * (1.0 / 52.0)
      points += combination._2.count( _.suit == "s" ) * (1.0 / 13.0) * 2
      points += combination._2.count( _.number == 1 )
      if (combination._2.contains(Card(2, "s"))) points += 1
      if (combination._2.contains(Card(10, "d"))) points += 2
      points += evaluateTakingCard(combination._1)
      points -= sweepSub(tableCards, cardsInGame, players)
      takeEvaluations += combination -> points
    }
    takeEvaluations
  }

  def evaluateTakingCard(card: Card) = {
    var points = 0.0
    if (card == (Card(2, "s"))) points += 1
    if (card == (Card(10, "d"))) points += 2
    if (card.number == 1) points += 1
    if (card.suit == "s") points += (1.0 / 13.0) * 2
    points
  }

  def sweepSub(tableCards: Buffer[Card], cardsInGame: Buffer[Card], players: Int): Double = {
    val sum = tableCards.map( _.number ).sum
    val possibleCards = cardsInGame.filter( _.number == sum )
    (possibleCards.size.toDouble / cardsInGame.size.toDouble) * (1.0 / players)
  }

  def evaluatePlace(combinations: Buffer[Buffer[Card]], players: Int, cardsInGame: Buffer[Card], tableCards: Buffer[Card]) = {
    println(tableCards)
    var placeEvaluations = Map[Card, Double]()
    for (card <- handCards) {
      var points: Double = 0
      if (card == (Card(2, "s"))) points -= 1
      if (card == (Card(10, "d"))) points -= 2
      if (card.number == 1) points -= 1
      if (card.suit == "s") points -= (1.0 / 13.0) * 2
      points += giveSpecialCards(combinations, card, players, cardsInGame)
      points += helpWithOthers(tableCards, card, players) /// 2.0           //dividing by 2 decreases the weight of this aspect
      placeEvaluations += card -> points                                    //when calculating which card should be placed,
      println(tableCards)                                                   //the other aspects are more important
    }
    placeEvaluations
  }

  def giveSpecialCards(combinations: Buffer[Buffer[Card]], card: Card, players: Int, cardsInGame: Buffer[Card]): Double = {
    var minusPoints: Double = 0
    val aces = cardsInGame.filter( _.number == 1 )
    for (combination <- combinations) {
      val sum = card.number + combination.map( _.number ).sum
      if (sum == 14) minusPoints -= (1 * aces.size)
      if (sum == 15 && cardsInGame.contains(Card(2, "s"))) minusPoints -= 1
      if (sum == 16 && cardsInGame.contains(Card(10, "d"))) minusPoints -= 2
    }
    minusPoints * (1.0 / players)
  }

  def helpWithOthers(tableCards: Buffer[Card], card: Card, players: Int) = {
    val newTableCards = tableCards :+ card
    val handCardsLeft = handCards.filter( _ != card)
    val newPossibles = posCombinations(tableCombinations(newTableCards), handCardsLeft)
    var plusPoints: Double = 0
    for (secondCard <- (handCardsLeft)) {                               //second card is the card to take on the next turn
      val combinations = newPossibles.filter( _._1 == secondCard )          //combinations the second card can take
      val cardCount = combinations.map( _._2.size ).sum * (1.0 / 52.0)
      val spadeCount = combinations.map( comb => comb._2.count( card => card.suit == "s" ) ).sum * (1.0 / 13.0) * 2
      val onePointCount = combinations.map( comb => comb._2.count( card => card.number == 1 || card == Card(2, "s") ) ).sum
      val twoPointCount = combinations.map( comb => comb._2.count( card => card == Card(10, "d") ) ).sum * 2
      plusPoints += (cardCount + spadeCount + onePointCount + twoPointCount) * (1.0 / players)
      // all the possible points, lets divide with the players size
      // because the chance of getting these current combinations decreases when the other players play
    }
    plusPoints
  }

}
