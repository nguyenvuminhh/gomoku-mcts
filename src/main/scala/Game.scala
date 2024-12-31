// Game.scala
import scala.io.StdIn.readLine
import GameSettings.{SIZE, printCmd}

class Game(aiStarts: Boolean = true) extends Serializable:
    /**
     * A variable used to indicate whether the next turn is
     * player's turn
     */
    private var isPlayersTurn: Boolean = true

    /**
     * A variable used to indicate whether the game is finished
     */
    var finished: Boolean = false

    /**
     * A variable used to indicate the winner once the game is
     * finished
     *
     * @return 1 if AI win
     *         -1 if player win
     *         0 if draw
     */
    var winner: Int = 0

    /**
     * A variable holding the main board of the game
     */
    val mainBoard = new Board(step = 0, currentTurn = if aiStarts then 1 else -1)

    /**
     * A variable holding the last move made by AI
     */
    var lastMove: (Int, Int) = (-1, -1)
    /**
     * An object of Monte Carlo Search Tree agent
     */
    var agent = MCTSAgent(mainBoard.clone())

    /**
     * A method used to initialize the game. AI will play its first move
     * if it is its turn, and the agent will start building tree
     */
    def start(): Unit =
        if aiStarts then
            val mid = SIZE / 2
            playMove(mid, mid, 1)
            agent = MCTSAgent(mainBoard.clone())
//        playMove(7, 7, -1)
//        playMove(7, 6, -1)
//        playMove(7, 5, -1)
////        playMove(6, 6, -1)
//        playMove(5, 5, -1)
//        playMove(8, 5, -1)
//        playMove(8, 4, -1)
//        playMove(8, 10, -1)
//
//        playMove(4, 4, 1)
//        playMove(6, 5, 1)
//        playMove(8, 6, 1)
//        playMove(8, 7, 1)
//        playMove(8, 8, 1)
//        playMove(8, 9, 1)
//        playMove(7, 8, 1)
////        playMove(6, 9, 1)
        agent = MCTSAgent(mainBoard.clone())

        agent.start()

    /**
     * A method used to play a move
     *
     * @param x column index
     * @param y row index
     * @param player value of the move. 1 for AI, -1 for player.
     * @return true if the move is placed successfully
     *         false otherwise
     */
    private def playMove(x: Int, y: Int, player: Int): Boolean =
        // CHECK IF MOVE IS PLACABLE
        if !mainBoard.isValidCoord(x, y) || mainBoard.isOccupied(x, y) then return false

        // PLACE THE STONE
        val gameResult = mainBoard.placeStone(x, y, player)
        winner = gameResult._1
        finished = gameResult._2
        if finished then
            winner match
                case 1 => printCmd("GAME OVER! AI WIN!")
                case -1 => printCmd("GAME OVER! YOU WIN!")
                case 0 => printCmd("GAME OVER! DRAW!")
                case _ => throw Exception("ILLEGAL VALUE IN PRINT RESULT")
        true

    /**
     * A method used to handle player's move
     *
     * @param x column index
     * @param y row index
     * @return true if the move is placed successfully
     *         false otherwise
     */
    def handlePlayerMove(x: Int, y: Int): Boolean =
        if (finished || !isPlayersTurn) return false

        isPlayersTurn = false
        if !playMove(x, y, -1) then
            isPlayersTurn = true
            return false
        lastMove = (x, y)
        true

    /**
     * A method used to query and place move produced by the agent
     *
     * @param x column index of player's previous move
     * @param y row index of player's previous move
     * @return true if the move is placed successfully
     *         false otherwise
     */
    def getAndPlayAIMove(x: Int, y: Int): Boolean =
        // GET MOVE
        val (bestX, bestY) = agent.updateOpponentMoveAndGetBestMove((x, y))

        // IF NOT FOUND
        if bestX == -1 then
            printCmd("No possible moves left. Game Over.")
            finished = true
            return true

        // PLACE MOVE
        if !playMove(bestX, bestY, 1) then
            throw new IllegalArgumentException(s"AI made illegal move: $bestX $bestY")
        lastMove = (bestX, bestY)
        isPlayersTurn = true
        true

