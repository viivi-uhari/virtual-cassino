package os2.cassino

import java.io.{BufferedReader, BufferedWriter, File, FileNotFoundException, FileReader, FileWriter, IOException}
import scala.collection.mutable.Buffer
import scala.util.Random

class Game(var players: Buffer[Player], var table: OwnTable, var deck: Deck) {

  var currentPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())
  var previousPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())  //the player that played the previous turn
  var lastPlayer = new Player("replaceable", Buffer[Card](), Buffer[Card]())      //the player that was the last one to take cards off the table

  def cardsInGame = this.table.cards ++ deck.cards

  def tableCards = this.table.cards

  var result = "Success, no errors"

  var error = false

  def addPlayers(number: Int) = {
    for (n <- 1 to number) {
      this.players += new Player("Player " + n.toString, Buffer[Card](), Buffer[Card]())
    }
  }

  def addComputers(number: Int) = {
    for (n <- 1 to number) {
      this.players += new Computer("Computer " + n.toString, Buffer[Card](), Buffer[Card]())
    }
  }

  def start() = {
    this.deck.restack()
    this.deck.shuffle()
    for (player <- players) {
      player.addCards(this.deck.dealCards.toVector)
    }
    table.addCards(this.deck.dealCards.toVector)
    val i = Random.nextInt(this.players.size)
    this.currentPlayer = this.players(i)
  }

  def play(card: Card) = this.currentPlayer.playCard(card)

  def take(cards: Buffer[Card]) = {
    this.error = false
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

  def place() = {
    println("inside place " + currentPlayer.name)
    println(currentPlayer.currentCard)
    println(table.cards)
    this.currentPlayer.placeCard(currentPlayer.currentCard)
    this.table.addCard(currentPlayer.currentCard)
    if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
    previousPlayer = currentPlayer
    turn()
  }

  def end() = {
    this.players.clear()
    this.table.cards.clear()
  }


  /*def playTurn(command: String) = {
    val act = command.takeWhile( _ != ' ')
    val subject = command.dropWhile( _ != ' ').drop(1)
    act match {
      case "players" => {
        for (n <- 1 to subject.toInt) {
          this.players += new Player("Player " + n.toString, Buffer[Card](), Buffer[Card]())
        }
        this.currentPlayer = players.head
      }
      case "computers" => {
        for (n <- 1 to subject.toInt) {
          this.players += new Computer("Computer " + n.toString, Buffer[Card](), Buffer[Card]())
        }
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
        this.error = false
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
  }*/

  def playComputer(computer: Computer) = {
    val cardsToTake = computer.evaluate(tableCards, this.cardsInGame, this.players.size)
    if (cardsToTake.isEmpty) this.place() else this.take(cardsToTake)
    cardsToTake
  }

  def eraseComputers(computers: Int) = { this.players = this.players.dropRight(computers) }

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

  /*def cardsToSubject(cards: Buffer[Card]): String = {
    var string = ""
    for (card <- cards) {
      string += card.number.toString + card.suit + ", "
    }
    string.trim.dropRight(1)
  }*/

  def cardToString(card: Card): String = {
    var number = ""
    card.number match {
      case 1 => number = "A"
      case 11 => number = "J"
      case 12 => number = "Q"
      case 13 => number = "K"
      case a: Int => number = a.toString
    }
    number + card.suit
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

  def winners = {
    var winners = Buffer[Player](this.players.head)
    for (player <- this.players.tail) {
      if (player.points > winners.head.points) {
        winners = Buffer[Player](player)
      } else if (player.points == winners.head.points) {
        winners += player
      }
    }
    winners
  }

  def load(sourceFile: String): Unit = {

    this.result = "Success"

    try {

      val input = new FileReader(sourceFile)
      val lineReader = new BufferedReader(input)

      def handleCards(chars: String): Buffer[Card] = {
        var cards = Buffer[Card]()
        var a = 0
        var string = "0"
        var number = 0
        while (a < chars.length) {
          if ("123456789QKJA".contains(chars(a))) {
            if (chars(a) == '1') {
              string = "10"
              a += 2
            } else {
              string = chars(a).toString
              a += 1
            }
          } else if ("scdh".contains(chars(a))) {
            string match {
              case "Q" => number = 12
              case "K" => number = 13
              case "J" => number = 11
              case "A" => number = 1
              case s: String => number = s.toInt
            }
            cards += Card(number, chars(a).toString)
            a += 1
          } else {
            this.result = "Failure, unknown character describing cards"
            a = chars.length
          }
        }
        cards
      }

      try {

        var currentLine = lineReader.readLine()
        if (!(currentLine == "CASSINO")) this.result = "Failure, unknown file type"

        var players = Map[Int, Player]()
        var computers = Map[Int, Player]()
        var table = new OwnTable
        var currentPlayer = new Player("", Buffer[Card](), Buffer[Card]())
        var turnMissing = true

        currentLine = lineReader.readLine()

        while (currentLine != "END") {
          currentLine.take(3) match {
            case "PLR" => {
              while (currentLine startsWith "PLR") {
                val playerNumber = currentLine(3).toString.toInt        //Option
                val nameSize = currentLine(4).toString.toInt            //Option
                val playerName = currentLine.slice(5, nameSize + 5)
                val cards = currentLine.drop(5 + nameSize)
                val handChars = cards.takeWhile(_ != ':')
                val pileChars = cards.dropWhile(_ != ':').drop(1)

                players += playerNumber -> new Player(playerName, handleCards(handChars), handleCards(pileChars))
                currentLine = lineReader.readLine()
              }
            }
            case "CMP" => {
              while (currentLine startsWith "CMP") {
                val cmpNumber = currentLine(3).toString.toInt          //Option
                val cards = currentLine.drop(4)
                val handChars = cards.takeWhile(_ != ':')
                val pileChars = cards.dropWhile(_ != ':').drop(1)
                computers += cmpNumber -> new Computer("Computer " + cmpNumber, handleCards(handChars), handleCards(pileChars))
                currentLine = lineReader.readLine()
              }
            }
            case "TBL" => {
              val cards = currentLine.drop(3)
              table.addCards(handleCards(cards).toVector)
              currentLine = lineReader.readLine()
            }
            case "TRN" => {
              val player = currentLine.slice(3, 6)
              val number = currentLine.slice(6, 7).toInt
              if (player == "plr") currentPlayer = players(number) else currentPlayer = computers(number)
              currentLine = lineReader.readLine()
              turnMissing = false
            }
            case _ => currentLine = lineReader.readLine()
          }
        }

        if (players.isEmpty) {
          this.result = "Failure, no players"
        } else if (players.size == 1 && computers.isEmpty) {
          this.result = "Failure, no opponents"
        } else if (turnMissing) {
          this.result = "Failure, no record of turn"
        } else if (players.forall( _._2.handCards.isEmpty ) && computers.forall( _._2.handCards.isEmpty )) {
          this.result = "Failure, the game has already ended"
        }

        if (result == "Success") {
          val deck = new Deck
          deck.restack()
          val allHandCrads = players.values.flatMap(_.handCards) ++ computers.values.flatMap(_.handCards)
          val allPileCrads = players.values.flatMap(_.pileCards) ++ computers.values.flatMap(_.pileCards)
          deck.removeCards(table.cards.toVector ++ allHandCrads ++ allPileCrads)
          deck.shuffle()

          this.players = (players.values ++ computers.values).toBuffer
          this.table = table
          this.deck = deck
          this.currentPlayer = currentPlayer
        }

      } finally {
        input.close()
        lineReader.close()
      }
    } catch {
      case notFound: FileNotFoundException => this.result = "Failure, no file found"
      case e: IOException => this.result = "Failure, IOException"
    }
  }

  def save(fileName: String): Unit = {

    this.result = "Success"

    try {
      val file = new File(fileName)
      val writer = new FileWriter(file)
      val bw = new BufferedWriter(writer)

      try {
        bw.write("CASSINO")
        bw.write("\n")

        val playerStrings = Buffer[String]()
        val playersNo = this.players.takeWhile( plr => !plr.isInstanceOf[Computer] ).size
        for (i <- this.players.indices) {
          val player = this.players(i)
          val cardString = player.handCards.map( cardToString(_) ).mkString + ":" + player.pileCards.map( cardToString(_) ).mkString
          val start = if (player.isInstanceOf[Computer]) "CMP" + (i + 1 - playersNo).toString else "PLR" + (i + 1).toString + player.name.length + player.name
          playerStrings += start + cardString
        }
        playerStrings.foreach( n => bw.write(n + "\n") )

        val tableString = "TBL" + this.table.cards.map( cardToString(_) ).mkString
        bw.write(tableString)
        bw.write("\n")

        var turnString = "TRN"
        for (i <- this.players.indices) {
          if (this.players(i) == this.currentPlayer) {
            if (this.players(i).isInstanceOf[Computer]) turnString += "cmp" + (i + 1 - playersNo).toString else turnString += "plr" + (i + 1).toString
          }
        }
        bw.write(turnString)
        bw.write("\n")
        bw.write("END")

      } finally {
        bw.close()
        writer.close()
      }
    } catch {
      case notFound: FileNotFoundException => this.result = "Failure, no file found"
      case e: IOException => this.result = "Failure, IOException"
    }
  }


}
