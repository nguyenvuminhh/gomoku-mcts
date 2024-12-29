import Constants.MCTSSIMULATIONCOUNT

import scala.annotation.tailrec
import scala.util.Random

@SerialVersionUID(1L)
class Node(
              var parent: Node,
              val nextPlayerTurn: Int,
              val board: Board,
              @transient var isRoot: Boolean = false,
          ) extends Serializable:
    /** 
     * Default variable for serializing
     */
    var _isRoot = false

    /**
     * Total visit count
     */
    var visitCount = 0

    /**
     * Total value/reward. WIN = 1, LOSE = -1, DRAW = 0
     */
    private var totalValue = 0

    /**
     * All posible moves (just count cells with at least 1 cell near it 
     */
    private var possibleMoves = board.generateMoves()

    /**
     * A hashmap from next move to it correspond next child node
     */
    val children = collection.mutable.HashMap.empty[(Int, Int), Node]

    /**
     * Return result of the state of the node
     *
     * @return None if not terminal state
     *         1 if AI win
     *         -1 if AI lose
     *         0 if draw
     */
    var winner: Option[Int] = None

    /**
     * Variable indicating if the node has a faild child
     */
    var hasFailedChild = false

    /**
     * A method to check if the node has a faild grandchild
     * If true then the move is guaranteed loss
     *
     * @return true if there exists a child has a failed child
     *         false otherwise
     */
    private def hasFailedGrandChild: Boolean = children.exists((_, child) => child.hasFailedChild)

    /**
     * A method to check if the node is fully expanded
     *
     * @return true if fully expanded
     *         false otherwise
     */
    def fullyExpanded: Boolean = possibleMoves.isEmpty

    /**
     * A method to calculate ucb1 of the node
     *
     * @return +inf if not yet visited or has failed grandchild
     *         -inf if there is a failed child
     *         totalValue/visitCount + sqrt(2 * ln(ni)/n) otherwise
     */
    private def ucb =
        // CASE NOT YET VISITED OR HAS A FAILED GRANDCHILD
        if visitCount == 0 || children.exists((_, child) => child.hasFailedChild) then
            Double.MaxValue
        // CASE HAS A FAILED CHILD
        else if hasFailedChild then
            Double.MinValue
        // ELSE
        else
            totalValue*1.0/visitCount + Math.sqrt(2 * Math.log(parent.visitCount) / visitCount)

    /**
     * A method return the metric that decide which node to be the best move
     *
     * @return -inf if there is a failed child
     *         visitCount otherwise
     */
    private def metric =
        if hasFailedGrandChild then
            Double.MinValue
        else
            visitCount*1.0

    /**
     * A method return best move
     *
     * @return ((-1, -1), null) if game ended
     *         (move, successor node) otherwise
     */
    def bestMove: ((Int, Int), Node) =
        if children.nonEmpty then
            children.maxBy((_, child) => child.metric)
        else
            ((-1, -1), null)

    /**
     * A method used to select node to expand
     *
     * @return this if this is not fully expanded or a terminal node
     *         recursively the best child according to UCB1
     */
    @tailrec
    private final def select: Node =
        // CASE NOT FULLY EXPANDED OR A TERMINAL NODE
        if !fullyExpanded || winner.nonEmpty then
            this
        // ELSE
        else
            children.values.maxBy(_.ucb).select

    /**
     * A method to expand a node
     *
     * @return New node that is expanded from the selected node
     */
    private def expand(): Node =
        require(!fullyExpanded, "Cannot expand a fully expanded node")
        // DEQUEUE NEXT MOVE
        val nextMove = possibleMoves.head
        possibleMoves = possibleMoves.tail

        // CREATE NEW BOARD
        val newBoard = board.clone()
        val gameResult = newBoard.placeStone(nextMove, nextPlayerTurn)

        // CREATE NEW NODE
        val newNode = new Node(this, -nextPlayerTurn, newBoard)
        if gameResult._2 then
            newNode.winner = Some(gameResult._1)
            hasFailedChild = newNode.winner.contains(-1)

        // APPEND AND RETURN
        children.addOne(nextMove, newNode)
        newNode

    /**
     * A method used to backpropagate the simulation result
     *
     * @param value the value for updating. WIN = 1, LOSE = -1, DRAW = 0
     */
    @tailrec
    final private def update(value: Int): Unit =
        visitCount += 1
        totalValue += value
        if !this.isRoot && parent != null then parent.update(value)

    /**
     * A method used to simulate the new node.
     * The new node will be simulation `MCTSSIMULATIONCOUNT` times
     */
    def simulation(): Unit =
        for _ <- 0 until MCTSSIMULATIONCOUNT do
            // SELECTION
            val nodeToExpand = select

            // EXPANSION
            val newNode =
                if nodeToExpand.winner.nonEmpty then
                    nodeToExpand
                else
                    nodeToExpand.expand()

            // SIMULATION (only if no winner yet)
            val result =
                if newNode.winner.nonEmpty then
                    newNode.winner.get
                else
                    // SET UP
                    val dummyBoard = newNode.board.clone()
                    var simResult = (0, false)
                    var turn = newNode.nextPlayerTurn

                    // RANDOM PLAY UNTIL REACHING TERMINAL
                    while !simResult._2 do
                        val moves = dummyBoard.generateMoves()
                        if moves.isEmpty then simResult = (0, true)
                        else
                            val randomMove = moves(Random.nextInt(moves.length))
                            simResult = dummyBoard.placeStone(randomMove, turn)
                            turn = -turn
                    simResult._1

            // BACKPROPAGATION
            newNode.update(result)

    /**
     * Overiding toString
     * 
     * @return Board + totalValue + visitCount + 
     */
    override def toString: String =
        "Current Board: \n" +
        board.toString() + "\n" +
        "Value: " + totalValue + " | Visit counts: " + visitCount + "\n" +
        (if nextPlayerTurn == 1 then "AI's turn" else "Player's turn") + "\n" +
        "Has failed child: " + hasFailedChild + "\n" +
        "Has failed grandchild: " + hasFailedGrandChild + "\n"
