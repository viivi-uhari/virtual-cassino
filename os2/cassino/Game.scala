package os2.cassino

import java.io.{BufferedReader, BufferedWriter, File, FileNotFoundException, FileReader, FileWriter, IOException}
import scala.collection.mutable.Buffer
import scala.util.Random

class Game(var players: Buffer[Player], val table: OwnTable, val deck: Deck) {

  var currentPlayer = new Player("", Buffer[Card](), Buffer[Card]())   //the palyer whose turn it is
  var previousPlayer = new Player("", Buffer[Card](), Buffer[Card]())  //the player that played the previous turn
  var lastPlayer = new Player("", Buffer[Card](), Buffer[Card]())      //the player that was the last one to take cards off the table

  private var result = "Success"                                       //concerning the load and save methods
  private var error = false                                            //concerning the check/take actions

  //at the beginning cards in game are the deck cards,
  //these are the cards that the computer opponent knows that can be in other players' hands (including its)
  private var cardsStillInGame = deck.cards

  //method to return the cards in game
  def cardsInGame = this.cardsStillInGame

  //a simpler method to get the game's tabel's cads
  def tableCards = this.table.cards

  //method to return the result of the load and save methods
  def giveResult = this.result

  //method to tell if there was an error during the take move
  def giveError = this.error

  //method to add players
  def addPlayers(number: Int) = {
    for (n <- 1 to number) {
      this.players += new Player("Player " + n.toString, Buffer[Card](), Buffer[Card]())
    }
  }

  //method to add computer opponents
  def addComputers(number: Int) = {
    for (n <- 1 to number) {
      this.players += new Computer("Computer " + n.toString, Buffer[Card](), Buffer[Card]())
    }
  }

  //method to start the game: restacks, shuffles, deals and determines the first player
  def start() = {
    this.deck.restack()
    this.deck.shuffle()
    for (player <- players) {
      player.addCards(this.deck.dealCards.toVector)
    }
    table.addCards(this.deck.dealCards.toVector)
    val i = Random.nextInt(this.players.size)
    this.currentPlayer = this.players(i)
    this.cardsStillInGame = this.deck.cards --= this.table.cards    //the dealt table cards can't be in other players' hands
  }

  //method that sets the current player's current card
  def play(card: Card) = this.currentPlayer.playCard(card)

  //method that takes cards of the table to the current player's pile if possible
  def take(cards: Buffer[Card]) = {
    this.error = false
    if (this.currentPlayer.check(this.currentPlayer.currentCard, cards.toVector)) {
      this.currentPlayer.takeCards(cards.toVector)
      this.table.removeCards(cards.toVector)
      if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
      if (this.table.cards.isEmpty) this.currentPlayer.addPoints(1) //if a sweep, gets one point
      this.cardsStillInGame -= this.currentPlayer.currentCard       //now the computer opponents knows the played card isn't in the game anymore,
      this.previousPlayer = currentPlayer                             //the current table cards have already been accounted for during other turns
      this.lastPlayer = currentPlayer                               //the last Player is updated only if the player takes cards off the table
      turn()
    } else {
      this.error = true
    }
  }

  //method that places the current player's current card on the table
  def place() = {
    this.currentPlayer.placeCard(currentPlayer.currentCard)
    this.table.addCard(currentPlayer.currentCard)
    if (this.deck.cards.nonEmpty) this.currentPlayer.addCard(this.deck.removeCard)
    this.cardsStillInGame -= this.currentPlayer.currentCard         //now the computer opponents knows the played card isn't in the game anymore
    this.previousPlayer = this.currentPlayer
    turn()
  }

  //method to clear all if for example a new number for the players is given
  //deck doesn't need to be cleared because it is restacked and shuffled during the start method
  def clearAll() = {
    this.players.clear()
    this.table.cards.clear()
  }

  //method that handles the procedures at the end of a game
  //table needs to be cleared because the last cards are given to the lastPlayer
  def end() = {
    this.lastPlayer.addAtTheEnd(this.table.cards.toVector)
    this.table.cards.clear()
    this.pointCount()
  }

  //method to get the given computer opponnet to make it's move
  //returns the cards to take to report them to the user in the GUI class
  def playComputer(computer: Computer) = {
    val actualCardsInGame = this.cardsInGame --= computer.handCards                       //doesn't need to consider its cards when evaluating its move,
    val cardsToTake = computer.evaluate(tableCards, actualCardsInGame, this.players.size)   //only the other players' possible hand cards
    if (cardsToTake.isEmpty) this.place() else this.take(cardsToTake)
    cardsToTake
  }

  //method to clear the computer opponents if for example a new number for the opponents is given
  def clearComputers(computers: Int) = this.players = this.players.takeWhile( !_.isInstanceOf[Computer] )

  //gives the number of the human players in the game
  def playerNro = this.players.takeWhile( !_.isInstanceOf[Computer] ).size

  //method used in pointCount and save, turns the card into its string form
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

  //changes the turn
  private def turn() = {
    val currentIndex = this.players.indexOf(this.currentPlayer)
    if (currentIndex < players.size - 1) this.currentPlayer = this.players(currentIndex + 1) else this.currentPlayer = this.players.head
  }

  //executes the final point count and returns a string representing its outcome
  def pointCount() = {
    var pointString = ""
    val mostCardsPlayer = this.players.maxBy( _.pileCards.size )
    val mostSpadesPlayer = this.players.maxBy( _.pileCards.count( _.suit == "s" ) )
    this.players.foreach( player => player.addPoints(player.pileCards.count( _.number == 1 )) )
    this.players.filter( _.pileCards.contains(Card(2, "s")) ).foreach( player => player.addPoints(1) )
    this.players.filter( _.pileCards.contains(Card(10, "d")) ).foreach( player => player.addPoints(2) )
    mostCardsPlayer.addPoints(1)
    mostSpadesPlayer.addPoints(2)

    for (player <- this.players) {
      pointString += player.name + ": " + player.tellPoints + " points ("
      if (player == mostCardsPlayer) pointString += "most cards, "
      if (player == mostSpadesPlayer) pointString += "most spades, "
      if (player.pileCards.count( _.number == 1 ) != 0) pointString += player.pileCards.filter( _.number == 1 ).map( cardToString(_) ).mkString(", ") + ", "
      if (player.pileCards.contains(Card(2, "s"))) pointString += "2s, "
      if (player.pileCards.contains(Card(10, "d"))) pointString += "10d, "
      if (pointString.last != '(') pointString = pointString.dropRight(2)
      pointString += ")\n"
    }
    pointString

  }

  //determines the winner(s)
  def winners = {
    var winners = Buffer[Player](this.players.head)
    for (player <- this.players.tail) {
      if (player.tellPoints > winners.head.tellPoints) {
        winners = Buffer[Player](player)
      } else if (player.tellPoints == winners.head.tellPoints) {
        winners += player
      }
    }
    winners
  }

  //used in load to check if the PLR blocks are in order
  private def checkForOrder(numbers: Buffer[Int]) = {                                   // true if all in order
    numbers.zip(numbers.drop(1)).map( n => n._2 - n._1 ).forall( _ == 1 ) ||
      numbers.zip(numbers.drop(1)).map( n => n._2 - n._1 ).isEmpty
  }

  //used in load to check if same cards are given
  private def checkForUnique(cards: Buffer[Card]) = cards.distinct.size == cards.size   // true if all unique

  //for loading a text file, uses a reader that reads the file line by line
  def load(sourceFile: String): Unit = {

    this.result = "Success"

    try {

      val input = new FileReader(sourceFile)
      val lineReader = new BufferedReader(input)

      //inner method to handle the strings of cards
      def handleCards(chars: String): Buffer[Card] = {
        var cards = Buffer[Card]()
        var a = 0
        var string = "0"
        var number = 0
        while (a < chars.length) {
          //first checks for numbers, since they are supposed to come first and registers these in "string"
          if ("123456789QKJA".contains(chars(a))) {
            if (a < chars.length - 1 && "123456789QKJA".contains(chars(a + 1))) {     //checks if the previous character is also a "number"
              this.result = "Failure, wrong order of characters describing cards"
              a = chars.length
            } else if (a == chars.length - 1) {                                       //checks if the last character is a "number"
              this.result = "Failure, wrong order of characters describing cards"
              a = chars.length
            } else if (chars(a) == '1' && chars(a + 1) == '0') {                      //if the number 1 is given, it must be followed by the number 0
              string = "10"                                                           //because 1 is A, 11 is J, 12 is Q and 13 is K
              a += 2                                                                  //goes two steps forward
            } else if (chars(a) == '1' && !(chars(a + 1) == '0')) {
              this.result = "Failure, unknown character describing cards"
              a = chars.length
            } else {                                                                  //if not a 10 and no errors, just one step forward
              string = chars(a).toString
              a += 1
            }
          //then checks for suits and creates the actual cards
          } else if ("scdh".contains(chars(a))) {
            if (a < chars.length - 1 && "scdh".contains(chars(a + 1))) {             //checks if the previous character is also a suit
              this.result = "Failure, wrong order of characters describing cards"
              a = chars.length
            } else {
              string match {
                case "Q" => number = 12
                case "K" => number = 13
                case "J" => number = 11
                case "A" => number = 1
                case s: String => number = s.toInt
              }
            }
            cards += Card(number, chars(a).toString)
            a += 1
          //if doesn't match the previous ones, then an unknown chracter
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

        //variables to be modified while reading the file
        var newPlayers = Map[Int, Player]()
        var computers = Map[Int, Player]()
        var newTable = new OwnTable
        var newCurrentPlayer = new Player("", Buffer[Card](), Buffer[Card]())
        var turnMissing = true

        currentLine = lineReader.readLine()

        //needs an END block
        while (currentLine != "END") {
          currentLine.take(3) match {
            case "PLR" => {
              val playerNumber = currentLine(3).toString.toIntOption  //options to check if the file has the right format
              val nameSize = currentLine(4).toString.toIntOption
              var playerName = ""
              var handChars = ""
              var pileChars = ""
              playerNumber match {
                case Some(number) => {
                  nameSize match {
                    case Some(size) => {
                      playerName = currentLine.slice(5, size + 5)
                      val cards = currentLine.drop(5 + size)
                      handChars = cards.takeWhile(_ != ':')
                      pileChars = cards.dropWhile(_ != ':').drop(1)
                      newPlayers += number -> new Player(playerName, handleCards(handChars), handleCards(pileChars))
                    }
                    case None => this.result = "Failure, no size for player name"
                    }
                }
                case None => this.result = "Failure, no number for player"
              }
              currentLine = lineReader.readLine()
            }
            case "CMP" => {
              val cmpNumber = currentLine(3).toString.toIntOption
              cmpNumber match {
                case Some(number) => {
                  val cards = currentLine.drop(4)
                  val handChars = cards.takeWhile(_ != ':')
                  val pileChars = cards.dropWhile(_ != ':').drop(1)
                  computers += number -> new Computer("Computer " + number, handleCards(handChars), handleCards(pileChars))
                }
                case None => this.result = "Failure, no number for computer opponent"
              }
              currentLine = lineReader.readLine()
            }
            case "TBL" => {
              val cards = currentLine.drop(3)
              newTable.addCards(handleCards(cards).toVector)
              currentLine = lineReader.readLine()
            }
            case "TRN" => {
              var player = ""
              var number: Option[Int] = Some(0)
              if (currentLine.length >= 6) player = currentLine.slice(3, 6)
              if (currentLine.length > 6) number = currentLine.slice(6, 7).toIntOption
              number match {
                case Some(n) => {
                  if (player == "plr" && newPlayers.keys.toVector.contains(n)) {  //the TRN block needs to be before the player/opponent it refers to
                    newCurrentPlayer = newPlayers(n)
                    turnMissing = false
                  } else if (player == "cmp" && computers.keys.toVector.contains(n)) {
                    newCurrentPlayer = computers(n)
                    turnMissing = false
                  }
                }
                case None => this.result = "Failure, no record of turn or false turn"
              }
              currentLine = lineReader.readLine()
            }
            case _ => currentLine = lineReader.readLine()
          }
        }

        val allHandCrads = (newPlayers.values.flatMap(_.handCards) ++ computers.values.flatMap(_.handCards)).toBuffer
        val allPileCrads = (newPlayers.values.flatMap(_.pileCards) ++ computers.values.flatMap(_.pileCards)).toBuffer

        //the result strings speak for themselves
        if (newPlayers.isEmpty) {
          this.result = "Failure, no valid players"
        } else if (newPlayers.size == 1 && computers.isEmpty) {
          this.result = "Failure, no opponents"
        } else if (newPlayers.nonEmpty && !newPlayers.keys.toVector.contains(1) || !checkForOrder(newPlayers.keys.toBuffer)) {
          this.result = "Failure, the players' numbers aren't in order"
        } else if (computers.nonEmpty && !computers.keys.toVector.contains(1) || !checkForOrder(computers.keys.toBuffer)) {
          this.result = "Failure, the computer opponents' numbers aren't in order"
        } else if (turnMissing) {
          this.result = "Failure, no record of turn or false turn"
        } else if (newPlayers.forall( _._2.handCards.isEmpty ) && computers.forall( _._2.handCards.isEmpty )) {  //it isn't wise to load an ended game
          this.result = "Failure, the game has already ended"
        } else if (newPlayers.exists( _._2.handCards.size > 4 ) || computers.exists( _._2.handCards.size > 4 )) {
          this.result = "Failure, too many hand cards"
        } else if (!checkForUnique(allHandCrads ++ allPileCrads)) {
          this.result = "Failure, same cards"
        }

        //if no errors, the new game is created (actually the existing one modified)
        if (result == "Success") {
          val newDeck = new Deck
          newDeck.restack()
          newDeck.removeCards(table.cards.toVector ++ allHandCrads ++ allPileCrads)
          newDeck.shuffle()

          this.players.clear()
          this.players = (newPlayers.values ++ computers.values).toBuffer

          this.table.cards.clear()
          this.table.cards ++= newTable.cards
          this.deck.cards.clear()
          this.deck.cards ++= newDeck.cards
          this.currentPlayer = newCurrentPlayer
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

  //method to save the game, uses a buffered writer
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
        for (i <- this.players.indices) {
          val player = this.players(i)
          val cardString = player.handCards.map( cardToString(_) ).mkString + ":" + player.pileCards.map( cardToString(_) ).mkString
          val start = if (player.isInstanceOf[Computer]) "CMP" + (player.name.last).toString else "PLR" + (i + 1).toString + player.name.length + player.name
          playerStrings += start + cardString
        }
        playerStrings.foreach( n => bw.write(n + "\n") )

        val tableString = "TBL" + this.table.cards.map( cardToString(_) ).mkString
        bw.write(tableString)
        bw.write("\n")

        var turnString = "TRN"
        for (i <- this.players.indices) {
          if (this.players(i) == this.currentPlayer) {
            if (this.players(i).isInstanceOf[Computer]) turnString += "cmp" + (this.players(i).name.last).toString else turnString += "plr" + (i + 1).toString
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
