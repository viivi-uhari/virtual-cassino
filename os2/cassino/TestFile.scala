package os2.cassino

import java.io.{BufferedReader, BufferedWriter, File, FileNotFoundException, FileReader, FileWriter, IOException, Reader}
import scala.collection.mutable.Buffer
import scala.reflect.internal.util.FileUtils.LineWriter

object TestFile extends App {



  def load(sourceFile: String, game: Game): String = {

    var result = "Success, no errors"

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
            case "TBL" => {
              val cards = currentLine.drop(3)
              table.addCards(handleCards(cards).toVector)
              currentLine = lineReader.readLine()
            }
            case "TRN" => {
              val player = currentLine.slice(3, 6)
              val number = currentLine.slice(6, 7).toInt
              if (player == "plr") currentPlayer = players(number)
              currentLine = lineReader.readLine()
              turnMissing = false
            }
            case _ => currentLine = lineReader.readLine()
          }
        }

        if (players.isEmpty) {
          result = "Failure, no players"
        //} else if (players.size == 1) {
        //  result = "Failure, no opponents"
        } else if (turnMissing) {
          result = "Failure, no record of turn"
        }

        if (result.takeWhile( _ != ',' ) == "Success") {
          val deck = new Deck
          deck.restack()
          val allHandCrads = players.values.flatMap(_.handCards)
          val allPileCrads = players.values.flatMap(_.pileCards)
          deck.removeCards(table.cards.toVector ++ allHandCrads ++ allPileCrads)
          deck.shuffle()

          game.players = players.values.toBuffer
          game.table = table
          game.deck = deck
          game.currentPlayer = currentPlayer
          result
        } else {
          result
        }

      } finally {
        input.close()
        lineReader.close()
      }
    } catch {
      case notFound: FileNotFoundException => {
        result = "Failure, no file found"
        result
      }
      case e: IOException => {
        result = "Failure, IOException"
        result
      }
    }
  }

  def save(fileName: String, game: Game): String = {

    var result = "Success, no errors"

    try {
      val file = new File(fileName)
      val writer = new FileWriter(file)
      val bw = new BufferedWriter(writer)

      try {
        bw.write("CASSINO")
        bw.write("\n")

        val playerStrings = Buffer[String]()
        for (i <- game.players.indices) {
          val player = game.players(i)
          val cardString = player.handCards.map( cardToString(_) ).mkString + ":" + player.pileCards.map( cardToString(_) ).mkString
          playerStrings += "PLR" + (i + 1).toString + player.name.length + player.name + cardString
        }
        playerStrings.foreach( n => bw.write(n + "\n") )

        val tableString = "TBL" + game.table.cards.map( cardToString(_) ).mkString
        bw.write(tableString)
        bw.write("\n")

        var turnString = "TRN"
        for (i <- game.players.indices) {
          if (game.players(i) == game.currentPlayer) turnString += "plr" + (i + 1).toString
        }
        bw.write(turnString)
        bw.write("\n")
        bw.write("END")
        result

      } finally {
        bw.close()
        writer.close()
      }
    } catch {
      case notFound: FileNotFoundException => {
        result = "Failure, no file found"
        result
      }
      case e: IOException => {
        result = "Failure, IOException"
        result
      }
    }
  }


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

  val table = new OwnTable
  val deck = new Deck
  val game = new Game(Buffer[Player](), this.table, this.deck)

  println(load("/Users/viiviuhari/IdeaProjects/OS2-Project/exampleFile.txt", game))
  println(game.players.map(_.name))
  println(game.players.map(_.handCards))
  println(game.players.map(_.pileCards))
  println(game.table.cards)
  println(game.deck.cards)
  println(game.currentPlayer.name)

  //works!
  //println(save("exampleWritten.txt", game))

}
