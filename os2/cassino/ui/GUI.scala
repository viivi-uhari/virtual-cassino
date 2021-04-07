package os2.cassino.ui

import os2.cassino._

import java.awt.{Color, Font}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.mutable.Buffer
import scala.swing.event.{ButtonClicked, MouseClicked}
import scala.swing.{BoxPanel, Button, Component, Dimension, Graphics2D, GridPanel, Image, Label, MainFrame, Orientation, Panel, SimpleSwingApplication, TextArea, TextField}

object GUI extends SimpleSwingApplication {

  val cardWidth = 54
  val cardHeight = 90
  val cardFont = new Font("TimesRoman", Font.PLAIN, 30)
  val nameFont = new Font("Calibri", Font.PLAIN, 20)
  val green = new Color(129, 193, 133)
  val red = new Color(243, 64, 64)
  val white = new Color(252, 250, 250)
  val black = new Color(0, 0, 0)

  val hearts = ImageIO.read(new File("heart.png"))
  val diamonds = ImageIO.read(new File("diamond.png"))
  val clubs = ImageIO.read(new File("clubs.png"))
  val spades = ImageIO.read(new File("spades.png"))
  val suitWidth = 30
  val suitHeight = 30


  val playerButton = new Button("Confirm")
  val compButton = new Button("Confirm")
  val startButton = new Button("start")
  val sourceField = new TextField
  val sourceField2 = new TextField
  val outPut = new TextArea("", 30, 100)
  outPut.editable = false

  val saveField = new TextField
  val saveEndButton = new Button("save")
  val playButton = new Button("Play again")
  val endOutPut = new TextArea("", 30, 100)
  endOutPut.editable = false
  //val feedback = new TextArea("", 30, 10)
  //feedback.editable = false

  val start = new BoxPanel(Orientation.Vertical) {

    contents += new Label("Welcome to play Cassino!")
    contents += new BoxPanel(Orientation.Horizontal) {
      val playerLabel = new Label("How many players?")
      contents += playerLabel
      contents += sourceField
      contents += playerButton
    }
    contents += new BoxPanel(Orientation.Horizontal) {
      val compLabel = new Label("How many computer opponents?")
      contents += compLabel
      contents += sourceField2
      contents += compButton
    }
    contents += startButton
    contents += outPut

  }

  val end = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Game Ended")
    contents += endOutPut
    contents += new Label("The file's name you want the game to be saved to:")
    contents += saveField
    contents += saveEndButton
    contents += playButton
  }


  val tableCards = new GridPanel(5, 4) {
    background = green
  }

  val deckPic = new Panel {
    val x = 33
    val y = 5

    override def paintComponent(g: Graphics2D) = {
      g.setColor(red)
      g.fillRect(x, y, cardWidth, cardHeight)
    }
  }
  def empty = {
    val emptyCardPanel = new Panel {
      override def paintComponent(g: Graphics2D) = {
        g.setColor(green)
        g.fillRect(x, y, cardWidth, cardHeight)
      }
    }
    emptyCardPanel
  }

  val confirmButton = new Button("Confirm")
  val clearButton = new Button("Clear")
  val endButton = new Button("End")
  val saveButton = new Button("Save")

  val buttons = new GridPanel(4, 1) {
    background = green
    contents += confirmButton
    contents += clearButton
    contents += endButton
    contents += saveButton
  }

  val deckButtons = new GridPanel(1, 2) {
    background = green
    minimumSize = new Dimension(240, 130)
    preferredSize = new Dimension(240, 130)
    maximumSize = new Dimension(240, 130)
    contents += buttons
  }

  val right = new GridPanel(6, 1) {
    background = green

  }
  val center = new BoxPanel(Orientation.Vertical) {
    background = green
  }
  val left = new GridPanel(6, 1) {
    background = green
  }

  val play = new GridPanel(1, 3) {
    contents += right
    contents += center
    contents += left
  }

  val deck = new Deck
  val table = new OwnTable
  val game = new Game(Buffer[Player](), this.table, this.deck)
  var toTake = ""

  val turn = new Label("It's " + this.game.currentPlayer.name + "'s turn.") {
    minimumSize = new Dimension(240, 40)
    preferredSize = new Dimension(240, 40)
    maximumSize = new Dimension(240, 40)
  }
  val take = new Label("Taking: ") {
    minimumSize = new Dimension(240, 40)
    preferredSize = new Dimension(240, 40)
    maximumSize = new Dimension(240, 40)
  }
  val current = new Label("Playing: ") {
    minimumSize = new Dimension(240, 40)
    preferredSize = new Dimension(240, 40)
    maximumSize = new Dimension(240, 40)
  }
  val valid = new Label("Valid move?") {
    minimumSize = new Dimension(240, 40)
    preferredSize = new Dimension(240, 40)
    maximumSize = new Dimension(240, 40)
  }


  val x = 3
  val y = 5

  def suitPic(suit: String): Image = {
    var pic = hearts
    suit match {
      case "h" => pic = hearts
      case "d" => pic = diamonds
      case "c" => pic = clubs
      case "s" => pic = spades
    }
    pic
  }

  def numberString(number: Int): String = {
    var string = ""
    number match {
      case 1 => string = "A"
      case 11 => string = "J"
      case 12 => string = "Q"
      case 13 => string = "K"
      case a: Int => string = a.toString
    }
    string
  }

  def frontCard(card: Card) = {
    val frontCardPanel = new Panel {
      override def paintComponent(g: Graphics2D) = {
        g.setColor(white)
        g.fillRect(x, y, cardWidth, cardHeight)
        g.setFont(cardFont)
        g.setColor(black)
        g.drawString(numberString(card.number), x + 18, y + 37)
        g.drawImage(suitPic(card.suit), x + 12, y + 50, suitWidth, suitHeight, null)
      }
    }
    frontCardPanel
  }

  def selectedCard(card: Card): Panel = {
    val selectedCardPanel = new Panel {
      override def paintComponent(g: Graphics2D) = {
        g.setColor(white)
        g.drawRect(x, y, cardWidth, cardHeight)
        g.setColor(white)
        g.fillRect(x, y, cardWidth, cardHeight)
        g.setFont(cardFont)
        g.setColor(black)
        g.drawString(numberString(card.number), x + 18, y + 37)
        g.drawImage(suitPic(card.suit), x + 12, y + 50, suitWidth, suitHeight, null)
      }
    }
    selectedCardPanel
  }

  def backCard = {
    val backCardPanel = new Panel {
      override def paintComponent(g: Graphics2D) = {
        g.setColor(red)
        g.fillRect(x, y, cardWidth, cardHeight)
      }
    }
    backCardPanel
  }

  var playerPanels = Buffer[(Player, Buffer[Component])]()
  var tablePanels = Buffer[(Card, Component)]()
  var currentPanel: (Buffer[Card], Buffer[Component]) = (Buffer(Card(1, "s")), Buffer(frontCard(Card(1, "s"))))
  var previousPanel: (Buffer[Card], Buffer[Component]) = (Buffer(Card(1, "s")), Buffer(frontCard(Card(1, "s"))))
  var cardPanelPairs = currentPanel._1.zip(currentPanel._2)
  var cardPanelPairs2 = previousPanel._1.zip(previousPanel._2)


  def setUp() = {

    for (i <- game.players.indices) {
      val fourCards = new GridPanel(1, 4) {
        background = green
      }
      if (game.players(i) == game.currentPlayer) {
        for (card <- game.players(i).handCards) {
          fourCards.contents += this.frontCard(card)
        }
      } else {
        for (card <- game.players(i).handCards) {
        fourCards.contents += this.backCard
        }
      }
      val name = new Label(game.players(i).name)
      val box = new BoxPanel(Orientation.Vertical)
      box.background = green
      box.contents += fourCards
      box.contents += name
      if (i % 2 == 0) right.contents += box else left.contents += box
    }

    for (card <- table.cards) {
      tableCards.contents += frontCard(card)
    }
    if (game.deck.cards.nonEmpty) deckButtons.contents += deckPic
    this.turn.text = "It's " + this.game.currentPlayer.name + "'s turn."
    center.contents += turn
    center.contents += valid
    center.contents += take
    center.contents += current
    center.contents += deckButtons
    center.contents += tableCards

    val rightCardsPanels = right.contents.map(_.asInstanceOf[BoxPanel].contents.head.asInstanceOf[GridPanel].contents)
    val leftCardsPanels = left.contents.map(_.asInstanceOf[BoxPanel].contents.head.asInstanceOf[GridPanel].contents)

    var cardPanels = IndexedSeq[Buffer[Component]]()
    for (i <- rightCardsPanels.indices) {
      cardPanels = cardPanels :+ rightCardsPanels(i)
      if (i < leftCardsPanels.size) cardPanels = cardPanels :+ leftCardsPanels(i)
    }

    //matching the currentPlayer's cards with their swing components
    playerPanels.clear()
    for (i <- cardPanels.indices) {
      playerPanels += ((game.players(i), cardPanels(i)))
    }
    for (i <- playerPanels.indices) {
      if (playerPanels(i)._1 == game.currentPlayer) {
        currentPanel = (playerPanels(i)._1.handCards, playerPanels(i)._2)
      }
    }
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)
    currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))  //only needs to listen to the current cards

    //getting the GUI to listen to the table's cards
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))

    //matching the table's cards with their swing components
    tablePanels.clear()
    tablePanels = tableCards.contents.indices.map( i => (game.table.cards(i), tableCards.contents(i)) ).toBuffer

  }



  def draw() = {

    println(game.previousPlayer.name)
    println(game.currentPlayer.name)

    // handling the players' cards
    for (i <- playerPanels.indices) {
      if (game.players(i) == game.currentPlayer) {
        for (n <- game.players(i).handCards.indices) {
          playerPanels(i)._2(n) = frontCard(game.players(i).handCards(n))
          this.listenTo(playerPanels(i)._2(n))
        }
      } else if (game.players(i) == game.previousPlayer) {
        var n = 0
        while(n < game.players(i).handCards.size) {
          playerPanels(i)._2(n) = backCard
          n += 1
        }
        while(n < playerPanels(i)._2.size) {
          playerPanels(i)._2(n) = empty
          n += 1
        }
      }
    }

    for (i <- playerPanels.indices) {
      if (playerPanels(i)._1 == game.currentPlayer) {
        currentPanel = (playerPanels(i)._1.handCards, playerPanels(i)._2)
      }
    }
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)        //doesn't listen to the old cards anymore, only has as many pairs as hand cards
    currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))  //only needs to listen to the current cards

    //handling the table's cards
    tableCards.contents.clear()
    for (card <- table.cards) {
      tableCards.contents += frontCard(card)
    }

    //setting up the center's elements
    center.contents.clear()
    center.validate()
    center.repaint()
    if (game.deck.cards.nonEmpty) deckButtons.contents += deckPic // weird, doesn't work
    turn.text = "It's " + this.game.currentPlayer.name + "'s turn."
    take.text = "Taking: "
    current.text = "Using: "
    center.contents += turn
    center.contents += valid
    center.contents += take
    center.contents += current
    center.contents += deckButtons
    center.contents += tableCards

    top.validate()
    top.repaint()

    //getting the GUI to listen to the table's cards                    //for some reason cards expand to the sides
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))
    //matching the table's cards with their swing components
    tablePanels.clear()
    tablePanels = tableCards.contents.indices.map( i => (game.table.cards(i), tableCards.contents(i)) ).toBuffer
  }

  def updateTake() = {
    this.take.text = "Taking: " + toTake
    center.contents(2) = this.take
    top.validate()
    top.repaint()
  }
  def updateCurrent() = {
    this.current.text = "Playing: " + numberString(this.game.currentPlayer.currentCard.number) + this.game.currentPlayer.currentCard.suit
    center.contents(3) = this.current
    top.validate()
    top.repaint()
  }
  def updateInvalid() = {
    this.valid.text = "Valid move? No, try again."
    toTake = ""
    center.contents(1) = this.valid
    top.validate()
    top.repaint()
  }
  def updateValid() = {
    this.valid.text = "Valid move? Yes."
    toTake = ""
    center.contents(1) = this.valid
    top.validate()
    top.repaint()
  }
  def updateNoCard() = {
    this.valid.text = "Valid move? Choose a card to play."
    toTake = ""
    center.contents(1) = this.valid
    top.validate()
    top.repaint()
  }


  var playersNro = "nothing"

  this.listenTo(playerButton, startButton, confirmButton, clearButton, endButton, saveButton, saveEndButton, playButton)

  this.reactions += {
    case clicked: ButtonClicked if (clicked.source == playerButton) => {
      if (sourceField.text.toIntOption.isDefined) {
        if ((2 until 13).contains(sourceField.text.toInt)) {
          playersNro = sourceField.text
          game.playTurn("end")                                    // to clear the players and the table if a new number is give
          game.playTurn("players " + playersNro)
          outPut.text = playersNro + " players."
        } else {
          outPut.text = "Pleas write a number between 2 and 12."
        }
      } else {
        outPut.text = "Pleas write a number between 2 and 12."
      }
    }
    case clicked: ButtonClicked if (clicked.source == startButton) => {
      game.playTurn("start")
      if (playersNro != "nothing") {
        setUp()
        window.contents = play
        println(tablePanels)
        println(tablePanels.map(_._1))
      } else {
        outPut.text = "Pleas give the number of players."
      }
    }
    case clicked: ButtonClicked if (clicked.source == confirmButton) => {
      if (toTake.nonEmpty) {
        game.playTurn("take" + toTake.dropRight(1))
        if (game.error) {
          updateInvalid()
        } else {
          updateValid()
        }
        println(toTake)
      } else {
        if (game.currentPlayer.handCards.contains(game.currentPlayer.currentCard)) {
          game.playTurn("place")
          updateValid()
        } else {
          updateNoCard()
        }
      }
      println(game.deck.cards)
      if (game.players.forall( _.handCards.isEmpty )) {
        game.lastPlayer.addAtTheEnd(game.table.cards.toVector)
        println("Total of bonuses: " + game.players.map( _.points ))
        game.pointCount()
        var pointString = ""
        for(player <- game.players) {
          pointString += player.name + ": " + player.points.toString + " points\n"
        }
        endOutPut.text = pointString + "\n\nThe winner is " + game.players.maxBy( _.points ).name
        this.window.contents = end
      } else {
        draw()
      }
      println(game.table.cards)
      println(game.players.map( _.pileCards ))
      println(game.players.map( _.handCards ))
    }
    case clicked: ButtonClicked if (clicked.source == clearButton) => {
      toTake = ""
      updateTake()
      println(toTake)
    }
    case MouseClicked(src, _, _, _, _) if (cardPanelPairs.exists(_._2 == src)) => {
      cardPanelPairs.find(_._2 == src) match {
        case Some(pair) => {
          game.playTurn("play " + pair._1.number.toString + pair._1.suit)
          updateCurrent()
        }
        case None => //nothing
      }
      println(game.currentPlayer.name)
      println(game.currentPlayer.currentCard)
    }
    case MouseClicked(src, _, _, _, _) if (tablePanels.exists(_._2 == src)) => {
      tablePanels.find(_._2 == src) match {
        case Some(pair) => {
          toTake += " " + pair._1.number.toString + pair._1.suit + ","
          updateTake()
        }
        case None => //nothing
      }
      println(tablePanels.map(_._1))
      println(toTake)
    }
  }


  val window = new MainFrame {

    title = "CASSINO"
    minimumSize = new Dimension(720, 800)
    preferredSize = new Dimension(720, 800)
    maximumSize = new Dimension(720, 800)

    contents = end

  }

  def top = window


  /*val allCards = for {
      i <- cardPanels.indices
      cardPanel <- cardPanels(i)
    } yield cardPanel
    allCards.foreach(n => this.listenTo(n.mouse.clicks))*/

  /*
          println("Total pile cards: " + game.players.map( _.pileCards.size).sum)
        for(n <- 1 until 14) {
          println("Total " + n.toString + ":" + game.players.map( _.pileCards.count(_.number == n) ).sum)
        }
        println("Total spades: " + game.players.map( _.pileCards.count(_.suit == "s") ).sum)
        println("Total clubs: " + game.players.map( _.pileCards.count(_.suit == "c") ).sum)
        println("Total hearts: " + game.players.map( _.pileCards.count(_.suit == "h") ).sum)
        println("Total diamonds: " + game.players.map( _.pileCards.count(_.suit == "d") ).sum)
   */

}
