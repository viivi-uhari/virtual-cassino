package os2.cassino.ui

import os2.cassino._

import java.awt.{Color, Font}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.mutable.Buffer
import scala.swing.event.{ButtonClicked, MouseClicked}
import scala.swing.{BoxPanel, Button, Component, Dimension, Graphics2D, GridPanel, Image, Label, MainFrame, Orientation, Panel, SimpleSwingApplication, Swing, TextArea, TextField}

object GUI extends SimpleSwingApplication {

  // some constants
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
  val closeButton = new Button("close")   //or hopefully play again??
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
    contents += closeButton
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
  var playersNro = ""
  var compNro = ""
  var toTakeBuffer = Buffer[Card]()

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
  def takeText(taken: String) = {
    val text = new Panel {
      minimumSize = new Dimension(200, 25)
      preferredSize = new Dimension(200, 25)
      maximumSize = new Dimension(200, 25)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString(taken, 0, 20)                           //card to take
      }
    }
    text
  }

  // updates the text that tells which card is about to be played
  def currentText(current: String): Panel = {
    val text = new Panel {
      minimumSize = new Dimension(200, 32)
      preferredSize = new Dimension(200, 32)
      maximumSize = new Dimension(200, 32)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString(current, 0, 20)                   //current card
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
  var compLabels = Buffer[Component]()
  var compL = Buffer[(Player, Component)]()
  var currentLabel = Buffer[(Player, Component)]()

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
    center.contents += takeText("Taking: ")
    center.contents += currentText("Playing: ")
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
    //playerPanels.clear()
    for (i <- cardPanels.indices) {
      playerPanels += ((game.players(i), cardPanels(i)))
    }
    for (i <- playerPanels.indices) {
      if (playerPanels(i)._1 == game.currentPlayer) {
        currentPanel = (playerPanels(i)._1.handCards, playerPanels(i)._2)
      }
    }
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)        //pairs of cards and their GUI components
    //only needs to listen to the current cards if it's an actual player
    if (!game.currentPlayer.isInstanceOf[Computer]) currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))

    //getting the GUI to listen to the table's cards
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))


    //finding all the computer opponents' labels
    val rightComputerLabels = right.contents.map(_.asInstanceOf[BoxPanel].contents(1))
    val leftComputerLabels = left.contents.map(_.asInstanceOf[BoxPanel].contents(1))
    for (i <- rightComputerLabels.indices) {
      compLabels = compLabels :+ rightComputerLabels(i)
      if (i < leftComputerLabels.size) compLabels = compLabels :+ leftComputerLabels(i)
    }
    compLabels = compLabels.drop(playersNro.toInt)
    //matching the computer opponents' with their labels
    for (i <- compLabels.indices) {
      compL += ((game.players(i + playersNro.toInt), compLabels(i)))
    }
    currentLabel = compL.filter( _._1 == game.currentPlayer )
    currentLabel.foreach( n => this.listenTo(n._2.mouse.clicks) )


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
    //only needs to listen to the current cards if it's an actual player
    if (!game.currentPlayer.isInstanceOf[Computer]) currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))

    //updating the current labels
    currentLabel = compL.filter( _._1 == game.currentPlayer )
    currentLabel.foreach( n => this.listenTo(n._2.mouse.clicks) )
    println(currentLabel.map( _._1.name ))

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
    center.contents(6) = takeText("Taking: ")
    center.contents(7) = currentText("Playing: ")
    center.contents(8) = tableCards
    toTakeBuffer.clear()

    top.validate()
    top.repaint()
  }


  // helper methods, updates the texts of the center of the game with the previous methods takeText, currentText and updateText
  def updateTake() = {
    val cardString = toTakeBuffer.map( game.cardToString(_) ).mkString(", ")
    center.contents(6) = takeText("Taking: " + cardString)
    top.validate()
    top.repaint()
  }
  def updateCurrent() = {
    val card = game.cardToString(this.game.currentPlayer.currentCard)
    center.contents(7) = currentText("Playing: " + card)
    top.validate()
    top.repaint()
  }
  def updateInvalid() = {
    toTakeBuffer.clear()
    center.contents(5) = updateText("Invalid move.")
    center.contents(6) = takeText("Taking: ")
    top.validate()
    top.repaint()
  }
  def updateNoCard() = {
    toTakeBuffer.clear()
    center.contents(5) = updateText("Choose a card to play.")
    top.validate()
    top.repaint()
  }
  def updateSave() = {
    val fileName = saveField.text
    if (fileName.isEmpty) {
      center.contents(5) = updateText("Enter a valid name for the file.")
    } else {
      game.save(fileName)
      if (game.giveResult == "Success") center.contents(5) = updateText("Game saved.") else center.contents(5) = updateText(game.giveResult)
    }
    top.validate()
    top.repaint()
  }
  def updateComp(took: Buffer[Card]) = {
    val card = game.cardToString(this.game.previousPlayer.currentCard)
    toTakeBuffer.clear()
    val cardString = took.map( game.cardToString(_) ).mkString(", ")
    center.contents(6) = takeText("Took: " + cardString)
    center.contents(7) = currentText("Played: " + card)
    top.validate()
    top.repaint()
  }

  // helper methods to update the end window
  def updateEndSave() = {
    val fileName = saveEndField.text
    if (fileName.nonEmpty) {
      game.save(fileName)
      if (game.giveResult == "Success") feedback.text = "Game saved." else feedback.text = game.giveResult
    } else {
      feedback.text = "\nPlease enter a valid name for the file."
    }
  }
  def endSetUp() = {
    game.end()
    var pointString = ""
    for(player <- game.players) {
      pointString += player.name + ": " + player.tellPoints.toString + " points\n"
    }
    var winnerString = ""
    if (game.winners.size == 1) winnerString = "\n\nThe winner is " else winnerString = "\n\nIt's a tie. The winners are "
    endOutPut.text = pointString + winnerString + game.winners.map( _.name ).mkString(", ")
  }

  // listening to all the buttons
  this.listenTo(playerButton, compButton, startButton, confirmButton, clearButton, endButton, saveButton, saveEndButton, closeButton, loadButton)

  // all of the reactions

  this.reactions += {
    // loading the game
    case clicked: ButtonClicked if (clicked.source == loadButton) => {
      if (loadSourceField.text.isEmpty) {
        outPut.text = "Please enter a valid name for the file."
      } else {
        game.load(loadSourceField.text)
        outPut.text = game.giveResult
        if (game.giveResult == "Success") {
          sourceField.editable = false
          sourceField2.editable = false
        }
      }
    }
    // giving a number for the players
    case clicked: ButtonClicked if (clicked.source == playerButton) => {
      if (sourceField.text.toIntOption.isDefined && (1 to 12).contains(sourceField.text.toInt)) {
        playersNro = sourceField.text
        game.clearAll()                                           // to clear the players and the table if a new number is give
        game.addPlayers(playersNro.toInt)
        outPut.text = playersNro + " players."
      } else {
        outPut.text = "Pleas give a number between 1 and 12."
      }
    }
    // giving a number for the computer opponents
    case clicked: ButtonClicked if (clicked.source == compButton) => {
      if (sourceField.text.toIntOption.isEmpty) {
        outPut.text = "Pleas give a number for the players first."
      } else if (sourceField2.text.toIntOption.isDefined && sourceField2.text.toIntOption != Some(1) && (0 to (12 - playersNro.toInt)).contains(sourceField2.text.toInt)
                 || sourceField2.text.toIntOption == Some(1) && (1 to (12 - playersNro.toInt)).contains(sourceField2.text.toInt)) {
        if (compNro.nonEmpty) game.clearComputers(compNro.toInt)  // to clear the computer opponents if a new number is give
        compNro = sourceField2.text
        //game.playTurn("computers " + compNro)
        game.addComputers(compNro.toInt)
        outPut.text = compNro + " computer opponents."
      } else if (sourceField2.text.toIntOption == Some(1)) {
        outPut.text = "Pleas give a number between 1 and " + (12 - playersNro.toInt).toString + "."
      } else {
        outPut.text = "Pleas give a number between 0 and " + (12 - playersNro.toInt).toString + "."
      }
      println(game.players.map( _.name ))
    }
    // starting the game
    case clicked: ButtonClicked if (clicked.source == startButton) => {
      if (game.players.size > 1 && playersNro.nonEmpty) {
        //game.playTurn("start")
        game.start()
        setUp()
        window.contents = play
      } else if (outPut.text == "Success, no errors") {
        setUp()
        window.contents = play
      } else {
        outPut.text = "Pleas give the number of players/computer opponents."
      }
    }
    // registering the click of the confirm button
    case clicked: ButtonClicked if (clicked.source == confirmButton) => {
      if (toTakeBuffer.nonEmpty) {                                                    // checking if the player doesn't want to take anything
        game.take(toTakeBuffer)
        if (game.giveError) updateInvalid() else draw()                               // error tells if the move is valid
      } else {
        if (game.currentPlayer.handCards.contains(game.currentPlayer.currentCard)) {  // checking that the current player has a card from its hand
          game.place()
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
      toTakeBuffer.clear()
      updateTake()
    }
    case clicked: ButtonClicked if (clicked.source == saveButton) => updateSave()        // saving the game
    case clicked: ButtonClicked if (clicked.source == saveEndButton) => updateEndSave()  // saving the game at the end
    // ending the game, closing the GUI
    case clicked: ButtonClicked if (clicked.source == closeButton || clicked.source == endButton) => this.window.closeOperation()
    // clicking on the player's cards
    case MouseClicked(src, _, _, _, _) if (cardPanelPairs.exists(_._2 == src)) => {
      cardPanelPairs.find(_._2 == src) match {
        case Some(pair) => {
          game.play(pair._1)
          updateCurrent()
        }
        case None => //nothing
      }
    }
    // clicking on the table's cards
    case MouseClicked(src, _, _, _, _) if (tablePanels.exists(_._2 == src)) => {
      tablePanels.find(_._2 == src) match {
        case Some(pair) => {
          toTakeBuffer += pair._1
          updateTake()
        }
        case None => //nothing
      }
    }
    // clicking on a computer opponent's Label
    case MouseClicked(src, _, _, _, _) if (currentLabel.exists(_._2 == src)) => {
      currentLabel.find(_._2 == src) match {
        case Some(pair) => {
          println(game.currentPlayer.name)
          println(game.currentPlayer.currentCard)
          val took = game.playComputer(pair._1.asInstanceOf[Computer])
          if (game.players.forall( _.handCards.isEmpty )) {                               // checking if it's the end of the game
            endSetUp()
            this.window.contents = end
          } else {
            draw()
            updateComp(took)
          }

          println(took)
          println(game.currentPlayer.name)
          println(game.currentPlayer.currentCard)
        }
        case None => //nothing
      }
      println(game.currentPlayer.name)
      println(game.table.cards)
      println(game.players.map( _.name))
      println(game.players.map( _.handCards))
      println(game.players.map( _.pileCards))
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
