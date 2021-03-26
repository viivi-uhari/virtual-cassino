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
  val outPut = new TextArea("filler.", 30, 100)
  outPut.editable = false

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


  val tableCards = new GridPanel(6, 4) {
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
  val empty = new Panel {
    background = green
  }

  val buttons = new GridPanel(4, 1) {
    background = green
    contents += new Button("Confirm")
    contents += new Button("Clear")
    contents += new Button("End")
    contents += new Button("Save")
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


  var playersNro = "nothing"

  val deck = new Deck
  val table = new OwnTable
  val game = new Game(Buffer[Player](), this.table, this.deck)

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
  var cardPanelPairs = currentPanel._1.zip(currentPanel._2)


  def setUp() = {

    for (card <- table.cards) {
      tableCards.contents += frontCard(card)
      tablePanels += ((card, frontCard(card))) //matching the table's cards with their swing components
    }
    if (game.deck.cards.nonEmpty) deckButtons.contents += deckPic else deckButtons.contents += empty
    val turn = new Label("It's " + this.game.currentPlayer.name + "'s turn.") {
      minimumSize = new Dimension(240, 40)
      preferredSize = new Dimension(240, 40)
      maximumSize = new Dimension(240, 40)
    }
    center.contents += turn
    center.contents += deckButtons
    center.contents += tableCards

    for (i <- game.players.indices) {
      val fourCards = new GridPanel(1, 4) {
        background = green
      }
      //if (game.players(i) == game.currentPlayer) {
      for (card <- game.players(i).handCards) {
        fourCards.contents += this.frontCard(card)
      }
      //} else {
      // for (card <- game.players(i).handCards) {
      // fourCards.contents += this.backCard
      // }
      //}
      val name = new Label(game.players(i).name)
      val box = new BoxPanel(Orientation.Vertical)
      box.background = green
      box.contents += fourCards
      box.contents += name
      if (i % 2 == 0) right.contents += box else left.contents += box
    }

    //getting the GUI to listen to the players' cards
    val rightCardsPanels = right.contents.map(_.asInstanceOf[BoxPanel].contents.head.asInstanceOf[GridPanel].contents)
    val leftCardsPanels = left.contents.map(_.asInstanceOf[BoxPanel].contents.head.asInstanceOf[GridPanel].contents)

    var cardPanels = IndexedSeq[Buffer[Component]]()
    for (i <- rightCardsPanels.indices) {
      cardPanels = cardPanels :+ rightCardsPanels(i)
      if (i < leftCardsPanels.size) cardPanels = cardPanels :+ leftCardsPanels(i)
    }
    val allCards = for {
      i <- cardPanels.indices
      cardPanel <- cardPanels(i)
    } yield cardPanel
    allCards.foreach(n => this.listenTo(n.mouse.clicks))

    //matching the currentPlayer's cards with their swing components
    for (i <- cardPanels.indices) {
      playerPanels += ((game.players(i), cardPanels(i)))
    }
    for (i <- playerPanels.indices) {
      if (playerPanels(i)._1 == game.currentPlayer) {
        currentPanel = (playerPanels(i)._1.handCards, playerPanels(i)._2)
      }
    }
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)

    //getting the GUI to listen to the table's cards
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))

  }


  this.listenTo(playerButton, startButton)

  this.reactions += {
    case click: ButtonClicked if (click.source == playerButton) => {
      if (sourceField.text.toIntOption.isDefined) playersNro = sourceField.text //else error
      game.playTurn("players " + playersNro)
    }
    case click: ButtonClicked if (click.source == startButton) => {
      game.playTurn("start")
      outPut.text = game.players.map(_.handCards).mkString("; ")
      setUp()
      window.contents = play
      //println(playerPanels.map( _._1.name ))
      //println(playerPanels.map( _._1.handCards ))
      println(tablePanels)
      println(tablePanels.map(_._1))
    }
    case MouseClicked(src, _, _, _, _) if (cardPanelPairs.exists(_._2 == src)) => {
      cardPanelPairs.find(_._2 == src) match {
        case Some(pair) => game.playTurn("play " + pair._1.number.toString + pair._1.suit)
        case None => //nothing
      }
      println(game.currentPlayer.name)
      println(game.currentPlayer.currentCard)
    }
    case MouseClicked(src, _, _, _, _) if (tablePanels.exists(_._2 == src)) => {
      var toTake = ""
      tablePanels.find(_._2 == src) match {
        case Some(pair) => toTake += pair._1.number.toString + pair._1.suit
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

    contents = start

  }

  def top = window


}
