import Constants.{MCTSSIMULATIONCOUNT, MCTSTHRESHOLD}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Random

class MCTSAgent(val board: Board):
    private var node = new Node(null, board.currentTurn, board.clone())
    @volatile var running = AtomicBoolean(true) // Controls if the loop runs
    private val lock = new Object

    def start(): Unit = Future {
        while running.get() do
            node.simulation()
    }


    def updateOpponentMoveAndGetBestMove(move: (Int, Int)): (Int, Int) = lock.synchronized {
        println("ASD")
        running.set(false)
        lock.notifyAll()
        print("wait")
        newTree(move)
        while node.visitCount < MCTSTHRESHOLD do
            //println(node.visitCount + "/" + MCTSTHRESHOLD)
            //println(node.board)
            node.simulation()

        val bestMove = node.bestMove
        node = bestMove._2

        // Restart background simulations
        running.set(true)
        lock.notifyAll()
        if node != null then print(node)
        bestMove._1
    }

    private def newTree(move: (Int, Int)): Unit =
        node = node.childrens.getOrElse(move, new Node(null, -board.currentTurn, createNewBoard(move)))
        node.parent = null

    private def createNewBoard(move: (Int, Int)) =
        val newBoard = board.clone()
        newBoard.placeStone(move, newBoard.currentTurn)
        newBoard