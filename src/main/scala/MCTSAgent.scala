import Constants.MCTSTHRESHOLD

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Random

class MCTSAgent(val board: Board):
    private val running = new AtomicBoolean(true)
    private var node = new Node(null, board.currentTurn, board.clone())
    private val lock = new Object()

    def start(): Unit = Future {
        while running.get() do
            lock.synchronized {
                if running.get() then  // Double check inside sync block
                    node.simulation()
            }
    }

    def stop(): Unit =
        running.set(false)

    def updateOpponentMoveAndGetBestMove(move: (Int, Int)): (Int, Int) =
        lock.synchronized {
            // Stop background simulations
            running.set(false)

            // Update tree with opponent's move
            newTree(move)

            // Run simulations until threshold
            var count = 0
            while count < MCTSTHRESHOLD do
                node.simulation()
                count += 1

            // Get best move
            val bestMove = node.bestMove
            node = bestMove._2

            // Restart background simulations
            running.set(true)

            bestMove._1
        }

    private def newTree(move: (Int, Int)): Unit =
        node = node.childrens.getOrElse(move, new Node(null, -board.currentTurn, createNewBoard(move)))
        node.parent = null

    private def createNewBoard(move: (Int, Int)) =
        val newBoard = board.clone()
        newBoard.placeStone(move, newBoard.currentTurn)
        newBoard