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
  game.currentPlayer = player1


  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)

  game.playTurn("play 5h")

  println("\n\nAfter play 5h\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)

  game.playTurn("take 5d")
  println("\n\nAfter take 5d\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)

  game.playTurn("play 6s")
  println("\n\nAfter play 6s\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)

  game.playTurn("take 6c")
  println("\n\nAfter take 6c\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("player1 handcards: " + player1.handCards)
  println("player1 pilecards: " + player1.pileCards)
  println("player2 handcards: " + player2.handCards)
  println("player2 pilecards: " + player2.pileCards)
  println("players' names: " + game.players.map( _.name ))
  println("turn: " + game.currentPlayer.name)


  game.playTurn("end")

  println("\n\nAfter ending the game\n\n")

  println("deck cards: " + deck.cards)
  println("table cards: " + table.cards)
  println("players' names: " + game.players.map( _.name ))


  val player3 = new Player("player3", Buffer[Card](), Buffer[Card]())
  val player4 = new Player("player4", Buffer[Card](), Buffer[Card]())
  val table2 = new Table
  val deck2 = new Deck


  /* val game2 = new Game(Buffer(player3, player4), table2, deck2)

  println("\n\nNew game\n\n")

  println("deck cards: " + deck2.cards)
  println("table cards: " + table2.cards)
  println("player3 handcards: " + player3.handCards)
  println("player3 pilecards: " + player3.pileCards)
  println("player4 handcards: " + player4.handCards)
  println("player4 pilecards: " + player4.pileCards)
  println("players' names: " + game.players.map( _.name ))

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

  val game3 = new Game(Buffer[Player](), table2, deck2)

  println("\n\nNew game, adding 4 players\n\n")

  game3.playTurn("players 4")

  println("players' names: " + game3.players.map( _.name ))
  println("turn: " + game3.currentPlayer.name)





}
