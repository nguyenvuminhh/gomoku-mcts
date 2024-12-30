import Constants.{EXPORTNODE, IMPORTNODE, MCTSTHRESHOLD, printCmd}

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MCTSAgent(val board: Board) extends Serializable:
    /**
     * Starting node of MCTS
     */
    private var node = loadRootNode()

    /**
     * Reference to the original node for serializing
     */
    private val originalNode: MCTSNode = node

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
        while node.visitCount < MCTSTHRESHOLD do node.simulation()
        printCmd(node.childrenToString)
        // GET THE BEST MOVE
        val bestMove = node.bestMove
        node = bestMove._2

        // RESTART THE SIMULATION THREAD ON THE NEW NODE
        running.set(true)
        start()
        lock.notifyAll()
        if node != null then printCmd(node.toString())
        bestMove._1
    }

    /**
     * A method that take in the move, then find the corresponding state in
     * the node's children (reuse). If not found, create a new one.
     *
     * @param move opponent's move
     */
    private def newTree(move: (Int, Int)): Unit =
        node = node.children.getOrElse(move, new MCTSNode(node, -board.currentTurn, createNewBoard(move), isRoot = true))

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

    /**
     * A method that serialize the original root node
     */
    def saveRootNode(): Unit =
        if !EXPORTNODE then return

        val filePath = "src/main/resources/node.obj"
        val file = new File(filePath)

        // DELETE OLD FILE IF EXISTS
        if (file.exists()) file.delete()

        // SAVE NEW OBJECT
        val fileOutput = new FileOutputStream(filePath)
        val objectOutput = new ObjectOutputStream(fileOutput)
        objectOutput.writeObject(originalNode)

        // CLOSE
        objectOutput.close()
        fileOutput.close()

    /**
     * A method that load the root node from file (if found).
     *
     * @return node from file if found
     *         new node otherwise
     */
    private def loadRootNode(): MCTSNode =
        if !IMPORTNODE then return new MCTSNode(null, board.currentTurn, board.clone(), isRoot = true)

        val filePath = "src/main/resources/node.obj"
        val file = new File(filePath)

        // IF FILE NOT FOUND RETURN NEW NODE
        if !file.exists() then
            return new MCTSNode(null, board.currentTurn, board.clone(), isRoot = true)

        // IF FILE FOUND THEN LOAD AND RETURN
        val fileInput = new FileInputStream(filePath)
        val objectInput = new ObjectInputStream(fileInput)
        val loadedNode = objectInput.readObject().asInstanceOf[MCTSNode]
        printCmd("Loaded node: ")
        printCmd(loadedNode.toString())
        objectInput.close()
        fileInput.close()
        loadedNode