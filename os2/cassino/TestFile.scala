package os2.cassino

import java.io.{BufferedReader, BufferedWriter, File, FileNotFoundException, FileReader, FileWriter, IOException, Reader}
import scala.collection.mutable.Buffer
import scala.reflect.internal.util.FileUtils.LineWriter

object TestFile extends App {

  var result = "Success, no errors"

  val table = new OwnTable
  val deck = new Deck
  val game = new Game(Buffer[Player](), this.table, this.deck)

  def load(sourceFile: String): Unit = {

    result = "Success, no errors"

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
            result = "Failure, unknown character describing cards"
            a = chars.length
          }
        }
        cards
      }

      try {

        var currentLine = lineReader.readLine()
        if (!(currentLine == "CASSINO")) result = "Failure, unknown file type"

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

        if (result.takeWhile( _ != ',' ) == "Success") {
          val deck = new Deck
          deck.restack()
          val allHandCrads = players.values.flatMap(_.handCards) ++ computers.values.flatMap(_.handCards)
          val allPileCrads = players.values.flatMap(_.pileCards) ++ computers.values.flatMap(_.pileCards)
          deck.removeCards(table.cards.toVector ++ allHandCrads ++ allPileCrads)
          deck.shuffle()

          game.players = (players.values ++ computers.values).toBuffer
          game.table = table
          game.deck = deck
          game.currentPlayer = currentPlayer
        }

      } finally {
        input.close()
        lineReader.close()
      }
    } catch {
      case notFound: FileNotFoundException => result = "Failure, no file found"
      case e: IOException => result = "Failure, IOException"
    }
  }

  def save(fileName: String): Unit = {

    this.result = "Success, no errors"

    try {
      val file = new File(fileName)
      val writer = new FileWriter(file)
      val bw = new BufferedWriter(writer)

      try {
        bw.write("CASSINO")
        bw.write("\n")

        val playerStrings = Buffer[String]()
        val playersNo = game.players.takeWhile( plr => !plr.isInstanceOf[Computer] ).size
        for (i <- game.players.indices) {
          val player = game.players(i)
          val cardString = player.handCards.map( game.cardToString(_) ).mkString + ":" + player.pileCards.map( game.cardToString(_) ).mkString
          val start = if (player.isInstanceOf[Computer]) "CMP" + (i + 1 - playersNo).toString else "PLR" + (i + 1).toString + player.name.length + player.name
          playerStrings += start + cardString
        }
        playerStrings.foreach( n => bw.write(n + "\n") )

        val tableString = "TBL" + game.table.cards.map( game.cardToString(_) ).mkString
        bw.write(tableString)
        bw.write("\n")

        var turnString = "TRN"
        for (i <- game.players.indices) {
          if (game.players(i) == game.currentPlayer) {
            if (game.players(i).isInstanceOf[Computer]) turnString += "cmp" + (i + 1 - playersNo).toString else turnString += "plr" + (i + 1).toString
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



  println(load("exampleFile.txt"))
  println(game.players.map(_.name))
  println(game.players.map(_.handCards))
  println(game.players.map(_.pileCards))
  println(game.table.cards)
  println(game.deck.cards)
  println(game.currentPlayer.name)

  //works!
  println(save("exampleWritten.txt"))

}
