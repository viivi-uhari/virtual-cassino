package os2.cassino.tests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import os2.cassino._

import scala.collection.mutable.Buffer


class UnitTests extends AnyFlatSpec with Matchers {

  val player1 = new Player("Player 1", Buffer[Card](Card(1, "s"), Card(2, "d"), Card(10, "c"), Card(9, "c")), Buffer[Card]())
  val comp1 = new Computer("Computer 1", Buffer[Card](Card(12, "s"), Card(13, "d"), Card(8, "c"), Card(7, "c")), Buffer[Card]())
  val table = new OwnTable
  val deck = new Deck

  table.addCards(Vector[Card](Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c")))
  deck.restack()
  deck.shuffle()
  deck.removeCards((player1.handCards ++ comp1.handCards ++ table.cards).toVector)

  val game = new Game(Buffer(player1, comp1), table, deck)

  //testing the check method

  "player1.check" should "check the cards correctly (ace and normal cards)" in {

    assertResult(true) { player1.check(player1.handCards(0), Vector(Card(3, "d"), Card(6, "c"), Card(5, "c"))) }
    assertResult(false) { player1.check(player1.handCards(1), Vector(Card(3, "d"))) }
    assertResult(true) { player1.check(player1.handCards(1), Vector(Card(2, "s"))) }
    assertResult(true) { player1.check(player1.handCards(2), Vector(Card(4, "s"), Card(6, "c"))) }
    assertResult(true) { player1.check(player1.handCards(3), Vector(Card(3, "d"), Card(6, "c"))) }

  }

  it should "check the cards correctly (multiple same number cards)" in {

    assertResult(true) { player1.check(player1.handCards(2), Vector(Card(10, "d"), Card(10, "s"))) }

  }

  it should "check the cards correctly (multiple combinations, ace and normal cards)" in {

    assertResult(true) { player1.check(player1.handCards(2), Vector(Card(10, "d"), Card(10, "s"), Card(4, "s"), Card(6, "c"))) }
    assertResult(true) { player1.check(player1.handCards(0), Vector(Card(10, "d"), Card(4, "s"), Card(8, "s"), Card(6, "c"))) }
    assertResult(false) { player1.check(player1.handCards(2), Vector(Card(10, "d"), Card(10, "s"), Card(6, "c"))) }
    assertResult(false) { player1.check(player1.handCards(0), Vector(Card(10, "d"), Card(4, "s"), Card(6, "c"))) }

  }

  it should "check the cards correctly (special cards)" in {

    assertResult(false) { player1.check(Card(2, "s"), Vector(Card(2, "d"))) }
    assertResult(true) { player1.check(Card(2, "s"), Vector(Card(10, "d"), Card(5, "c"))) }
    assertResult(false) { player1.check(Card(10, "d"), Vector(Card(10, "s"))) }
    assertResult(true) { player1.check(Card(10, "d"), Vector(Card(10, "h"), Card(6, "h"))) }

  }

  //checking the computer opponent's evaluation

  game.deck.restack()
  game.deck.shuffle()

  "comp1.evaluate" should "return the best move (ace, taking + special card)" in {

    comp1.handCards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "d"), Card(13, "d"), Card(8, "c"), Card(7, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val cardsToTake = comp1.evaluate(game.tableCards, game.cardsInGame, 2)

    assert(cardsToTake.contains(Card(3, "d")))
    assert(cardsToTake.contains(Card(6, "c")))
    assert(cardsToTake.contains(Card(5, "c")))
    assertResult(Card(1, "d")) { comp1.currentCard }

  }

  it should "return the best move (2s, taking + special card)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    comp1.handCards ++= Buffer[Card](Card(2, "s"), Card(13, "d"), Card(8, "c"), Card(7, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    game.cardsInGame --= game.table.cards

    val cardsToTake = comp1.evaluate(game.tableCards, game.cardsInGame, 2)

    assert(cardsToTake.contains(Card(4, "s")))
    assert(cardsToTake.contains(Card(6, "c")))
    assert(cardsToTake.contains(Card(5, "c")))
    assertResult(Card(2, "s")) { comp1.currentCard }

  }

  it should "return the best move (10d)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(10, "d"), Card(13, "d"), Card(8, "c"), Card(7, "c"))
    game.table.cards ++= Buffer[Card](Card(4, "s"), Card(10, "h"), Card(6, "c"), Card(5, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val cardsToTake = comp1.evaluate(game.tableCards, game.cardsInGame, 2)

    assert(cardsToTake.contains(Card(10, "h")))
    assert(cardsToTake.contains(Card(6, "c")))
    assertResult(Card(10, "d")) { comp1.currentCard }

  }

  it should "return the best move (10d even if ace)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(10, "d"), Card(1, "d"), Card(8, "c"), Card(7, "c"))
    game.table.cards ++= Buffer[Card](Card(4, "s"), Card(10, "h"), Card(6, "c"), Card(5, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val cardsToTake = comp1.evaluate(game.tableCards, game.cardsInGame, 2)

    assert(cardsToTake.contains(Card(10, "h")))
    assert(cardsToTake.contains(Card(6, "c")))
    assertResult(Card(10, "d")) { comp1.currentCard }

  }

  it should "return the best move (spades)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(10, "s"), Card(10, "h"), Card(8, "c"), Card(7, "c"))
    game.table.cards ++= Buffer[Card](Card(4, "h"), Card(13, "h"), Card(6, "c"), Card(5, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val cardsToTake = comp1.evaluate(game.tableCards, game.cardsInGame, 2)

    assert(cardsToTake.contains(Card(4, "h")))
    assert(cardsToTake.contains(Card(6, "c")))
    assertResult(Card(10, "s")) { comp1.currentCard }

  }

  //testing the place evaluations of the computer opponent

  "comp1.evaluatePlace" should "return the best card to place (not an ace, not spades)" in {

    //very little chance that the next player has the two of spades (4c + 11d)

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "d"), Card(4, "c"), Card(12, "s"), Card(1, "h"))
    game.table.cards ++= Buffer[Card](Card(11, "d"), Card(13, "h"), Card(8, "d"), Card(5, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ game.table.cards)
    game.cardsInGame --= game.table.cards

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    println(placeEvaluations)
    assert(comp1.currentCard != Card(1, "d"))
    assert(comp1.currentCard != Card(1, "h"))
    assert(comp1.currentCard != Card(12, "s"))

  }

  it should "return the best card to place (not an ace, not giving the chance for 2s)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "d"), Card(4, "c"), Card(12, "s"), Card(1, "h"))
    game.table.cards ++= Buffer[Card](Card(11, "d"), Card(13, "h"), Card(8, "d"), Card(5, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ game.table.cards)

    val fakeCardsInGame = Buffer(Card(1, "s"), Card(2, "d"), Card(10, "c"), Card(9, "c"), Card(2, "s"))

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, fakeCardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    assert(comp1.currentCard != Card(1, "d"))
    assert(comp1.currentCard != Card(1, "h"))
    assert(comp1.currentCard != Card(4, "c"))

  }

  it should "return the best card to place (not an ace, not spades if possible)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "d"), Card(12, "c"), Card(12, "s"), Card(1, "h"))
    game.table.cards ++= Buffer[Card](Card(11, "d"), Card(13, "h"), Card(8, "d"), Card(5, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    game.cardsInGame --= game.table.cards

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    println(placeEvaluations)
    assert(comp1.currentCard != Card(1, "d"))
    assert(comp1.currentCard != Card(1, "h"))
    assert(comp1.currentCard != Card(12, "s"))

  }

  it should "return the best card to place (a small card rather than a big one, 3h vs 10h and 11d)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(2, "s"), Card(10, "h"), Card(3, "h"), Card(11, "d"))
    game.table.cards ++= Buffer[Card](Card(7, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    game.cardsInGame --= game.table.cards

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    println(placeEvaluations)
    assert(comp1.currentCard != Card(2, "s"))
    assert(comp1.currentCard != Card(10, "h"))
    assert(comp1.currentCard != Card(11, "d"))

  }

  it should "return the best card to place (a bigger card rather than spades, 3s vs 11d)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(2, "s"), Card(13, "h"), Card(3, "s"), Card(11, "d"))
    game.table.cards ++= Buffer[Card](Card(7, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    game.cardsInGame --= game.table.cards

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    println(placeEvaluations)
    assert(comp1.currentCard != Card(2, "s"))
    assert(comp1.currentCard != Card(3, "s"))
    assert(comp1.currentCard != Card(13, "h"))

  }

  it should "return the best card to place (the one that gives the worst chance for special cards)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(8, "h"), Card(4, "d"), Card(1, "s"), Card(6, "c"))
    game.table.cards ++= Buffer[Card](Card(7, "c"), Card(10, "h"), Card(12, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    game.cardsInGame --= game.table.cards

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    println(placeEvaluations)
    assert(comp1.currentCard != Card(4, "c"))
    assert(comp1.currentCard != Card(1, "s"))
    assert(comp1.currentCard != Card(6, "c"))

  }

  it should "return the best card to place (the one that gives the least chances for special cards, not spades)" in {

    //a small chance for the next one to have 10d, better to save spades

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(8, "s"), Card(4, "d"), Card(1, "s"), Card(6, "c"))
    game.table.cards ++= Buffer[Card](Card(7, "c"), Card(10, "h"), Card(12, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    game.cardsInGame --= game.table.cards

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = placeEvaluations.maxBy( _._2 )._1
    comp1.playCard(move._1)

    println(placeEvaluations)
    assert(comp1.currentCard != Card(4, "c"))
    assert(comp1.currentCard != Card(1, "s"))
    assert(comp1.currentCard != Card(8, "s"))

  }

  //cheking the game's take method

  "game.take" should "take the correct cards from the table, add them to player's pile and change the turn" in {

    game.table.cards.clear()
    game.table.cards ++= Buffer[Card](Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c"))
    game.deck.restack()
    game.deck.shuffle()
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)
    //Player1's hand cards: Buffer[Card](Card(1, "s"), Card(2, "d"), Card(10, "c"), Card(9, "c"))

    game.currentPlayer = player1
    game.currentPlayer.playCard(player1.handCards(0))

    game.take(Buffer(Card(3, "d"), Card(6, "c"), Card(5, "c")))

    assert(game.table.cards.contains(Card(4, "s")))
    assert(!game.table.cards.contains(Card(3, "d")))
    assert(!game.table.cards.contains(Card(6, "c")))
    assert(!game.table.cards.contains(Card(5, "c")))

    assert(player1.pileCards.contains(Card(3, "d")))
    assert(player1.pileCards.contains(Card(6, "c")))
    assert(player1.pileCards.contains(Card(5, "c")))

    assertResult(comp1) { game.currentPlayer }
    assertResult(player1) { game.previousPlayer }
    assertResult(player1) { game.lastPlayer }

  }

  it should "not change anything if the turn is invalid" in {

    game.table.cards.clear()
    game.table.cards ++= Buffer[Card](Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c"))
    game.deck.restack()
    game.deck.shuffle()
    player1.pileCards.clear()
    player1.handCards.clear()
    player1.handCards ++= Buffer[Card](Card(1, "s"), Card(2, "d"), Card(10, "c"), Card(9, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    game.currentPlayer = player1
    game.currentPlayer.playCard(player1.handCards(1))

    game.take(Buffer(Card(3, "d")))

    assert(game.table.cards.contains(Card(4, "s")))
    assert(game.table.cards.contains(Card(3, "d")))
    assert(game.table.cards.contains(Card(6, "c")))
    assert(game.table.cards.contains(Card(5, "c")))

    assert(player1.pileCards.isEmpty)

    assertResult(4) { game.table.cards.size }
    assertResult(4) { player1.handCards.size }
    assertResult(Card(2, "d")) { player1.currentCard }

    assertResult(player1) { game.currentPlayer }

  }

}
