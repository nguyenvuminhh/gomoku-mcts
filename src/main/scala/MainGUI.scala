import scalafx.application.Platform
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, VBox, StackPane}
import scalafx.scene.control.{Button, Label}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import Constants.SIZE

object MainGUI extends JFXApp3:
    override def start(): Unit =
        val game = new Game(aiStarts = true, VERBOSE = false)
        game.start()

        stage = new JFXApp3.PrimaryStage {
            title = "Gomoku Game"
            width = 700
            height = 750

            scene = new Scene {
                // GAME BOARD
                val gameBoard = new GridPane()
                gameBoard.setGridLinesVisible(true)
                gameBoard.setAlignment(Pos.Center)

                // STATUS TEXT
                val status = new Label("Game Started - Your Turn")
                status.setStyle("-fx-font-size: 20px; -fx-font-weight: bold")
                status.setAlignment(Pos.Center)

                // CELLS AND STONES
                val cells: Array[Array[Rectangle]] = Array.ofDim[Rectangle](SIZE, SIZE)
                val stones: Array[Array[Circle]] = Array.ofDim[Circle](SIZE, SIZE)

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

                    // Add the container instead of individual elements
                    gameBoard.add(cellContainer, j, i)

                def updateBoard(board: Board, status: Label, thinking: Boolean=false): Unit =
                    for
                        i <- 0 until SIZE
                        j <- 0 until SIZE
                    do
                        val value = board.matrix(i)(j)
                        stones(i)(j).stroke = Color.Green
                        if i == game.lastAIMove._2 && j == game.lastAIMove._1 then
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

                //                val resetButton = new Button("New Game") {
                //                    onAction = e => {
                //                        // Implement reset functionality
                //                    }
                //                }

                root = new VBox(20) {
                    children = Seq(status, gameBoard/*, resetButton*/)
                    padding = Insets(20)
                    alignment = Pos.Center
                }
            }
        }