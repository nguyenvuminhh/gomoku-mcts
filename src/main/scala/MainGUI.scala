// MainGUI.scala
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.scene.control.{Button, Label}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import Constants.SIZE

object MainGUI extends JFXApp3 {
    override def start(): Unit = {
        val game = new Game(aiStarts = true, VERBOSE = false)
        game.start()
        Thread.sleep(1000)
        stage = new JFXApp3.PrimaryStage {
            title = "Gomoku Game"
            width = 800
            height = 850

            scene = new Scene {
                val gameBoard = new GridPane()
                gameBoard.setGridLinesVisible(true)
                gameBoard.setAlignment(Pos.Center)

                val status = new Label("Game Started - Your Turn")
                status.setStyle("-fx-font-size: 20px")
                status.setAlignment(Pos.Center)

                // Create the board cells
                val cells = Array.ofDim[Rectangle](SIZE, SIZE)
                val stones = Array.ofDim[Circle](SIZE, SIZE)

                for {
                    i <- 0 until SIZE
                    j <- 0 until SIZE
                } {
                    val cell = new Rectangle {
                        width = 40
                        height = 40
                        fill = Color.Tan
                        stroke = Color.Black
                    }
                    val value: Int = game.mainBoard.matrix(i)(j)
                    val stone = new Circle {
                        visible = value != 0
                        fill = if (value == 1) Color.Black else Color.White
                        radius = 15
                    }

                    cells(i)(j) = cell
                    stones(i)(j) = stone

                    cell.onMouseClicked = e => {
                        if (!game.finished) {
                            val success = game.handlePlayerMove(j, i)  // Note: x and y are swapped for grid
                            if (success) {
                                updateBoard(game.mainBoard, status)
                            }
                        }
                    }

                    gameBoard.add(cell, j, i)
                    gameBoard.add(stone, j, i)
                }

                def updateBoard(board: Board, status: Label): Unit = {
                    for {
                        i <- 0 until SIZE
                        j <- 0 until SIZE
                    } {
                        val value = board.matrix(i)(j)
                        stones(i)(j).visible = value != 0
                        stones(i)(j).fill = if (value == 1) Color.Black else Color.White
                    }

                    if (game.finished) {
                        game.winner match {
                            case 1 => status.text = "Game Over - AI Wins!"
                            case -1 => status.text = "Game Over - You Win!"
                            case 0 => status.text = "Game Over - Draw!"
                            case _ => status.text = "Invalid game state!"
                        }
                    } else {
                        status.text = "Your Turn"
                    }
                }

                val resetButton = new Button("New Game") {
                    onAction = e => {
                        // Implement reset functionality
                        // This would need to be added to your Game class
                    }
                }

                // Layout
                root = new VBox(20) {
                    children = Seq(status, gameBoard, resetButton)
                    padding = Insets(20)
                    alignment = Pos.Center
                }
            }
        }
    }
}
