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

  table.cards = Buffer[Card](Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c"))

  deck.restack()
  deck.shuffle()

  deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

  val game = new Game(Buffer(player1, comp1), table, deck)

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

  //checking the computer opponents evaluation

  game.deck.restack()
  game.deck.shuffle()

  "comp1.evaluate" should "return the best move (ace)" in {

    comp1.handCards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "d"), Card(13, "d"), Card(8, "c"), Card(7, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val cardsToTake = comp1.evaluate(game.tableCards, game.cardsInGame, 2)

    assert(cardsToTake.contains(Card(3, "d")))
    assert(cardsToTake.contains(Card(6, "c")))
    assert(cardsToTake.contains(Card(5, "c")))
    assertResult(Card(1, "d")) { comp1.currentCard }

  }

  it should "return the best move (2s)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    comp1.handCards ++= Buffer[Card](Card(2, "s"), Card(13, "d"), Card(8, "c"), Card(7, "c"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

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

  "comp1.evaluatePlace" should "return the best card to place (not an ace, not giving the chance to take special cards)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "d"), Card(4, "c"), Card(12, "s"), Card(1, "h"))
    game.table.cards ++= Buffer[Card](Card(11, "d"), Card(13, "h"), Card(8, "d"), Card(5, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = (placeEvaluations.maxBy( _._2 )._1, Buffer[Card]())
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

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = (placeEvaluations.maxBy( _._2 )._1, Buffer[Card]())
     comp1.playCard(move._1)

    assert(comp1.currentCard != Card(1, "d"))
    assert(comp1.currentCard != Card(1, "h"))
    assert(comp1.currentCard != Card(12, "s"))

  }

  it should "return the best card to place (a small card rather than a big one)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(2, "s"), Card(10, "h"), Card(3, "h"), Card(11, "d"))
    game.table.cards ++= Buffer[Card](Card(7, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = (placeEvaluations.maxBy( _._2 )._1, Buffer[Card]())
     comp1.playCard(move._1)

    assert(comp1.currentCard != Card(2, "s"))
    assert(comp1.currentCard != Card(10, "h"))
    assert(comp1.currentCard != Card(11, "d"))

  }

  it should "return the best card to place (keep spades even if a small card)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(2, "s"), Card(10, "h"), Card(3, "s"), Card(11, "d"))
    game.table.cards ++= Buffer[Card](Card(7, "h"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = (placeEvaluations.maxBy( _._2 )._1, Buffer[Card]())
    comp1.playCard(move._1)

    assert(comp1.currentCard != Card(2, "s"))
    assert(comp1.currentCard != Card(3, "s"))
    assert(comp1.currentCard != Card(11, "d"))

  }

  it should "return the best card to place (a small card rather than a big one, both spades)" in {

    game.deck.restack()
    game.deck.shuffle()
    comp1.handCards.clear()
    game.table.cards.clear()
    comp1.handCards ++= Buffer[Card](Card(1, "c"), Card(7, "s"), Card(4, "s"), Card(11, "s"))
    game.table.cards ++= Buffer[Card](Card(8, "d"))
    game.deck.cards --= (player1.handCards ++ comp1.handCards ++ table.cards)

    val placeEvaluations = comp1.evaluatePlace(comp1.tableCombinations(game.tableCards), 2, game.cardsInGame, game.tableCards)
    val move = (placeEvaluations.maxBy( _._2 )._1, Buffer[Card]())
    comp1.playCard(move._1)

    assert(comp1.currentCard != Card(1, "c"))
    assert(comp1.currentCard != Card(7, "s"))
    assert(comp1.currentCard != Card(11, "s"))

  }

  //cheking the game's methods

  "game.take" should "take the correct cards from the table, add them to player's pile and change the turn" in {

    game.table.cards.clear()
    game.table.cards = Buffer[Card](Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c"))
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

  "game.take" should "not change anything if the turn is invalid" in {

    game.table.cards.clear()
    game.table.cards = Buffer[Card](Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c"))
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
