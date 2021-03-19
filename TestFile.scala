import java.io.{BufferedReader, FileReader, IOException, Reader, StringReader}
import scala.collection.mutable.Buffer

object TestFile extends App{

  val input = new FileReader("/Users/viiviuhari/IdeaProjects/OS2-Project/exampleFile.txt")

  // now load returns a Game, could also modify an excisting Game

  def load(input: Reader): Game = {

    val lineReader = new BufferedReader(input)

    try {

      var currentLine = lineReader.readLine()

      // if (!(currentLine == "CASSINO"))

      def handleCards(chars: String): Buffer[Card] = {
        var cards = Buffer[Card]()
        var a = 0
        var string = "0"
        var number = 0
        while (a < chars.length) {
          if ("scdh".contains(chars(a))) {
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
            if (chars(a) == '1') {
              string = "10"
              a += 2
            } else {
              string = chars(a).toString
              a += 1
            }
          }
        }
        cards
      }

      var players = Map[Int, Player]()
      var table = new Table
      var currentPlayer = new Player("", Buffer[Card](), Buffer[Card]())

      currentLine = lineReader.readLine()

      while (currentLine != "END") {
        currentLine.take(3) match {
          case "PLR" => {
            while (currentLine startsWith "PLR") {
              val playerNumber = currentLine(3).toString.toInt
              val nameSize = currentLine(4).toString.toInt
              val playerName = currentLine.slice(5, nameSize + 5)
              val cards = currentLine.drop(5 + nameSize)
              val handChars = cards.takeWhile( _ != ':' )
              val pileChars = cards.dropWhile( _ != ':' ).drop(1)

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
          }
          case _ => currentLine = lineReader.readLine()
        }
      }

      val deck = new Deck
      deck.restack()
      val allHandCrads = players.values.flatMap( _.handCards )
      val allPileCrads = players.values.flatMap( _.pileCards )
      deck.removeCards(table.cards.toVector ++ allHandCrads ++ allPileCrads)
      deck.shuffle()

      val game = new Game
      game.players = players.values.toBuffer
      game.table = table
      game.deck = deck
      game.currentPlayer = currentPlayer
      game

    } catch { // better exception handeling
      case e: IOException =>
        throw e
        // println("Reading finished with error")
    }

  }


  val game = load(input)
  println(game.players.map( _.name ))
  println(game.players.map( _.handCards ))
  println(game.players.map( _.pileCards ))
  println(game.table.cards)
  println(game.deck.cards)
  println(game.currentPlayer.name)




}
