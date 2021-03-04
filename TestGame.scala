import scala.collection.mutable.Buffer

object TestGame extends App {

  val player1 = new Player("player1", Buffer(Card(2, "s"), Card(3, "d"), Card(5, "h")), Buffer[Card]())
  val player2 = new Player("player2", Buffer(Card(6, "s"), Card(11, "d"), Card(8, "h")), Buffer[Card]())
  val table = new Table
  table.cards = (Buffer(Card(6, "c"), Card(5, "d"), Card(7, "h")))
  val deck = new Deck
  deck.restack()
  deck.removeCards((player1.handCards ++ player2.handCards ++ table.cards).toVector)


  val game = new Game(Buffer(player1, player2), table, deck)


  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))

  game.playTurn("play 5h")

  println("\n\nAfter play 5h\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))

  game.playTurn("take 5d")

  println("\n\nAfter take 5d\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))



}
