import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import scala.collection.mutable.Buffer

class Game(var players: Buffer[Player], val table: Table, val deck: Deck) {

  var currentPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())

  var cardsInGame = table.cards ++ deck.cards

  def playTurn(command: String) = {
    val act = command.takeWhile( _ != ' ')
    val subject = command.dropWhile( _ != ' ').drop(1)
    act match {
      case "players" => {
        for (n <- 1 to subject.toInt) {
          this.players += new Player("player" + n.toString, Buffer[Card](), Buffer[Card]())
        }
        this.currentPlayer = players.head
      }
      case "start" => {
        this.deck.restack()
        this.deck.shuffle()
        for (player <- players) {
          player.addCards(this.deck.dealCards.toVector)
        }
        table.addCards(this.deck.dealCards.toVector)
      }
      case "play" => this.currentPlayer.playCard(Card(subject.head.toString.toInt, subject.tail))
      case "take" => {
        val strings = subject.split(", ")
        val cards = for {
          string <- strings
        } yield Card(string.head.toString.toInt, string.tail)
        if (this.currentPlayer.check(this.currentPlayer.currentCard, cards.toVector)) {
          this.currentPlayer.takeCards(cards.toVector)
          this.table.removeCards(cards.toVector)
          if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
          turn()
          // else feedback
        }
      }
      case "place" => {
        val card = Card(subject.head.toString.toInt, subject.tail)
        this.currentPlayer.playCard(card)
        this.table.addCard(card)
        turn()
      }
      case "end" => {
        this.players = this.players.empty
        this.table.cards = this.table.cards.empty
      }
    }
  }

  private def turn() = {
    val currentIndex = this.players.indexOf(this.currentPlayer)
    if (currentIndex < players.size - 1) this.currentPlayer = this.players(currentIndex + 1) else this.currentPlayer = this.players.head
  }






}
