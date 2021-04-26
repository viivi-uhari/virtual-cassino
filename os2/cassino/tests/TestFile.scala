package os2.cassino.tests

import os2.cassino._

import java.io._
import scala.collection.mutable.Buffer

object TestFile extends App {

  //tests the Game class's save and load methods

  val table = new OwnTable
  val deck = new Deck
  val game = new Game(Buffer[Player](), this.table, this.deck)

  println(game.load("exampleFile.txt"))
  println(game.players.map(_.name))
  println(game.players.map(_.handCards))
  println(game.players.map(_.pileCards))
  println(game.table.cards)
  println(game.deck.cards)
  println(game.currentPlayer.name)
  println(game.giveResult)

  //works!
  //println(game.save("exampleWritten.txt"))

}
