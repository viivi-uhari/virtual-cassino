package cassino.ui

import cassino._

import java.awt.{Color, Font}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.mutable.Buffer
import scala.swing.event.{ButtonClicked, MouseClicked}
import scala.swing.{BoxPanel, Button, Component, Dimension, Graphics2D, GridPanel, Image, Label, MainFrame, Orientation, Panel, SimpleSwingApplication, Swing, TextArea, TextField}

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


  //components for the start window
  val playerButton = new Button("Confirm")
  val compButton = new Button("Confirm")
  val startButton = new Button("Start")
  val loadButton = new Button("Load")
  val sourceField = new TextField
  val sourceField2 = new TextField
  val loadSourceField = new TextField
  val outPut = new TextArea("", 30, 100) {
    editable = false
  }

  //setting up the start window
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

  //components for the end window
  val saveEndField = new TextField
  val saveEndButton = new Button("Save")
  val closeButton = new Button("Quit")
  val endOutPut = new TextArea("") {
    editable = false
  }
  val feedback = new TextArea("", 30, 10) {
    editable = false
  }

  //setting up the end window
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
  val endButton = new Button("Quit")
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

  //extensions of the play window's buttons to make the GUI look nicer
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

  //Panel for the left cards (named wrong, actually the left one)
  val right = new GridPanel(6, 1) {
    background = green
  }
  //Panel for the right cards (named wrong, actually the right one)
  val left = new GridPanel(6, 1) {
    background = green
  }

  val play = new GridPanel(1, 3) {
    contents += right
    contents += center
    contents += left
  }

  //game elements
  val deck = new Deck
  val table = new OwnTable
  val game = new Game(Buffer[Player](), this.table, this.deck)
  var playersNro = 0
  var compNro = 0
  var toTakeBuffer = Buffer[Card]()

  //methods that tell what is happening durig the game,
  //the methods are separated because the panels are a bit different sizes, have different texts and make the code a bit clearer

  //who's turn it is
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

  //updates the text that tells if the turn is valid or not, if the game has been saved, and other errors
  def updateText(updateText: String): Panel = {
    val text = new Panel {
      minimumSize = new Dimension(200, 25)
      preferredSize = new Dimension(200, 25)
      maximumSize = new Dimension(200, 25)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString(updateText, 0, 20)  //update: valid, saved, no cards etc.
      }
    }
    text
  }

  //updates the text that tells which cards are about to be taken
  def takeText(taken: String) = {
    val text = new Panel {
      minimumSize = new Dimension(200, 25)
      preferredSize = new Dimension(200, 25)
      maximumSize = new Dimension(200, 25)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString(taken, 0, 20)  //card to take
      }
    }
    text
  }

  //updates the text that tells which card is about to be played
  def currentText(current: String): Panel = {
    val text = new Panel {
      minimumSize = new Dimension(200, 32)
      preferredSize = new Dimension(200, 32)
      maximumSize = new Dimension(200, 32)
      override def paintComponent(g: Graphics2D) = {
        g.setFont(nameFont)
        g.setColor(black)
        g.drawString(current, 0, 20)  //current card
      }
    }
    text
  }

  //constants for the card pics
  val x = 3
  val y = 5

  //method to convert the suit string to an image
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

  //a method to convert the number to a string on the cards
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

  //a pic of a card face up
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

  //a pic of a card face down
  def backCard = {
    val backCardPanel = new Panel {
      override def paintComponent(g: Graphics2D) = {
        g.setColor(red)
        g.fillRect(x, y, cardWidth, cardHeight)
      }
    }
    backCardPanel
  }

  //a method to display an empty slot for a card, the GUI looks nicer and doesn't register unwanted clicks with this
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
  var playerPanels = Buffer[(Player, Buffer[Component])]() //all players matched to the panels representing their cards
  var tablePanels = Buffer[(Card, Component)]()            //table cards matched to their panels
  var currentPanel: (Buffer[Card], Buffer[Component]) = (Buffer(Card(1, "s")), Buffer(frontCard(Card(1, "s"))))   //the current player's cards and their panels
  var previousPanel: (Buffer[Card], Buffer[Component]) = (Buffer(Card(1, "s")), Buffer(frontCard(Card(1, "s"))))  //the previous player's cards and their panels
  var cardPanelPairs = currentPanel._1.zip(currentPanel._2)       //the current player's cards and their panels in pairs
  var cardPanelPairs2 = previousPanel._1.zip(previousPanel._2)    //the previous player's cards and their panels in pairs
  var compLabels = Buffer[Component]()                     //all the computer opponents' labels
  var compL = Buffer[(Player, Component)]()                //all the computer opponents matched to their labels
  var currentLabel = Buffer[(Player, Component)]()         //the current computer opponent matched to its label, empty or contains only one (the current player)

  //at the start to set up
  def setUp() = {

    //handling the players, adding card panels and name labels
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


    //finding all the name labels
    val rightComputerLabels = right.contents.map(_.asInstanceOf[BoxPanel].contents(1))
    val leftComputerLabels = left.contents.map(_.asInstanceOf[BoxPanel].contents(1))
    for (i <- rightComputerLabels.indices) {
      compLabels = compLabels :+ rightComputerLabels(i)
      if (i < leftComputerLabels.size) compLabels = compLabels :+ leftComputerLabels(i)
    }
    //getting only the computer opponents' labels
    compLabels = compLabels.drop(game.playerNro)
    //matching the computer opponents' with their labels
    for (i <- compLabels.indices) {
      compL += ((game.players(i + game.playerNro), compLabels(i)))
    }
    currentLabel = compL.filter( _._1 == game.currentPlayer )
    //listening to the computer opponent's label only if it's the current player
    currentLabel.foreach( n => this.listenTo(n._2.mouse.clicks) )


  }


  //to update while playing

  def draw() = {

    //handling the players' cards
    for (i <- playerPanels.indices) {
      if (game.players(i) == game.currentPlayer) {
        for (n <- game.players(i).handCards.indices) {
          playerPanels(i)._2(n) = frontCard(game.players(i).handCards(n))
        }
      } else if (game.players(i) == game.previousPlayer) {       //only needs to flip the previous player's cards
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
    cardPanelPairs = currentPanel._1.zip(currentPanel._2)        //getting only the relevant pairs, filters away the "empty" cards/panels
    //only needs to listen to the current cards if it's an actual player
    if (!game.currentPlayer.isInstanceOf[Computer]) currentPanel._2.foreach(n => this.listenTo(n.mouse.clicks))

    //updating the current labels
    currentLabel = compL.filter( _._1 == game.currentPlayer )
    //listening to the computer opponent's label only if it's the current player
    currentLabel.foreach( n => this.listenTo(n._2.mouse.clicks) )

    //handling the table's cards' panels, clearing them and adding new cards to them
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

    //getting the GUI to listen to the table's current cards
    tableCards.contents.foreach(n => this.listenTo(n.mouse.clicks))

    //modifying the center's elements
    center.contents(4) = turnText(this.game.currentPlayer)
    center.contents(5) = updateText("Valid move.")
    center.contents(6) = takeText("Taking: ")
    center.contents(7) = currentText("Playing: ")
    center.contents(8) = tableCards
    toTakeBuffer.clear()

    top.validate()
    top.repaint()
  }


  //smaller draw methods, updates the texts of the center of the game with the previous methods takeText, currentText and updateText
  //after selecting cards from the table
  def updateTake() = {
    val cardString = toTakeBuffer.map( game.cardToString(_) ).mkString(", ")
    center.contents(6) = takeText("Taking: " + cardString)
    top.validate()
    top.repaint()
  }
  //after selecting a card from hand
  def updateCurrent() = {
    val card = game.cardToString(this.game.currentPlayer.currentCard)
    center.contents(7) = currentText("Playing: " + card)
    top.validate()
    top.repaint()
  }
  //if can't take the selected cards
  def updateInvalid() = {
    toTakeBuffer.clear()
    center.contents(5) = updateText("Invalid move.")
    center.contents(6) = takeText("Taking: ")
    top.validate()
    top.repaint()
  }
  //if a player trys to play without choosing a card
  def updateNoCard() = {
    toTakeBuffer.clear()
    center.contents(5) = updateText("Choose a card to play.")
    top.validate()
    top.repaint()
  }
  //after clicking the save button on the play window
  def updateSave() = {
    val fileName = saveField.text
    if (fileName.isEmpty) {
      center.contents(5) = updateText("Enter a valid name for the file.")
    } else {
      game.save(fileName)
      if (game.giveResult == "Success") center.contents(5) = updateText("Game saved.") else center.contents(5) = updateText(game.giveResult)
    }
    saveField.text = ""
    top.validate()
    top.repaint()
  }
  //after the computer has played
  def updateComp(took: Buffer[Card]) = {
    val card = game.cardToString(this.game.previousPlayer.currentCard)
    toTakeBuffer.clear()
    val cardString = took.map( game.cardToString(_) ).mkString(", ")
    center.contents(6) = takeText("Took: " + cardString)
    center.contents(7) = currentText("Played: " + card)
    top.validate()
    top.repaint()
  }

  //methods to update the end window
  //immediately after the end of the game
  def endSetUp() = {
    var pointString = game.end()
    var winnerString = ""
    if (game.winners.size == 1) winnerString = "\nThe winner is " else winnerString = "\nIt's a tie. The winners are "
    endOutPut.text = pointString + winnerString + game.winners.map( _.name ).mkString(", ")
  }
  //after clicking on the save button at the end
  def updateEndSave() = {
    val fileName = saveEndField.text
    if (fileName.nonEmpty) {
      game.save(fileName)
      if (game.giveResult == "Success") feedback.text = "Game saved." else feedback.text = game.giveResult
    } else {
      feedback.text = "\nPlease enter a valid name for the file."
    }
    saveEndField.text = ""
  }


  //listening to all the buttons
  this.listenTo(playerButton, compButton, startButton, confirmButton, clearButton, endButton, saveButton, saveEndButton, closeButton, loadButton)

  //all of the reactions
  this.reactions += {
    //loading the game
    case clicked: ButtonClicked if (clicked.source == loadButton) => {
      if (loadSourceField.text.isEmpty) {
        outPut.text = "Please enter a valid name for the file."
      } else {
        game.clearAll()
        game.load(loadSourceField.text)
        if (game.giveResult == "Success") {
          outPut.text = "Success"
          sourceField.editable = false
          sourceField2.editable = false
        } else {
          outPut.text = game.giveResult + ". Player information cleared."
        }
        loadSourceField.text = ""
      }
    }
    //giving a number for the players, after giving a new number for the players the computer opponents are also cleared
    case clicked: ButtonClicked if (clicked.source == playerButton) => {
      val text = sourceField.text.trim
      if (text.toIntOption.isDefined && (1 to 12).contains(text.toInt)) {
        playersNro = text.toInt
        game.clearAll()  //to clear the players and the table if a new number is give
        game.addPlayers(playersNro)
        if (playersNro != 12) {
          outPut.text = playersNro + " players. 0 computer opponents.\nGive a number for the computer opponents if wanted."
        } else {
          outPut.text = "12 players. 0 computer opponents.\nOpponents wont fit in the game."
        }
        compNro = 0
      } else if (text.toIntOption.isDefined && text.toInt == 0 || playersNro == 0) {
        playersNro = 0
        game.clearAll()
        outPut.text = "0 players. Pleas give a number between 1 and 12."
        compNro = 0
      } else {  //in case of text and numbers not in the range of 0-12, doesn't need to register changes
        outPut.text = playersNro + " players. " + compNro + " computer opponents."
        outPut.text += "\nPleas give a number between 1 and 12 if you want to change the number of players."
      }
      sourceField.text = ""
    }
    //giving a number for the computer opponents, the number for the players has to be given first
    case clicked: ButtonClicked if (clicked.source == compButton) => {
      val text = sourceField2.text.trim
      outPut.text = playersNro + " players. " + compNro + " computer opponents."
      if (game.playerNro == 0) {
        outPut.text = "Pleas give a number for the players first."
      } else if (text.toIntOption.isDefined && game.playerNro > 1 && (0 to (12 - game.playerNro)).contains(text.toInt) ||   //correct inputs
                 text.toIntOption.isDefined && game.playerNro == 1 && (1 to (12 - game.playerNro)).contains(text.toInt)) {  //correct inputs
        if (compNro != 0) game.clearComputers(compNro)  //to clear the computer opponents if a new number is give
        compNro = text.toInt
        game.addComputers(compNro)
        outPut.text = playersNro + " players. " + compNro + " computer opponents."
      } else if (game.playerNro == 1) {
        outPut.text += "\nPleas give a number between 1 and " + (12 - game.playerNro).toString + " if you want to change the number of computer opponents."
      } else if (game.playerNro == 12) {
        outPut.text = playersNro + " players. " + "Opponents wont fit in the game."
      } else {
        outPut.text += "\nPleas give a number between 0 and " + (12 - game.playerNro).toString + " if you want to change the number of computer opponents."
      }
      sourceField2.text = ""
    }
    //starting the game
    case clicked: ButtonClicked if (clicked.source == startButton) => {
      if (game.players.size > 1 && game.players.exists( !_.isInstanceOf[Computer] ) && outPut.text != "Success") { //"Success" checks if the game has been loaded
        game.start()
        setUp()
        window.contents = play
      } else if (outPut.text == "Success") {
        setUp()
        window.contents = play
      } else if (game.players.forall( _.isInstanceOf[Computer] )) {
        outPut.text = "Pleas give a number for the players."
      } else {
        outPut.text = "Pleas give a number for the computer opponents."
      }
    }
    //registering the click of the confirm button
    case clicked: ButtonClicked if (clicked.source == confirmButton) => {
      if (toTakeBuffer.nonEmpty) {                                                    //checking if the player doesn't want to take anything
        game.take(toTakeBuffer)
        if (game.giveError) updateInvalid() else draw()                               //error tells if the move is valid
      } else {
        if (game.currentPlayer.handCards.contains(game.currentPlayer.currentCard)) {  //checking that the current player has a card from its hand
          game.place()
          draw()
        } else {
          if (!game.currentPlayer.isInstanceOf[Computer]) updateNoCard()
        }
      }
      if (game.players.forall( _.handCards.isEmpty )) {                               //checking if it's the end of the game
        endSetUp()
        this.window.contents = end
      }
    }
    //clearing the cards to take
    case clicked: ButtonClicked if (clicked.source == clearButton) => {
      toTakeBuffer.clear()
      updateTake()
    }
    case clicked: ButtonClicked if (clicked.source == saveButton) => updateSave()        //saving the game
    case clicked: ButtonClicked if (clicked.source == saveEndButton) => updateEndSave()  //saving the game at the end
    //ending the game, closing the GUI
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
    //clicking on the table's cards
    case MouseClicked(src, _, _, _, _) if (tablePanels.exists(_._2 == src)) => {
      tablePanels.find(_._2 == src) match {
        case Some(pair) => {
          if (!toTakeBuffer.contains(pair._1)) {
            toTakeBuffer += pair._1
            updateTake()
          }
        }
        case None => //nothing
      }
    }
    //clicking on a computer opponent's Label
    case MouseClicked(src, _, _, _, _) if (currentLabel.exists(_._2 == src)) => {
      currentLabel.find(_._2 == src) match {
        case Some(pair) => {
          val took = game.playComputer(pair._1.asInstanceOf[Computer])
          if (game.players.forall( _.handCards.isEmpty )) {                               //checking if it's the end of the game
            endSetUp()
            this.window.contents = end
          } else {
            draw()
            updateComp(took)
          }
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
