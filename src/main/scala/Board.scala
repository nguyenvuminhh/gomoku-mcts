import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer

import Constants.SIZE
import Constants.{CLAMP_DIST, SIZE}


class Board (
    val matrix: Array[Array[Byte]] = Array.fill(SIZE, SIZE)(0),
    ):
    var step: Int = 0

    var winner: Byte = 2

    override def hashCode(): Int = matrix.map(_.toSeq).toSeq.hashCode()

    override def clone(): Board = new Board(matrix.map(_.clone()))

    def isOccupied(x: Int, y: Int): Boolean = matrix(y)(x) != 0

    def isValidCoord(x: Int, y: Int) = (0 <= x && x < SIZE && 0 <= y && y < SIZE)

    def placeStone(x: Int, y: Int, value: Byte): (Int, Boolean) =
        require(math.abs(value) == 1, "Value must be 1 or -1")
        require(isValidCoord(x, y), "Coord must be valid")
        require(!isOccupied(x, y), "Cell must be empty")
        step += 1
        matrix(y)(x) = value
        if step == SIZE * SIZE then
            return (0, true)

        if checkTerminal(x, y, value) then
            return (value, true)

        (0, false)

    def removeStone(x: Int, y: Int) =
        require(isValidCoord(x, y), "Coord must be valid")
        require(isOccupied(x, y), "Cell must not be empty")
        step -= 1
        matrix(y)(x) = 0
        winner = 2


    private def checkTerminal(x: Int, y: Int, player: Int): Boolean =
        def countStones(dx: Int, dy: Int): Int =
            var count = 1
            var done = false
            while !done do
                for (d <- 1 to 4)
                    val nx = x + d * dx
                    val ny = y + d * dy
                    if !isValidCoord(nx, ny) || matrix(ny)(nx) != player then
                        done = true
                    else
                        count += 1
                done = true
            count

        val directions = Seq((1, 0), (0, 1), (1, 1), (1, -1)) // Horizontal, vertical, diagonal, anti-diagonal
        return directions.exists { (dx, dy) => countStones(dx, dy) + countStones(-dx, -dy) - 1 >= 5 }

    def generateMoves(player: Byte, sorted: Boolean): Vector[(Byte, Byte)] =
        val moveList = ArrayBuffer[(Byte, Byte)]()

        for
            i <- 0 until SIZE
            j <- 0 until SIZE
        do
            if !isOccupied(i, j) then // Skip non-empty cells.
                val hasStoneNearby = (-1 to 1).exists { di =>
                    (-1 to 1).exists { dj =>
                        val ni = i + di
                        val nj = j + dj
                        isValidCoord(ni, nj) && isOccupied(ni, nj)
                        }
                    }
                    if hasStoneNearby then
                        val move = (i.toByte, j.toByte)
                        moveList += move

        moveList.toVector

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


