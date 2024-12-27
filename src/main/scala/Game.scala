// Game.scala
import scala.io.StdIn.readLine
import Constants.{CLAMP_DIST, DEPTH, SIZE}

class Game(aiStarts: Boolean = true, VERBOSE: Boolean = true):
    private var isPlayersTurn: Boolean = true
    var finished: Boolean = false
    var winner: Int = 0
    val mainBoard = new Board(step = 0, currentTurn = if aiStarts then 1 else -1)


    def start(): Unit =
        if aiStarts then
            val mid = SIZE / 2
            playMove(mid, mid, 1)
            isPlayersTurn = true

    val agent = MCTSAgent(mainBoard.clone())
    agent.start()

    def playMove(x: Int, y: Int, player: Int): Boolean =
        if !(0 <= x && x < SIZE && 0 <= y && y < SIZE) || mainBoard.isOccupied(x, y) then return false

        val result = mainBoard.placeStone(x, y, player)
        winner = result._1
        finished = result._2
        if finished then
            winner match
                case 1 => printCmd("GAME OVER! AI WIN!")
                case -1 => printCmd("GAME OVER! YOU WIN!")
                case 0 => printCmd("GAME OVER! DRAW!")
                case _ => throw Exception("ILLEGAL VALUE IN PRINT RESULT")
        true


    def handlePlayerMove(x: Int, y: Int): Boolean =
        if (finished || !isPlayersTurn) return false

        isPlayersTurn = false

        if !playMove(x, y, -1) then
            isPlayersTurn = true
            return false

        val (bestX, bestY) = agent.updateOpponentMoveAndGetBestMove((x, y))

        if bestX == -1 then
            printCmd("No possible moves left. Game Over.")
            finished = true
            return true

        if !playMove(bestX, bestY, 1) then
            throw new IllegalArgumentException(s"AI made illegal move: $bestX $bestY")

        isPlayersTurn = true
        true

    private def printCmd(message: String): Unit = if VERBOSE then println(message)

    def startCLI(): Unit =
        printCmd("Welcome to Gomoku!")
        printCmd("Enter moves in format 'x y' (e.g., '7 7')")
        printCmd("Board coordinates are 0-14")
        while !finished do

            printCmd("\nCurrent board:")
            printCmd(mainBoard.toString)
            println(isPlayersTurn.toString)

            if isPlayersTurn then
                var done = false
                while !done do
                    try {
                        val move = readLine("Your move (x y): ").split(" ").map(_.toInt)
                        val x = move(0)
                        val y = move(1)
                        if handlePlayerMove(x, y) then done = true
                        else printCmd("Invalid move, try again!")
                    } catch
                        case exception: Exception => throw exception


        printCmd("\nFinal board:")
        printCmd(mainBoard.toString)