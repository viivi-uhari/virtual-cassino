package os2.cassino

import scala.collection.mutable.Buffer

class Game(var players: Buffer[Player], var table: OwnTable, var deck: Deck) {

  var currentPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())
  var previousPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())
  var lastPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())

  var cardsInGame = this.table.cards ++ deck.cards

  var error = false

  def playTurn(command: String) = {
    val act = command.takeWhile( _ != ' ')
    val subject = command.dropWhile( _ != ' ').drop(1)
    act match {
      case "players" => {
        for (n <- 1 to subject.toInt) {
          this.players += new Player("Player " + n.toString, Buffer[Card](), Buffer[Card]())
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
          if (this.table.cards.isEmpty) this.currentPlayer.points += 1
          lastPlayer = currentPlayer
          previousPlayer = currentPlayer
          turn()
        } else {
          this.error = true
        }
      }
      case "place" => { //place command just places the current card
        this.currentPlayer.placeCard(currentPlayer.currentCard)
        this.table.addCard(currentPlayer.currentCard)
        if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
        previousPlayer = currentPlayer
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

  def pointCount() = {

    val mostCardsPlayer = this.players.maxBy( _.pileCards.size )
    val mostSpadesPlayer = this.players.maxBy( _.pileCards.count( _.suit == "s" ) )
    this.players.foreach( player => player.points += player.pileCards.count( _.number == 1 ) )
    this.players.filter( _.pileCards.contains(Card(2, "s")) ).foreach( player => player.points += 1 )
    this.players.filter( _.pileCards.contains(Card(10, "d")) ).foreach( player => player.points += 2 )

    mostCardsPlayer.points += 1
    mostSpadesPlayer.points += 2

  }





}
