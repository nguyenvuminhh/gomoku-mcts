import scala.annotation.tailrec
import scala.util.Random
class Node(
              var parent: Node,
              val nextPlayerTurn: Int,
              val board: Board
          ):
    var visitCount = 0
    private var totalValue = 0
    private val epsilon = Double.MinPositiveValue
    private var possibleMoves = board.generateMoves()
    val childrens = collection.mutable.HashMap.empty[(Int, Int), Node]
    var winner: Option[Int] = None

    def fullyExpanded: Boolean = possibleMoves.isEmpty

    private def ucb =
        if visitCount == 0 then Double.MaxValue
        else totalValue*1.0/visitCount + Math.sqrt(2 * Math.log(parent.visitCount) / (visitCount + epsilon))

    def bestMove: ((Int, Int), Node) = childrens.maxBy(kv => kv._2.visitCount)

    @tailrec
    final def select: Node =
        if !fullyExpanded then this
        else if winner.nonEmpty then this
        else childrens.values.maxBy(_.ucb).select

    def expand(): Node =
        require(!fullyExpanded, "Cannot expand a fully expanded node")
        val nextMove = possibleMoves.head
        possibleMoves = possibleMoves.tail

        val newBoard = board.clone()
        val gameResult = newBoard.placeStone(nextMove, nextPlayerTurn)
        val newNode = new Node(this, -nextPlayerTurn, newBoard)
        if gameResult._2 then newNode.winner = Some(gameResult._1)
        childrens.addOne(nextMove, newNode)
        newNode

    @tailrec
    final private def update(value: Int): Unit =
        visitCount += 1
        totalValue += value
        if parent != null then parent.update(value)

    def simulation(): Unit =
        // Selection
        val nodeToExpand = select

        // Expansion
        val newNode = if nodeToExpand.winner.nonEmpty then nodeToExpand
        else nodeToExpand.expand()

        // Simulation (only if no winner yet)
        val result = if newNode.winner.nonEmpty then newNode.winner.get
        else {
            val dummyBoard = newNode.board.clone()
            print(dummyBoard)

            var simResult = (0, false)
            var turn = newNode.nextPlayerTurn
            while !simResult._2 do
//                println(dummyBoard)
                val moves = dummyBoard.generateMoves()
                if moves.isEmpty then simResult = (0, true)
                else
                    val randomMove = moves(Random.nextInt(moves.length))
                    simResult = dummyBoard.placeStone(randomMove, turn)
                    turn = -turn
            simResult._1
        }

        // Backpropagation
        newNode.update(result)