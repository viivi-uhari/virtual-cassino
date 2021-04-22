package os2.cassino.tests

import os2.cassino._

import scala.collection.mutable.Buffer

object TestGame extends App {


  val player1 = new Player("Player 1", Buffer[Card](Card(1, "s"), Card(2, "d"), Card(10, "c"), Card(9, "c")), Buffer[Card]())
  val player2 = new Player("player2", Buffer(Card(6, "s"), Card(11, "d"), Card(8, "h")), Buffer[Card]())
  val table = new OwnTable
  table.cards = Buffer(Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c"))
  val deck = new Deck
  deck.restack()
  deck.removeCards((player1.handCards ++ player2.handCards ++ table.cards).toVector)


  val game = new Game(Buffer(player1, player2), this.table, this.deck)
  game.currentPlayer = player1

  /*

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)


  game.playTurn("place 5h")

  println("\n\nAfter place 5h\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)

  /*game.playTurn("take 5d")
  println("\n\nAfter take 5d\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)*/

  game.playTurn("play 11d")
  println("\n\nAfter play 11d\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)

  game.playTurn("take 6c, 5d")
  println("\n\nAfter take 6c, 5d\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)


  game.playTurn("end")

  println("\n\nAfter ending the game\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("players' names: " + game.players.map(_.name))

  */

  /*
  val game2 = new os2.cassino.Game

  val player3 = new os2.cassino.Player("player3", Buffer[os2.cassino.Card](), Buffer[os2.cassino.Card]())
  val player4 = new os2.cassino.Player("player4", Buffer[os2.cassino.Card](), Buffer[os2.cassino.Card]())
  val table2 = new os2.cassino.OwnTable
  val deck2 = new os2.cassino.Deck

  game2.table = table2
  game2.deck = deck2
  game2.players = Buffer(player3, player4)

  println("\n\nNew game\n\n")

  println("deck cards: " + deck2.cards)
  println("table cards: " + table2.cards)
  println("player3 handcards: " + player3.handCards)
  println("player3 pilecards: " + player3.pileCards)
  println("player4 handcards: " + player4.handCards)
  println("player4 pilecards: " + player4.pileCards)
  println("players' names: " + game2.players.map( _.name ))

  game2.playTurn("start")

  println("\n\nAfter start\n\n")

  println("deck cards: " + deck2.cards)
  println("table cards: " + table2.cards)
  println("player3 handcards: " + player3.handCards)
  println("player3 pilecards: " + player3.pileCards)
  println("player4 handcards: " + player4.handCards)
  println("player4 pilecards: " + player4.pileCards)
  println("players' names: " + game2.players.map( _.name ))

  //sorting the deck card to better see if the right cards are missing
  println("deck cards sorted: " + deck2.cards.sortBy( _.number ))

  */

  /*

  val table3 = new OwnTable
  val deck3 = new Deck

  val game3 = new Game(Buffer[Player](), this.table3, this.deck3)

  println("\n\nNew game, adding 4 players\n\n")

  game3.playTurn("players 4")

  println("players' names: " + game3.players.map(_.name))
  println("turn: " + game3.currentPlayer.name)

  game3.playTurn("start")

  println("\n\nAfter start\n\n")

  println("deck cards: " + deck3.cards)
  println("table cards: " + table3.cards)
  println("player1 handcards: " + game3.players.head.handCards)
  println("player1 pilecards: " + game3.players.head.pileCards)
  println("player2 handcards: " + game3.players(1).handCards)
  println("player2 pilecards: " + game3.players(1).pileCards)
  println("player3 handcards: " + game3.players(2).handCards)
  println("player3 pilecards: " + game3.players(2).pileCards)
  println("player4 handcards: " + game3.players(3).handCards)
  println("player4 pilecards: " + game3.players(3).pileCards)
  println("players' names: " + game3.players.map(_.name))

  //sorting the deck card to better see if the right cards are missing
  println("deck cards sorted: " + deck3.cards.sortBy(_.number))
  */


  println("\n\nTesting the check method\n\n")

  println(player1.check(Card(11, "s"), Vector(Card(8, "c"))))
  println(player1.check(Card(11, "s"), Vector(Card(11, "d"), Card(11, "c"))))
  println(player1.check(Card(10, "s"), Vector(Card(10, "d"), Card(10, "s"))))

  /*println(player1.check(os2.cassino.Card(12, "h"), Vector(os2.cassino.Card(11, "h"), os2.cassino.Card(6, "d"), os2.cassino.Card(5, "c"), os2.cassino.Card(1, "s"))))
  println(player1.check(os2.cassino.Card(12, "h"), Vector(os2.cassino.Card(13, "h"), os2.cassino.Card(6, "d"), os2.cassino.Card(5, "c"), os2.cassino.Card(1, "s"))))
  println(player1.check(os2.cassino.Card(1, "h"), Vector(os2.cassino.Card(13, "h"), os2.cassino.Card(6, "d"), os2.cassino.Card(8, "c"), os2.cassino.Card(1, "s"))))
  println(player1.check(os2.cassino.Card(2, "s"), Vector(os2.cassino.Card(13, "h"), os2.cassino.Card(7, "d"), os2.cassino.Card(8, "c"), os2.cassino.Card(2, "s"))))
  println(player1.check(os2.cassino.Card(8, "s"), Vector(os2.cassino.Card(2, "h"), os2.cassino.Card(3, "d"), os2.cassino.Card(3, "c"), os2.cassino.Card(8, "s"))))
  println(player1.check(os2.cassino.Card(8, "s"), Vector(os2.cassino.Card(2, "h"), os2.cassino.Card(3, "d"), os2.cassino.Card(3, "c"), os2.cassino.Card(9, "s"))))
  */

  /*val player1 = new Player("player1", Buffer(Card(2, "s"), Card(3, "d"), Card(5, "h")), Buffer[Card]())
  val comp1 = new Computer("Computer 1", Buffer(Card(11, "c"), Card(9, "c"), Card(1, "h"), Card(10, "s")), Buffer())
  val table = new OwnTable
  val deck = new Deck
  table.cards = (Buffer(Card(8, "s"), Card(8, "d")))
  deck.restack()
  deck.removeCards((player1.handCards ++ comp1.handCards ++ table.cards).toVector)

  val game = new Game(Buffer(player1, comp1), this.table, this.deck)
  game.currentPlayer = comp1

  //println(comp1.helpWithOthers(game.table.cards, Card(9, "h"), 2))
  //println(comp1.helpWithOthers(game.table.cards, Card(2, "d"), 2))
  //println(comp1.helpWithOthers(game.table.cards, Card(3, "s"), 2))
  //println(comp1.helpWithOthers(game.table.cards, Card(7, "s"), 2))

  //val combinations = comp1.tableCombinations(game.table.cards)
  //val possibleCombinations = comp1.posCombinations(combinations, comp1.handCards)
  //println(possibleCombinations)
  //println(table.cards)
  //comp1.evaluate(game.table.cards, game.cardsInGame, 2)
  //println(comp1.evaluateTake(possibleCombinations, game.table.cards, game.cardsInGame, 2))

  comp1.evaluate(game.table.cards, game.cardsInGame, 2)
  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("comp1 handcards: " + comp1.handCards)
  println("comp1 pilecards: " + comp1.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)
  */

}
