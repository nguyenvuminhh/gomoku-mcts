import scalafx.application.Platform
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, StackPane, VBox}
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import Constants.{SIZE, printCmd, setDifficulties}
import javafx.application.Application.launch
import scalafx.scene.control.Alert.AlertType

object MainGUI extends JFXApp3:
    override def start(): Unit =
        try
            printCmd("Starting MainGUI initialization...")
            val ButtonType1 = new ButtonType("Level 1")
            val ButtonType2 = new ButtonType("Level 2")
            val ButtonType3 = new ButtonType("Level 3")
            val ButtonType4 = new ButtonType("Level 4")
            val ButtonType5 = new ButtonType("Level 5")

            val alert = new Alert(AlertType.Confirmation) {
                initOwner(stage)
                title = "Select Difficulties"
                headerText = "NOTE: The more difficult, the more time the computer takes in each move."
                contentText = "Choose your option. Level 2-3 should be OK"
                buttonTypes = Seq(
                    ButtonType1, ButtonType2, ButtonType3, ButtonType4, ButtonType5)
            }

            val result = alert.showAndWait()

            result match {
                case Some(ButtonType1) => setDifficulties(1)
                case Some(ButtonType2) => setDifficulties(2)
                case Some(ButtonType3) => setDifficulties(3)
                case Some(ButtonType4) => setDifficulties(4)
                case Some(ButtonType5) => setDifficulties(5)
            }

            val game = new Game(aiStarts = true)
            printCmd("Game instance created")

            game.start()
            printCmd("Game started")

            stage = new JFXApp3.PrimaryStage {
                title = "Gomoku Game"
                width = 700
                height = 750

                printCmd("Creating scene...")
                scene = new Scene {
                    // GAME BOARD
                    val gameBoard = new GridPane()
                    gameBoard.setGridLinesVisible(true)
                    gameBoard.setAlignment(Pos.Center)
                    printCmd("Game board created")

                    // STATUS TEXT
                    val status = new Label("Game Started - Your Turn")
                    status.setStyle("-fx-font-size: 20px; -fx-font-weight: bold")
                    status.setAlignment(Pos.Center)

                    // CELLS AND STONES
                    val cells: Array[Array[Rectangle]] = Array.ofDim[Rectangle](SIZE, SIZE)
                    val stones: Array[Array[Circle]] = Array.ofDim[Circle](SIZE, SIZE)
                    printCmd("Arrays initialized")

                    // INITIALIZE CELLS AND STONES
                    for
                        i <- 0 until SIZE
                        j <- 0 until SIZE
                    do
                        val cell = new Rectangle {
                            width = 40
                            height = 40
                            fill =
                                if Vector((7, 7), (3, 11), (3, 3), (11, 3), (11, 11)).contains((i, j)) then
                                    Color.rgb(166, 142, 110)
                                else
                                    Color.Tan
                            stroke = Color.Black
                        }
                        val value: Int = game.mainBoard.matrix(i)(j)
                        val stone = new Circle {
                            visible = value != 0
                            fill = if value == 1 then Color.Black else Color.White
                            radius = 15
                        }

                        cells(i)(j) = cell
                        stones(i)(j) = stone

                        val cellContainer = new StackPane {
                            children = List(cell, stone)
                        }

                        cellContainer.onMouseClicked = e => {
                            if !game.finished then
                                // PLACE PLAYER'S STONE
                                val success1 = game.handlePlayerMove(j, i)
                                if success1 then
                                    // UPDATE THE UI
                                    updateBoard(game.mainBoard, status, false)

                                    // QUERY AND PLACE AI'S MOVE
                                    Platform.runLater {
                                        status.text = "Thinking..."

                                        new Thread(() => {
                                            val success2 = game.getAndPlayAIMove(j, i)

                                            Platform.runLater {
                                                if success2 then
                                                    updateBoard(game.mainBoard, status)
                                            }
                                        }).start()
                                    }
                        }

                        gameBoard.add(cellContainer, j, i)

                    printCmd("Board elements initialized")

                    def updateBoard(board: Board, status: Label, thinking: Boolean=false): Unit =
                        for
                            i <- 0 until SIZE
                            j <- 0 until SIZE
                        do
                            val value = board.matrix(i)(j)
                            stones(i)(j).stroke = Color.Green
                            if i == game.lastMove._2 && j == game.lastMove._1 then
                                stones(i)(j).strokeWidth = 3
                            else
                                stones(i)(j).strokeWidth = 0

                            stones(i)(j).visible = value != 0
                            stones(i)(j).fill = if (value == 1) Color.Black else Color.White

                        if game.finished then
                            game.agent.saveRootNode()
                            game.winner match
                                case 1 => status.text = "Game Over - AI Wins!"
                                case -1 => status.text = "Game Over - You Win!"
                                case 0 => status.text = "Game Over - Draw!"
                                case _ => status.text = "Invalid game state!"
                        else if thinking then
                            status.text = "Thinking..."
                        else
                            status.text = "Your Turn"

                    root = new VBox(20) {
                        children = Seq(status, gameBoard)
                        padding = Insets(20)
                        alignment = Pos.Center
                    }
                    printCmd("Scene setup completed")
                }
            }
            printCmd("Stage setup completed")
        catch
            case e: Exception =>
                printCmd(s"Error during initialization: ${e.getMessage}")
                e.printStackTrace()

    @main def launchGame(): Unit =
        printCmd("Launching application...")
        launch()