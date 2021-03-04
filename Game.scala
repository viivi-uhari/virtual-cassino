import scala.collection.mutable.Buffer

class Game(var players: Buffer[Player], val table: Table, val deck: Deck) {

  private var currentPlayer = players.head

  var cardsInGame = table.cards ++ deck.cards

  def playTurn(command: String) = {
    val act = command.takeWhile( _ != ' ')
    val subject = command.dropWhile( _ != ' ').drop(1)
    act match {
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
        this.currentPlayer.takeCards(cards.toVector)
        this.table.removeCards(cards.toVector)
        if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
      }
      case "place" => {
        this.currentPlayer.playCard(Card(subject.head.toString.toInt, subject.tail))
        this.table.addCard(Card(subject.head.toString.toInt, subject.tail))
      }
      case "end" => {
        this.players.empty
        this.table.cards.empty
        this.deck.restack()
      }
    }
  }

}
