package logic

import javafx.application.Application.launch
import logic.GameSettings.{SIZE, printCmd, setDifficulties}
import logic.MainGUI.stage
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.layout.{GridPane, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}

object MainGUI extends JFXApp3:
    override def start(): Unit =
        try
            printCmd("Starting MainGUI initialization...")
            // SELECT DIFFICULTIES
            val ButtonLevel1 = new ButtonType("Level 1")
            val ButtonLevel2 = new ButtonType("Level 2")
            val ButtonLevel3 = new ButtonType("Level 3")
            val ButtonLevel4 = new ButtonType("Level 4")
            val ButtonLevel5 = new ButtonType("Level 5")

            val alertDifficulties = new Alert(AlertType.Confirmation) {
                initOwner(stage)
                title = "Select Difficulties"
                headerText = "NOTE: The more difficult, the more time the computer takes in each move."
                contentText = "Choose your option. Level 2-3 should be OK"
                buttonTypes = Seq(ButtonLevel1, ButtonLevel2, ButtonLevel3, ButtonLevel4, ButtonLevel5)
            }

            val resultDifficulties = alertDifficulties.showAndWait()
            val dialogPane = alertDifficulties.getDialogPane

            resultDifficulties match
                case Some(ButtonLevel1) => setDifficulties(1)
                case Some(ButtonLevel2) => setDifficulties(2)
                case Some(ButtonLevel3) => setDifficulties(3)
                case Some(ButtonLevel4) => setDifficulties(4)
                case Some(ButtonLevel5) => setDifficulties(5)

            // SELECT WHO GOES FIRST
            val ButtonAIFirst = new ButtonType("AI")
            val ButtonHumanFirst = new ButtonType("Human")
            val alertAIFirst = new Alert(AlertType.Confirmation) {
                initOwner(stage)
                title = "Who goes first"
                headerText = "Choose who go first."
                contentText = "Choose your option."
                buttonTypes = Seq(ButtonAIFirst, ButtonHumanFirst)
            }

            val resultAIFirst = alertAIFirst.showAndWait()
            resultAIFirst match
                case Some(ButtonAIFirst) => GameSettings.AIFIRST = true
                case Some(ButtonHumanFirst) => GameSettings.AIFIRST = false

            // INITIALIZE AND START GAME
            val game = new Game(aiStarts = GameSettings.AIFIRST)
            printCmd("Game instance created")
            game.start()
            printCmd("Game started")

            // INITIALIZE STAGE
            stage = new JFXApp3.PrimaryStage {
                title = "Gomoku Game"
                width = 700
                height = 750
                // INITIALIZE GAME
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
                                    updateBoard(game.mainBoard, status)

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