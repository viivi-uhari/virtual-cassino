package os2.cassino

import scala.collection.mutable.Buffer

class Computer(name: String, handCards: Buffer[Card], pileCards: Buffer[Card]) extends Player(name, handCards, pileCards) {

  var combinations = Buffer[Buffer[Card]]()
  var possibleCombinations = Buffer[(Card, Buffer[Card])]()
  var takeEvaluations = Map[Double, (Card, Buffer[Card])]()
  var placeEvaluations = Map[Double, Card]()

  def evaluate(cards: Buffer[Card], cardsInGame: Buffer[Card], players: Int): Buffer[Card] = {
    tableCombinations(cards)
    posCombinations()
    if (possibleCombinations.isEmpty) {
      evaluatePlace(players)
      val move = (placeEvaluations.minBy( _._1 )._2, Buffer[Card]())
      this.currentCard = move._1
      move._2
    } else {
      evaluateTake(cards, cardsInGame, players)
      val move = takeEvaluations.maxBy( _._1 )._2
      this.currentCard = move._1
      move._2
    }
  }

  def tableCombinations(cards: Buffer[Card]) = {
    combinations.clear()
    for (i <- 1 to cards.size) {
      for (combination: Buffer[Card] <- cards.combinations(i).toBuffer) {
        combinations += combination
      }
    }
  }

  def posCombinations(): Unit = {
    possibleCombinations.clear()
    for (card <- handCards) {
      for (combination <- this.combinations) {
        if (check(card, combination.toVector)) this.possibleCombinations += card -> combination
      }
    }
  }

  def evaluateTake(cards: Buffer[Card], cardsInGame: Buffer[Card], players: Int) = {
    takeEvaluations = takeEvaluations.empty
    for (combination <- possibleCombinations) {
      var points: Double = 0
      points += combination._2.size * (1.0 / 52.0)
      points += combination._2.count( _.suit == "s" ) * (1.0 / 13.0) * 2
      points += combination._2.count( _.number == 1 )
      if (combination._2.contains(Card(2, "s"))) points += 1
      if (combination._2.contains(Card(10, "d"))) points += 2
      points -= sweepSub(cards, cardsInGame, players)
      takeEvaluations += points -> combination
    }
  }

  def sweepSub(cards: Buffer[Card], cardsInGame: Buffer[Card], players: Int): Double = {
    val sum = cards.map( _.number ).sum
    val possibleCards = cardsInGame.filter( _.number == sum )
    (possibleCards.size.toDouble / cardsInGame.size.toDouble) * (1.0 / players)
  }

  def evaluatePlace(players: Int) = {
    placeEvaluations = placeEvaluations.empty
    for (card <- handCards) {
      var minusPoints: Double = 0
      if (card == (Card(2, "s"))) minusPoints -= 1
      if (card == (Card(10, "d"))) minusPoints -= 2
      if (card.number == 1) minusPoints -= 1
      if (card.suit == "s") minusPoints -= (1.0 / 13.0) * 2
      minusPoints += giveSpecialCards(card, players)
      placeEvaluations += minusPoints -> card
    }
  }

  def giveSpecialCards(card: Card, players: Int): Double = {
    var minusPoints: Double = 0
    for (combination <- combinations) {
      val sum = card.number + combination.map( _.number).sum
      if (sum == 14 || sum == 15) minusPoints -= 1
      if (sum == 16) minusPoints -= 2
    }
    minusPoints * (1.0 / players)
  }

  // Big or small?
  // 9)	Could it help to play other cards from the hand?
  // (how many different cards in combinations could the placed card help to get * 1/52 + spade count of these cards * 1/13 * 2 +
  //+ point count of these cards * 1) * 1/player count

}
