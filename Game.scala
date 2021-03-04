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
        if (check(this.currentPlayer.currentCard, cards.toVector)) {
          this.currentPlayer.takeCards(cards.toVector)
          this.table.removeCards(cards.toVector)
          if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
          turn()
          // else feedback
        }
      }
      case "place" => {
        this.currentPlayer.playCard(Card(subject.head.toString.toInt, subject.tail))
        this.table.addCard(Card(subject.head.toString.toInt, subject.tail))
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

  def check(playedCard: Card, wantedCards: Vector[Card]) = {
    var result = true
    var wantedNumbers = wantedCards.map( _.number ).toBuffer
    for (n <- wantedNumbers) {
      if (n > playedCard.handNumber) {
        result = false
      } else if (n == playedCard.handNumber) {
        wantedNumbers -= n
      }
    }

    if (result) {
      var originalList = wantedNumbers
      while (wantedNumbers.nonEmpty && result) {
        val sum = wantedNumbers.head + wantedNumbers(1)
        if (sum == playedCard.handNumber) {
          wantedNumbers --= wantedNumbers.take(2)
          originalList = wantedNumbers
        } else if (sum < playedCard.handNumber) {
          if (wantedNumbers.size > 2) {
            wantedNumbers --= wantedNumbers.take(2)
            wantedNumbers = sum +: wantedNumbers
            originalList = wantedNumbers
          } else {
            result = false
          }
        } else {
          val second = wantedNumbers(1)
          wantedNumbers = wantedNumbers.head +: wantedNumbers.drop(2)
          wantedNumbers += second
          if (wantedNumbers == originalList) result = false
        }
      }
    }
    result
  }

}
