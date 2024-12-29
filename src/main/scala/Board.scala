import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer

import Constants.SIZE

@SerialVersionUID(1L)
class Board (
    val matrix: Array[Array[Int]] = Array.fill(SIZE, SIZE)(0),
    var currentTurn: Int = 1,
    var step: Int = 0,
    ) extends Serializable:

    /**
     * A method used to hash the matrix
     *
     * @return hashed object
     */
    override def hashCode(): Int = matrix.map(_.toSeq).toSeq.hashCode()

    /**
     * A method used to clone the board
     *
     * @return cloned board
     */
    override def clone(): Board = new Board(matrix.map(_.clone()), currentTurn, step)

    /**
     * A method used to check if the cell is occupied
     *
     * @param x column index
     * @param y row index
     * @return true if occupied
     *         false otherwise
     */
    def isOccupied(x: Int, y: Int): Boolean = matrix(y)(x) != 0

    /**
     * A method used to check if the coordinate is valid
     *
     * @param x column index
     * @param y row index
     * @return true if is in range (0, 0) -> (SIZE, SIZE)
     *         false otherwise
     */
    def isValidCoord(x: Int, y: Int) = 0 <= x && x < SIZE && 0 <= y && y < SIZE

    /**
     * A method used to overload the method placeStone(x: Int, y: Int, value: Int)
     *
     * @see placeStone(x: Int, y: Int, value: Int): (Int, Boolean)
     */
    def placeStone(xy: (Int, Int), value: Int): (Int, Boolean) = placeStone(xy._1, xy._2, value)

    /**
     * A method used to place the stone
     *
     * @param x column index
     * @param y row index
     * @param value value of player (1 for AI, -1 for player)
     * @return (winner, finished)
     */
    def placeStone(x: Int, y: Int, value: Int): (Int, Boolean) =
        require(math.abs(value) == 1, "Value must be 1 or -1")
        require(isValidCoord(x, y), "Coord must be valid")
        require(!isOccupied(x, y), "Cell must be empty")
        require(currentTurn == value)
        step += 1
        matrix(y)(x) = value

        if step == SIZE * SIZE then
            return (0, true)

        if checkTerminal(x, y, value) then
            return (value, true)

        currentTurn = -currentTurn
        (0, false)

    /**
     * A method used to remove stone
     * @param x column index
     * @param y row index
     */
    def removeStone(x: Int, y: Int): Unit =
        require(isValidCoord(x, y), "Coord must be valid")
        require(isOccupied(x, y), "Cell must not be empty")
        step -= 1
        matrix(y)(x) = 0
        currentTurn = -currentTurn

    /**
     * A method used to check if terminal state is reached
     *
     * @param x column index of the latest move
     * @param y row index of the latest move
     * @param player value of the latest move
     * @return true if terminal is reached
     *         false otherwise
     */
    private def checkTerminal(x: Int, y: Int, player: Int): Boolean =
        // HELPER METHOD TO COUNT STONE
        def countStones(dx: Int, dy: Int): Int =
            var count = 1
            var done = false
            while !done do
                for (d <- 1 to 4) do
                    val nx = x + d * dx
                    val ny = y + d * dy
                    if !isValidCoord(nx, ny) || matrix(ny)(nx) != player then
                        return count
                    else
                        count += 1
                done = true
            count

        // HORIZONTAL, VERTICAL, DIAGONAL, ANTI-DIAGONAL
        val directions = Seq((1, 0), (0, 1), (1, 1), (1, -1))
        directions.exists { (dx, dy) => countStones(dx, dy) + countStones(-dx, -dy) - 1 >= 5 }

    /**
     * A method used to generate moves (empty cells) that have at least 1 occupied cell nearby
     *
     * @return A vector of such moves
     */
    def generateMoves(): Vector[(Int, Int)] =
        val moveList = ArrayBuffer[(Int, Int)]()

        for
            i <- 0 until SIZE
            j <- 0 until SIZE
        do
            if !isOccupied(i, j) then // SKIP OCCUPIED CELL
                val hasStoneNearby = (-1 to 1).exists { di =>
                    (-1 to 1).exists { dj =>
                        val ni = i + di
                        val nj = j + dj
                        isValidCoord(ni, nj) && isOccupied(ni, nj)
                        }
                    }
                    if hasStoneNearby then
                        val move = (i, j)
                        moveList += move

        moveList.toVector

    /**
     * A method used to override toString
     *
     * @return A board with coord
     */
    override def toString: String =
        val sb = new StringBuilder("   ")
        sb.append("012345678901234").append("\n")
        for y <- 0 until SIZE do
            sb.append(f"$y%2d ")
            for x <- 0 until SIZE do
                sb.append(matrix(y)(x) match {
                    case 1 => "X"
                    case -1 => "O"
                    case _ => "-"
                })
            sb.append("\n")
        sb.append("   012345678901234")
        sb.toString
end Board


