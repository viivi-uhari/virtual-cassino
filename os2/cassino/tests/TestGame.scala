package os2.cassino.tests

import os2.cassino._

import scala.collection.mutable.Buffer

object TestGame extends App {


  //first testing the take, place and play methods of the Game class

  val player1 = new Player("Player 1", Buffer[Card](Card(1, "s"), Card(2, "d"), Card(10, "c"), Card(9, "c")), Buffer[Card]())
  val player2 = new Player("player2", Buffer(Card(6, "s"), Card(11, "d"), Card(8, "h")), Buffer[Card]())
  val deck = new Deck
  val table = new OwnTable

  table.addCards(Vector(Card(4, "s"), Card(3, "d"), Card(6, "c"), Card(5, "c")))
  deck.restack()
  deck.removeCards((player1.handCards ++ player2.handCards ++ table.cards).toVector)

  val game = new Game(Buffer(player1, player2), this.table, this.deck)
  game.currentPlayer = player1

  println("deck cards: " + game.deck.cards)
  println("table cards: " + game.table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)

  game.currentPlayer.playCard(Card(5, "h"))
  game.place()

  println("\n\nAfter place 5h\n\n")

  println("deck cards: " + game.deck.cards)
  println("table cards: " + game.table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)

  game.take(Buffer(Card(5, "d")))

  println("\n\nAfter take 5d\n\n")

  println("deck cards: " + game.deck.cards)
  println("table cards: " + game.table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)

  game.play(Card(11, "d"))

  println("\n\nAfter play 11d\n\n")

  println("deck cards: " + game.deck.cards)
  println("table cards: " + game.table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)

  game.take(Buffer(Card(6, "c"), Card(5, "d")))

  println("\n\nAfter take 6c, 5d\n\n")

  println("deck cards: " + game.deck.cards)
  println("table cards: " + game.table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map(_.name))
  println("turn: " + game.currentPlayer.name)


  //then testing the end, start and addPlayers methods

  game.end()

  println("\n\nAfter ending the game\n\n")

  println("deck cards: " + game.deck.cards)
  println("table cards: " + game.table.cards)
  println("players' names: " + game.players.map(_.name))

  val player3 = new Player("player3", Buffer[Card](), Buffer[Card]())
  val player4 = new Player("player4", Buffer[Card](), Buffer[Card]())
  val table2 = new OwnTable
  val deck2 = new Deck

  val game2 = new Game(Buffer(player3, player4), table2, deck2)

  println("\n\nNew game\n\n")

  println("deck cards: " + game2.deck.cards)
  println("table cards: " + game2.table.cards)
  println("player3 handcards: " + player3.handCards)
  println("player3 pilecards: " + player3.pileCards)
  println("player4 handcards: " + player4.handCards)
  println("player4 pilecards: " + player4.pileCards)
  println("players' names: " + game2.players.map( _.name ))

  game2.start()

  println("\n\nAfter start\n\n")

  println("deck cards: " + game2.deck.cards)
  println("table cards: " + game2.table.cards)
  println("player3 handcards: " + player3.handCards)
  println("player3 pilecards: " + player3.pileCards)
  println("player4 handcards: " + player4.handCards)
  println("player4 pilecards: " + player4.pileCards)
  println("players' names: " + game2.players.map( _.name ))
  println("Current player: " + game2.currentPlayer.name)

  //sorting the deck card to better see if the right cards are missing
  println("deck cards sorted: " + game2.deck.cards.sortBy( _.number ))

  val table3 = new OwnTable
  val deck3 = new Deck
  val game3 = new Game(Buffer[Player](), this.table3, this.deck3)

  println("\n\nNew game, adding 4 players\n\n")

  game3.addPlayers(4)

  println("players' names: " + game3.players.map(_.name))
  println("turn: " + game3.currentPlayer.name)

  game3.start()

  println("\n\nAfter start\n\n")

  println("deck cards: " + game3.deck.cards)
  println("table cards: " + game3.table.cards)
  println("player1 handcards: " + game3.players.head.handCards)
  println("player1 pilecards: " + game3.players.head.pileCards)
  println("player2 handcards: " + game3.players(1).handCards)
  println("player2 pilecards: " + game3.players(1).pileCards)
  println("player3 handcards: " + game3.players(2).handCards)
  println("player3 pilecards: " + game3.players(2).pileCards)
  println("player4 handcards: " + game3.players(3).handCards)
  println("player4 pilecards: " + game3.players(3).pileCards)
  println("players' names: " + game3.players.map(_.name))
  println("turn: " + game3.currentPlayer.name)

  //sorting the deck card to better see if the right cards are missing
  println("deck cards sorted: " + deck3.cards.sortBy(_.number))


  //then testing the check method of the Player class

  println("\n\nTesting the check method\n\n")

  println("should return false: ")
  println(player1.check(Card(12, "h"), Vector(Card(11, "h"), Card(6, "d"), Card(5, "c"), Card(1, "s"))))
  println("\n")
  println("should return false: ")
  println(player1.check(Card(12, "h"), Vector(Card(13, "h"), Card(6, "d"), Card(5, "c"), Card(1, "s"))))
  println("\n")
  println("should return true: ")
  println(player1.check(Card(1, "h"), Vector(Card(13, "h"), Card(6, "d"), Card(8, "c"), Card(1, "s"))))
  println("\n")
  println("should return true: ")
  println(player1.check(Card(2, "s"), Vector(Card(13, "h"), Card(7, "d"), Card(8, "c"), Card(2, "s"))))
  println("\n")
  println("should return true: ")
  println(player1.check(Card(8, "s"), Vector(Card(2, "h"), Card(3, "d"), Card(3, "c"), Card(8, "s"))))
  println("\n")
  println("should return false: ")
  println(player1.check(Card(8, "s"), Vector(Card(2, "h"), Card(3, "d"), Card(3, "c"), Card(9, "s"))))
  println("\n")
  println("should return true: ")
  println(player1.check(Card(11, "s"), Vector(Card(11, "h"), Card(11, "d"))))
  println("\n")
  println("should return false: ")
  println(player1.check(Card(11, "s"), Vector(Card(10, "h"), Card(11, "d"))))


  //then testing the computer opponent's methods

  println("\n\nTesting the Computer opponent\n\n")

  val player = new Player("player 1", Buffer(Card(2, "s"), Card(3, "d"), Card(5, "h")), Buffer[Card]())
  val comp = new Computer("Computer 1", Buffer(Card(11, "c"), Card(9, "c"), Card(1, "h"), Card(10, "s")), Buffer())
  val table4 = new OwnTable
  val deck4 = new Deck

  table4.addCards(Vector(Card(8, "s"), Card(8, "d")))
  deck4.restack()
  deck4.removeCards((player.handCards ++ comp.handCards ++ table4.cards).toVector)

  val game4 = new Game(Buffer(player, comp), this.table4, this.deck4)
  game4.currentPlayer = comp
  game4.cardsInGame --= game4.table.cards

  val combinations = comp.tableCombinations(game4.table.cards)
  val possibleCombinations = comp.posCombinations(combinations, comp.handCards)

  println("\nPossible combinations:\n")
  println(possibleCombinations)
  println("\nComputer evaluateTake method:\n")
  println(comp.evaluateTake(possibleCombinations, game4.table.cards, game4.cardsInGame, 2))
  println("\nComputer evaluatePlace method:\n")
  println(comp.evaluatePlace(combinations, 2, game4.cardsInGame, game4.table.cards))

  comp.evaluate(game4.table.cards, game4.cardsInGame, 2)
  game4.playComputer(comp)

  println("\nAfter game playComputer method:\n")
  println("deck cards: " + game4.deck.cards)
  println("table cards: " + game4.table.cards)
  println("player1 handcards: " + player.handCards)
  println("player1 pilecards: " + player.pileCards)
  println("comp1 handcards: " + comp.handCards)
  println("comp1 pilecards: " + comp.pileCards)
  println("players' names: " + game4.players.map(_.name))
  println("turn: " + game4.currentPlayer.name)

  //testing the pointCount methdo of the Game class
  println("\n\nTesting the pointcount string\n\n")

  val player5 = new Player("Player 5", Buffer[Card](), Buffer(Card(2, "s"), Card(1, "d"), Card(1, "h"), Card(3, "d"), Card(5, "h")))
  val player6 = new Player("Player 6", Buffer[Card](), Buffer(Card(10, "d"), Card(1, "s"), Card(10, "s"), Card(3, "s")))
  val table5 = new OwnTable
  val deck5 = new Deck
  val game5 = new Game(Buffer(player5, player6), this.table5, this.deck5)

  println(game5.pointCount())


}
