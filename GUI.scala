import java.awt.Color
import scala.Console.BLACK
import scala.swing._
import java.awt.{BasicStroke, Font}
import java.io.File
import javax.imageio.ImageIO
import scala.collection.mutable
import scala.swing.event.ButtonClicked
import scala.collection.mutable.Buffer

object GUI extends SimpleSwingApplication {

  val cardWidth = 54
  val cardHeight = 90
  val cardFont = new Font("TimesRoman", Font.PLAIN, 30)
  val green = new Color(129, 193, 133)
  val red = new Color(243, 64, 64)

  val hearts = ImageIO.read(new File("heart.png"))
  val diamonds = ImageIO.read(new File("diamond.png"))
  val clubs = ImageIO.read(new File("clubs.png"))
  val spades = ImageIO.read(new File("spades.png"))
  val suitWidth = 30
  val suitHeight = 30



  var playersNro = "nothing"

  val playerButton = new Button("Confirm")
  val compButton = new Button("Confirm")
  val sourceField = new TextField
  val sourceField2 = new TextField
  val outPut = new TextArea("filler.", 30, 100)
  outPut.editable = false
  outPut.text = playersNro

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
    contents += outPut

  }

  val frontCard = new Panel {

    val x = 3
    val y = 5

    override def paintComponent(g: Graphics2D) = {

      g.setColor(new Color(252, 250, 250))
      g.fillRect(x, y, cardWidth, cardHeight)

      g.setFont(cardFont)
      g.setColor(new Color(0, 0, 0))
      g.drawString("2", x + 18, y + 37)
      g.drawImage(diamonds, x + 12, y + 50, suitWidth, suitHeight, null)

    }
  }

  val frontCard2 = new Panel {
    val x = 3
    val y = 5

    override def paintComponent(g: Graphics2D) = {

      g.setColor(new Color(252, 250, 250))
      g.fillRect(x, y, cardWidth, cardHeight)

      g.setFont(cardFont)
      g.setColor(new Color(0, 0, 0))
      g.drawString("4", x + 18, y + 37)
      g.drawImage(clubs, x + 12, y + 50, suitWidth, suitHeight, null)

    }
  }
  val frontCard3 = new Panel {

    val x = 3
    val y = 5

    override def paintComponent(g: Graphics2D) = {

      g.setColor(new Color(252, 250, 250))
      g.fillRect(x, y, cardWidth, cardHeight)

      g.setFont(cardFont)
      g.setColor(new Color(0, 0, 0))
      g.drawString("Q", x + 18, y + 37)
      g.drawImage(hearts, x + 12, y + 50, suitWidth, suitHeight, null)

    }
  }
  val frontCard4 = new Panel {

    val x = 3
    val y = 5

    override def paintComponent(g: Graphics2D) = {

      g.setColor(new Color(252, 250, 250))
      g.fillRect(x, y, cardWidth, cardHeight)

      g.setFont(cardFont)
      g.setColor(new Color(0, 0, 0))
      g.drawString("J", x + 18, y + 37)
      g.drawImage(spades, x + 12, y + 50, suitWidth, suitHeight, null)

    }
  }

  val fourCards = new GridPanel(1, 4) {

    background = green
    //hGap = 6
    contents += frontCard
    contents += frontCard2
    contents += frontCard3
    contents += frontCard4
  }

  val tableCards = new GridPanel(1, 4) {
    background = green
  }
  val tableCards2 = new GridPanel(1, 4) {
    background = green
  }
  val tableCards3 = new GridPanel(1, 4) {
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

  val buttons = new GridPanel(3, 1) {
    background = green
    contents += new Button("Confirm")
    contents += new Button("End")
    contents += new Button("Save")
  }

  val deckButtons = new GridPanel(1, 2) {
    background = green
    contents += deckPic
    contents += buttons
  }

  val right = new GridPanel(8, 1) {
    background = green
    contents += fourCards
  }
  val center = new GridPanel(8, 1) {
    background = green
    contents += tableCards
    contents += tableCards2
    contents += tableCards3
    contents += deckButtons
  }
  val left = new GridPanel(8, 1) {
    background = green
  }

  val play = new GridPanel(1, 3) {
    contents += right
    contents += center
    contents += left
  }


  val deck = new Deck
  val table = new Table
  val game = new Game



  this.listenTo(playerButton)

  this.reactions += {
    case click: ButtonClicked if (click.source == playerButton) => {
      if (sourceField.text.toIntOption.isDefined) playersNro = sourceField.text
      game.playTurn("players " + playersNro)
      outPut.text = game.players.map( _.name ).mkString("; ")
      // window.contents = play
    }
  }

  val window = new MainFrame {

    title = "CASSINO"
    minimumSize   = new Dimension(720, 800)
    preferredSize = new Dimension(720, 800)
    maximumSize   = new Dimension(720, 800)

    contents = start

  }

  def top = window






  //def paintCard(suit: String, number: String, x: Int, y: Int)

}
