package os2.cassino

import scala.collection.mutable.Buffer

class Game(var players: Buffer[Player], val table: OwnTable, val deck: Deck) {

  var currentPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())

  var cardsInGame = this.table.cards ++ deck.cards

  def playTurn(command: String) = {
    val act = command.takeWhile( _ != ' ')
    val subject = command.dropWhile( _ != ' ').drop(1)
    act match {
      case "players" => {
        for (n <- 1 to subject.toInt) {
          this.players += new Player("os2.cassino.Player " + n.toString, Buffer[Card](), Buffer[Card]())
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
      case "play" => this.currentPlayer.playCard(subjectToCard(subject))
      case "take" => {
        val strings = subject.split(", ")
        val cards = for {
          string <- strings
        } yield subjectToCard(string)

        if (this.currentPlayer.check(this.currentPlayer.currentCard, cards.toVector)) {
          this.currentPlayer.takeCards(cards.toVector)
          this.table.removeCards(cards.toVector)
          if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
          turn()
          // else feedback
        }
      }
      case "place" => {
        this.currentPlayer.placeCard(subjectToCard(subject))
        this.table.addCard(subjectToCard(subject))
        turn()
      }
      case "end" => {
        this.players = this.players.empty
        this.table.cards = this.table.cards.empty
      }
    }
  }

  private def subjectToCard(subject: String) = {
    var card = this.currentPlayer.currentCard
    var second = subject(1)
    val end = subject(subject.length - 1).toString
    subject.head match {
      case 'Q' => card = Card(12, end)
      case 'K' => card = Card(13, end)
      case 'J' => card = Card(11, end)
      case 'A' => card = Card(1, end)
      case '1' if (second == '0') => card = Card(10, end)
      case '1' if (second == '1') => card = Card(11, end)
      case '1' if (second == '2') => card = Card(12, end)
      case '1' if (second == '3') => card = Card(13, end)
      case a: Char => card = Card(a.toString.toInt, end)
    }
    card
  }

  private def turn() = {
    val currentIndex = this.players.indexOf(this.currentPlayer)
    if (currentIndex < players.size - 1) this.currentPlayer = this.players(currentIndex + 1) else this.currentPlayer = this.players.head
  }






}