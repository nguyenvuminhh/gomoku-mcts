// Agent.scala
import Constants.{DEPTH, SIZE}

import java.lang.Math.random

// Assuming a Board class exists with the necessary methods
class Agent(private var board: Board) {

}

object Agent {
    val WIN_SCORE: Int = 100_000_000
    def calculateNextMove(depth: Int, board: Board): (Byte, Byte, Double) =
        // Block the board for AI to make a decision.
        var move: Array[Byte] = Array(0, 0)
        val bestMove = Agent.minimaxSearchAB(depth, board, 1, alpha = -Double.MaxValue, beta = Double.MaxValue)
        bestMove
    def evaluateBoard(board: Board): Double =
        val matrix = board.matrix
        val AIScore = getScore(matrix, forAI = true)
        val playerScore = getScore(matrix, forAI = false)
        println("===================================")
        println(board)
        println("AI Score: " + AIScore + " player Score: " + playerScore)
        println("===================================")
        AIScore*1.0 / (playerScore + 1e-9)

    def getScore(matrix: Array[Array[Byte]], forAI: Boolean): Int =
        evaluateHorizontal(matrix, forAI) +
        evaluateVertical(matrix, forAI) +
        evaluateDiagonal(matrix, forAI) +
        evaluateAntiDiagonal(matrix, forAI)

    /**
     * Minimax search with Alpha-Beta pruning.
     *
     * @param depth     The current depth in the game tree.
     * @param dummyBoard The current state of the board.
     * @param max       True if the current move is for the maximizer (AI), false otherwise.
     * @param alpha     The best already explored option along the path to the maximizer.
     * @param beta      The best already explored option along the path to the minimizer.
     * @return A tuple containing the score and the move coordinates.
     */
    def minimaxSearchAB(depth: Int, board: Board, player: Byte, alpha: Double, beta: Double): (Byte, Byte, Double) =
        if depth == 0 then return (-1, -1, random())
        val allPossibleMoves: Vector[(Byte, Byte)] = board.generateMoves(player, false)
        if (allPossibleMoves.isEmpty) then return (-1, -1, 0)
        if player == 1 then
            var value = Double.MinValue
            var bestX: Byte = -1
            var bestY: Byte = -1
            var currentAlpha = alpha

            for move <- allPossibleMoves do
                board.placeStone(move._1, move._2, player)
                val eval = minimaxSearchAB(depth - 1, board, (-player).toByte, currentAlpha, beta)._3
                board.removeStone(move._1, move._2)
                println("c: " + move + " h: " + eval + " d: " + depth)
                if eval > value then
                    value = eval
                    bestX = move._1
                    bestY = move._2
                currentAlpha = Math.max(currentAlpha, value)
                if value >= beta then return (bestX, bestY, value)

            (bestX, bestY, value)

        else
            var value = Double.MaxValue
            var bestX: Byte = -1
            var bestY: Byte = -1
            var currentBeta = beta

            for move <- allPossibleMoves do
                board.placeStone(move._1, move._2, player)
                val eval = minimaxSearchAB(depth - 1, board, (player * -1).toByte, alpha, currentBeta)._3
                board.removeStone(move._1, move._2)
                println("c: " + move + " h: " + eval + " d: " + depth)
                if eval < value then
                    value = eval
                    bestX = move._1
                    bestY = move._2;

                currentBeta = Math.min(currentBeta, value)
                if value <= alpha then return (bestX, bestY, value)

            (bestX, bestY, value)


    /**
     * Searches for a move that can instantly win the game.
     *
     * @param board The current state of the board.
     * @return An Option containing the winning move coordinates if found.
     */
//    def searchWinningMove(board: Board): Option[(Double, Int, Int)] = {
//        val allPossibleMoves: Array[Array[Int]] = board.generateMoves().toArray
//        val winningMove = ArrayBuffer[Int]()
//
//        // Iterate over all possible moves
//        for (move <- allPossibleMoves) {
//            // Create a temporary board that is equivalent to the current board
//            val dummyBoard = new Board(board)
//            // Play the move on that temporary board without drawing anything
//            dummyBoard.addStoneNoGUI(move(1), move(0), forAI = false)
//
//            // If the white player has a winning score in that temporary board, return the move.
//            if (getScore(dummyBoard, forAI = false, blacksTurn = false) >= WIN_SCORE) {
//                return Some((getScore(dummyBoard, forAI = false, blacksTurn = false).toDouble, move(0), move(1)))
//            }
//        }
//        None
//    }

    def evaluateHorizontal(boardMatrix: Array[Array[Byte]], forAI: Boolean): Int =
        val evaluations = Array(0, 2, 0) // [0] -> consecutive count, [1] -> block count, [2] -> score

        for
            i <- 0 until SIZE
            j <- 0 until SIZE
        do
            evaluateDirections(boardMatrix, i, j, forAI, evaluations)

        evaluateDirectionsAfterOnePass(evaluations, forAI)

        evaluations(2)

    def evaluateVertical(boardMatrix: Array[Array[Byte]], forAI: Boolean): Int =
        val evaluations = Array(0, 2, 0)

        for
            j <- 0 until SIZE
            i <- 0 until SIZE
        do
            evaluateDirections(boardMatrix, i, j, forAI, evaluations)

        evaluateDirectionsAfterOnePass(evaluations, forAI)
        
        evaluations(2)

    def evaluateDiagonal(boardMatrix: Array[Array[Byte]], forAI: Boolean): Int =
        val evaluations = Array(0, 2, 0)

        for k <- (1 - SIZE) until SIZE do
            val iStart = Math.max(0, k)
            val iEnd = Math.min(SIZE + k - 1, SIZE - 1)
            for (i <- iStart to iEnd) do
                val j = i - k
                evaluateDirections(boardMatrix, i, j, forAI, evaluations)
            evaluateDirectionsAfterOnePass(evaluations, forAI)

        evaluations(2)

    def evaluateAntiDiagonal(boardMatrix: Array[Array[Byte]], forAI: Boolean): Int =
        val evaluations = Array(0, 2, 0)

        for k <- 0 to (2 * (SIZE - 1)) do
            val iStart = Math.max(0, k - SIZE + 1)
            val iEnd = Math.min(SIZE - 1, k)
            for i <- iStart to iEnd do
                val j = k - i
                evaluateDirections(boardMatrix, i, j, forAI, evaluations)
            evaluateDirectionsAfterOnePass(evaluations, forAI)

        evaluations(2)

    /**
     * Evaluates the current direction (horizontal, vertical, or diagonal).
     */
    def evaluateDirections(boardMatrix: Array[Array[Byte]], i: Int, j: Int, forAI: Boolean, eval: Array[Int]): Unit = {
        // Check if the selected player has a stone in the current cell
        if (boardMatrix(i)(j) == (if (forAI) 1 else -1)) {
            // Increment consecutive stones count
            eval(0) += 1
        }
        // Check if cell is empty
        else if (boardMatrix(i)(j) == 0) {
            // Check if there were any consecutive stones before this empty cell
            if (eval(0) > 0) {
                // Consecutive set is not blocked by opponent, decrement block count
                eval(1) -= 1
                // Get consecutive set score
                eval(2) += getConsecutiveSetScore(eval(0), eval(1), forAI)
                // Reset consecutive stone count
                eval(0) = 0
                // Current cell is empty, next consecutive set will have at most 1 blocked side.
            }
            // No consecutive stones.
            // Current cell is empty, next consecutive set will have at most 1 blocked side.
            eval(1) = 1
        }
        // Cell is occupied by opponent
        else {
            // Check if there were any consecutive stones before this cell
            if (eval(0) > 0) {
                // Get consecutive set score
                eval(2) += getConsecutiveSetScore(eval(0), eval(1), forAI)
                // Reset consecutive stone count
                eval(0) = 0
                // Current cell is occupied by opponent, next consecutive set may have 2 blocked sides
                eval(1) = 2
            } else {
                // Current cell is occupied by opponent, next consecutive set may have 2 blocked sides
                eval(1) = 2
            }
        }
    }

    /**
     * Evaluates after completing a pass through a row, column, or diagonal.
     */
    private def evaluateDirectionsAfterOnePass(eval: Array[Int], forAI: Boolean): Unit = {
        // End of line, check if there were any consecutive stones before reaching the border
        if (eval(0) > 0) {
            eval(2) += getConsecutiveSetScore(eval(0), eval(1), forAI)
        }
        // Reset consecutive stone and blocks count
        eval(0) = 0
        eval(1) = 2
    }

    /**
     * Calculates the score for a set of consecutive stones.
     *
     * @param count        Number of consecutive stones in the set.
     * @param blocks       Number of blocked sides of the set (2: both sides blocked, 1: single side blocked, 0: both sides free).
     * @param currentTurn  True if it's the current player's turn, false otherwise.
     * @return The score for the consecutive set.
     */
    def getConsecutiveSetScore(count: Int, blocks: Int, forAI: Boolean): Int = {
        val winGuarantee = 1_000_000
        // If both sides of a set are blocked and count is less than 5, this set is worthless.
        if (blocks == 2 && count < 5) return 0

        count match {
            case 5 => WIN_SCORE
            case 4 => if forAI then winGuarantee else if blocks == 0 then winGuarantee*5 / 2 else winGuarantee / 4
            case 3 =>
                if forAI then
                    if blocks == 0 then 50_000
                    else 10
                else
                    if blocks == 0 then 200
                    else 5
            case 2 =>
                if forAI then
                    if blocks == 0 then 7
                    else 5
                else
                    if blocks == 0 then 5
                    else 3
            case 1 => 1
            case _ => WIN_SCORE * 2
        }
    }
}
