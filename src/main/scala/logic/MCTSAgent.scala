package logic

import logic.GameSettings.{EXPORTNODE, IMPORTNODE, MCTSTHRESHOLD, printCmd}

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

class MCTSAgent(val board: Board) extends Serializable:
    /**
     * Starting node of MCTS
     */
    private var node = new MCTSNode(null, board.currentTurn, board.clone())

    /**
     * A flag for MCTS' thread
     */
    @volatile private var running = AtomicBoolean(true)

    /**
     * Lock object for synchorization
     */
    private val lock = new Object

    /**
     * Start the process of MCTS
     */
    def start(): Unit = Future {
        while running.get() && node.visitCount < MCTSTHRESHOLD do
            node.simulation()
    }

    /**
     * A method that take in opponent's move and return next move
     *
     * @param move opponent's move
     * @return move made by AI
     */
    def updateOpponentMoveAndGetBestMove(move: (Int, Int)): (Int, Int) = lock.synchronized {
        // PAUSE THE THREAD
        running.set(false)
        lock.notifyAll()

        // UPDATE NODE/TREE AFTER THE OPPONENT'S MOVE
        newTree(move)

        printCmd("Moves considered. Thinking next move...")
        // KEEP SIMULATING UNTIL REACHING THRESHOLD
        while node.visitCount < MCTSTHRESHOLD do
            node.simulation()
        // GET THE BEST MOVE
        val bestMove = node.bestMoveX
        printCmd("Next move is " + bestMove._1)
        node = bestMove._2
        // RESTART THE SIMULATION THREAD ON THE NEW NODE
        running.set(true)
        start()
        lock.notifyAll()
        bestMove._1
    }

    /**
     * A method that take in the move, then find the corresponding state in
     * the node's children (reuse). If not found, create a new one.
     *
     * @param move opponent's move
     */
    private def newTree(move: (Int, Int)): Unit =
        node = node.children.getOrElse(move, new MCTSNode(node, -board.currentTurn, createNewBoard(move)))
        node.parent = null

    /**
     * A method that create a new board for the new state (if cannot reuse)
     *
     * @param move opponent's move
     * @return new cloned board after the move
     */
    private def createNewBoard(move: (Int, Int)): Board =
        val newBoard = node.board.clone()
        newBoard.placeStone(move, newBoard.currentTurn)
        newBoard
