package os2.cassino.ui

import os2.cassino._

import java.awt.{Color, Font}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.SwingConstants
import scala.collection.mutable.Buffer
import scala.swing.BorderPanel.Position
import scala.swing.FlowPanel.Alignment
import scala.swing.event.{ButtonClicked, MouseClicked}
import scala.swing.{BorderPanel, BoxPanel, Button, Component, Dialog, Dimension, Graphics2D, GridPanel, Image, Label, MainFrame, Orientation, Panel, SimpleSwingApplication, Swing, TextArea, TextField}

object GUI extends SimpleSwingApplication {

  //some constants
  val cardWidth = 54
  val cardHeight = 90
  val cardFont = new Font("TimesRoman", Font.PLAIN, 30)
  val nameFont = new Font("Calibri", Font.PLAIN, 13)
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


  // components for the start window
  val playerButton = new Button("Confirm")
  val compButton = new Button("Confirm")
  val startButton = new Button("start")
  val loadButton = new Button("load")
  val sourceField = new TextField
  val sourceField2 = new TextField
  val loadSourceField = new TextField
  val outPut = new TextArea("", 30, 100) {
    editable = false
  }

  // setting up the start window
  val start = new BoxPanel(Orientation.Vertical) {

    contents += new Label("Welcome to play Cassino!")
    contents += new BoxPanel(Orientation.Horizontal) {
      val playerLabel = new Label("How many players?")
      contents += playerLabel
      contents += Swing.HStrut(5)
      contents += sourceField
      contents += Swing.HStrut(5)
      contents += playerButton
    }
    contents += new BoxPanel(Orientation.Horizontal) {
      val compLabel = new Label("How many computer opponents?")
      contents += compLabel
      contents += Swing.HStrut(5)
      contents += sourceField2
      contents += Swing.HStrut(5)
      contents += compButton
    }
    contents += new BoxPanel(Orientation.Horizontal) {
      val loadLabel = new Label("From which file do you want to load a game?")
      contents += loadLabel
      contents += Swing.HStrut(5)
      contents += loadSourceField
      contents += Swing.HStrut(5)
      contents += loadButton
    }
    contents += startButton
    contents += outPut
  }

  // components for the end window
  val saveEndField = new TextField
  val saveEndButton = new Button("save")
  val playButton = new Button("Play again")
  val endOutPut = new TextArea("") {
    editable = false
  }
  val feedback = new TextArea("", 30, 10) {
    editable = false
  }

  // setting up the end window
  val end = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Game Ended") {
      horizontalTextPosition = scala.swing.Alignment.Right
    }
    contents += endOutPut
    contents += Swing.VStrut(10)
    contents += feedback
    contents += new Label("The file's name you want the game to be saved to:")
    contents += saveEndField
    contents += saveEndButton
    contents += playButton
  }

  //components for the play window
  //first the center components

  val confirmButton = new Button("Confirm")
  val clearButton = new Button("Clear")
  val endButton = new Button("End")
  val saveButton = new Button("Save")

  val saveField = new TextField {
    minimumSize = new Dimension(160, 20)
    preferredSize = new Dimension(160, 20)
    maximumSize = new Dimension(160, 20)
  }

  def emptyField = {     //to make the GUI look nicer
    new Panel {
      background = green
      minimumSize = new Dimension(200, 20)
      preferredSize = new Dimension(200, 20)
      maximumSize = new Dimension(200, 20)
    }
  }

  val save = new BoxPanel(Orientation.Horizontal) {
    background = green
    contents += saveButton
    contents += saveField
  }
  val confirm = new BoxPanel(Orientation.Horizontal) {
    background = green
    contents += confirmButton
    contents += emptyField
  }
  val clear = new BoxPanel(Orientation.Horizontal) {
    background = green
    contents += clearButton
    contents += emptyField
  }
  val endB = new BoxPanel(Orientation.Horizontal) {
    background = green
    contents += endButton
    contents += emptyField
  }
  val tableCards = new GridPanel(6, 4) {
    background = green
  }

  val center = new BoxPanel(Orientation.Vertical) {
    background = green
    contents += save
    contents += confirm
    contents += clear
    contents += endB
  }

  // Panel for the left cards (named wrong)
  val right = new GridPanel(6, 1) {
    background = green
  }
  // Panel for the right cards (named wrong)
  val left = new GridPanel(6, 1) {
    background = green
  }

  val play = new GridPanel(1, 3) {
    contents += right
    contents += center
    contents += left
  }

  // game elements
  val deck = new Deck
  val table = new OwnTable
  val game = new Game(Buffer[Player](), this.table, this.deck)
  var toTake = ""
  var playersNro = ""

  // methods that tell what is happening durig the game
  // who's turn it is
  def turnText(player: Player): Panel = {
    val text = new Panel {
      minimumSize = new Dimension(200, 25)
      preferredSize = new Dimension(200, 25)
      maximumSize = new Dimension(200, 25)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString("It's " + player.name + "'s turn", 0, 22)   //turn
      }
    }
    text
  }

  // updates the text that tells if the turn is valid or not, if the game has been saved, and other errors
  def updateText(updateText: String): Panel = {
    val text = new Panel {
      minimumSize = new Dimension(200, 25)
      preferredSize = new Dimension(200, 25)
      maximumSize = new Dimension(200, 25)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString(updateText, 0, 20)                 //update: valid, saved, no cards etc.
      }
    }
    text
  }

  // updates the text that tells which cards are about to be taken
  def takeText = {
    val text = new Panel {
      minimumSize = new Dimension(200, 25)
      preferredSize = new Dimension(200, 25)
      maximumSize = new Dimension(200, 25)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString("Taking: " + toTake, 0, 20)                           //card to take
      }
    }
    text
  }

  // updates the text that tells which card is about to be played
  def currentText(currentText: String): Panel = {
    val text = new Panel {
      minimumSize = new Dimension(200, 32)
      preferredSize = new Dimension(200, 32)
      maximumSize = new Dimension(200, 32)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString("Playing: " + currentText, 0, 20)                   //current card
      }
    }
    text
  }

  // constants for the card pics
  val x = 3
  val y = 5

  // method to convert the suit string to an image
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

  // a method to convert the number to a string on the cards
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

  // a method to display an empty slot for a card, the GUI looks nicer with this
  def empty = {
    val emptyCardPanel = new Panel {
      override def paintComponent(g: Graphics2D) = {
        g.setColor(green)
        g.fillRect(x, y, cardWidth, cardHeight)
      }
    }
    emptyCardPanel
  }

  //variables to map the cards etc. to the game's elements. These are updated during the setUp and draw methods.

  var playerPanels = Buffer[(Player, Buffer[Component])]()
  var tablePanels = Buffer[(Card, Component)]()
  var currentPanel: (Buffer[Card], Buffer[Component]) = (Buffer(Card(1, "s")), Buffer(frontCard(Card(1, "s"))))
  var previousPanel: (Buffer[Card], Buffer[Component]) = (Buffer(Card(1, "s")), Buffer(frontCard(Card(1, "s"))))
  var cardPanelPairs = currentPanel._1.zip(currentPanel._2)
  var cardPanelPairs2 = previousPanel._1.zip(previousPanel._2)

  //at the start to set up
  def setUp() = {

    //handling the players
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

    //handling the table's cards
    //tablePanels.clear()
    var n = 0
    while(n < game.table.cards.size) {
        val cardComponent = frontCard(game.table.cards(n))
        tableCards.contents += cardComponent
        tablePanels += ((game.table.cards(n), cardComponent))     //matching the table's cards with their swing components
        n += 1
    }
    while(n < 24) {                                               //max 24 cards to the table
      tableCards.contents += empty
      n += 1
    }

    //setting up the center
    center.contents += turnText(this.game.currentPlayer)
    center.contents += updateText("")
    center.contents += takeText
    center.contents += currentText("")
    center.contents += tableCards


    //finding all the card panels
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
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)        //pairs of cards and their GUI components
    currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))  //only needs to listen to the current cards

    //getting the GUI to listen to the table's cards
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))

  }


  // to update while playing

  def draw() = {

    // handling the players' cards
    for (i <- playerPanels.indices) {
      if (game.players(i) == game.currentPlayer) {
        for (n <- game.players(i).handCards.indices) {
          playerPanels(i)._2(n) = frontCard(game.players(i).handCards(n))
        }
      } else if (game.players(i) == game.previousPlayer) {       // only need to flip the previous player's cards
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

    //updating the current panels
    for (i <- playerPanels.indices) {
      if (playerPanels(i)._1 == game.currentPlayer) {
        currentPanel = (playerPanels(i)._1.handCards, playerPanels(i)._2)
      }
    }
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)        //getting only the relevant pairs, filters away the "empty" cards
    currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))  //only needs to listen to the current/relevant cards

    //handling the table's cards
    tableCards.contents.clear()
    tablePanels.clear()
    var n = 0
    while(n < game.table.cards.size) {
        val cardComponent = frontCard(game.table.cards(n))
        tableCards.contents += cardComponent
        tablePanels += ((game.table.cards(n), cardComponent))   //matching the table's cards with their swing components
        n += 1
    }
    while(n < 24) {                                             //max 24 cards to the table
      tableCards.contents += empty
      n += 1
    }

    //getting the GUI to listen to the table's cards
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))

    //setting up the center's elements
    center.contents(4) = turnText(this.game.currentPlayer)
    center.contents(5) = updateText("Valid move.")
    center.contents(6) = takeText
    center.contents(7) = currentText("")
    center.contents(8) = tableCards

    top.validate()
    top.repaint()
  }


  // helper methods, updates the texts of the center of the game with the previous methods takeText, currentText and updateText
  def updateTake() = {
    center.contents(6) = takeText
    top.validate()
    top.repaint()
  }
  def updateCurrent() = {
    val card = numberString(this.game.currentPlayer.currentCard.number) + this.game.currentPlayer.currentCard.suit
    center.contents(7) = currentText(card)
    top.validate()
    top.repaint()
  }
  def updateInvalid() = {
    toTake = ""
    center.contents(5) = updateText("Invalid move.")
    top.validate()
    top.repaint()
  }
  def updateNoCard() = {
    toTake = ""
    center.contents(5) = updateText("Choose a card to play.")
    top.validate()
    top.repaint()
  }
  def updateSave() = {
    val fileName = saveField.text
    if (fileName.isEmpty) {
      center.contents(5) = updateText("Please enter a valid name for the file.")
    } else {
      game.save(fileName)
      if (game.result.takeWhile( _ != ',' ) == "Success") center.contents(5) = updateText("Game saved.") else center.contents(5) = updateText(game.result)
    }
    top.validate()
    top.repaint()
  }

  // helper methods to update the end window
  def updateEndSave() = {
    val fileName = saveEndField.text
    if (fileName.nonEmpty) {
      game.save(fileName)
      if (game.result.takeWhile( _ != ',' ) == "Success") feedback.text = "Game saved." else feedback.text = game.result
    } else {
      feedback.text = "\nPlease enter a valid name for the file."
    }
  }
  def endSetUp() = {
    game.lastPlayer.addAtTheEnd(game.table.cards.toVector)
    game.pointCount()
    var pointString = ""
    for(player <- game.players) {
      pointString += player.name + ": " + player.points.toString + " points\n"
    }
    endOutPut.text = pointString + "\n\nThe winner is " + game.players.maxBy( _.points ).name
  }

  // listening to all the buttons
  this.listenTo(playerButton, startButton, confirmButton, clearButton, endButton, saveButton, saveEndButton, playButton, loadButton)

  // all of the reactions

  this.reactions += {
    // loading the game
    case clicked: ButtonClicked if (clicked.source == loadButton) => {
      if (loadSourceField.text.isEmpty) {
        outPut.text = "Please enter a valid name for the file."
      } else {
        game.load(loadSourceField.text)
        outPut.text = game.result
        if (game.result.takeWhile( _ != ',' ) == "Success") {
          sourceField.editable = false
          sourceField2.editable = false
        }
      }
    }
    // giving a number for the players
    case clicked: ButtonClicked if (clicked.source == playerButton) => {
      if (sourceField.text.toIntOption.isDefined && (2 until 13).contains(sourceField.text.toInt)) {
        playersNro = sourceField.text
        game.playTurn("end")                                       // to clear the players and the table if a new number is give
        game.playTurn("players " + playersNro)
        outPut.text = playersNro + " players."
      } else {
        outPut.text = "Pleas write a number between 2 and 12."
      }
    }
    // starting the game
    case clicked: ButtonClicked if (clicked.source == startButton) => {
      if (game.players.nonEmpty && playersNro.nonEmpty) {
        game.playTurn("start")
        setUp()
        window.contents = play
      } else if (outPut.text == "Success, no errors") {
        setUp()
        window.contents = play
      } else {
        outPut.text = "Pleas give the number of players."
      }
    }
    // registering the click of the confirm button
    case clicked: ButtonClicked if (clicked.source == confirmButton) => {
      if (toTake.nonEmpty) {                                                          // checking if the player doesn't want to take anything
        game.playTurn("take" + toTake.dropRight(1))
        if (game.error) updateInvalid() else draw()                                   // error tells if the move is valid
      } else {
        if (game.currentPlayer.handCards.contains(game.currentPlayer.currentCard)) {  // checking that the current player has a card from its hand
          game.playTurn("place")
          draw()
        } else {
          updateNoCard()
        }
      }
      if (game.players.forall( _.handCards.isEmpty )) {                               // checking if it's the end of the game
        endSetUp()
        this.window.contents = end
      }
    }
    // clearing the cards to take
    case clicked: ButtonClicked if (clicked.source == clearButton) => {
      toTake = ""
      updateTake()
    }
    case clicked: ButtonClicked if (clicked.source == saveButton) => updateSave()        // saving the game
    case clicked: ButtonClicked if (clicked.source == saveEndButton) => updateEndSave()  // saving the game at the end
    // ending the game, not working
    case clicked: ButtonClicked if (clicked.source == playButton || clicked.source == endButton) => {
      game.playTurn("end")
      this.window.contents = start
    }
    // clicking on the player's cards
    case MouseClicked(src, _, _, _, _) if (cardPanelPairs.exists(_._2 == src)) => {
      cardPanelPairs.find(_._2 == src) match {
        case Some(pair) => {
          game.playTurn("play " + pair._1.number.toString + pair._1.suit)
          updateCurrent()
        }
        case None => //nothing
      }
    }
    // clicking on the table's cards
    case MouseClicked(src, _, _, _, _) if (tablePanels.exists(_._2 == src)) => {
      tablePanels.find(_._2 == src) match {
        case Some(pair) => {
          toTake += " " + pair._1.number.toString + pair._1.suit + ","
          updateTake()
        }
        case None => //nothing
      }
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
