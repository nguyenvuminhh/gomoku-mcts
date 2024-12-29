package dump

//package dump
//
//class TrieNode:
//    val children: scala.collection.mutable.Map[Int, TrieNode] = scala.collection.mutable.Map()
//    var isLeaf: Boolean = false
//    val value: scala.collection.mutable.Map[Int, Double] = scala.collection.mutable.Map()
//
//class Trie:
//    val root: TrieNode = TrieNode()
//
//    def insert(pattern: (Vector[Byte], Vector[Byte], Vector[Byte], Int, Int)): Unit =
//        var current = root
//        val (patternArray, cells1, cells2, val1, val2) = pattern
//        for char <- patternArray do
//            if !current.children.contains(char) then
//                current.children(char) = TrieNode()
//            current = current.children(char)
//        current.isLeaf = true
//        for cell <- cells1 do
//            current.value(cell) = val1
//        for cell <- cells2 do
//            current.value(cell) = val2
//
//    def display(node: TrieNode = root, prefix: String = ""): Unit =
//        for (char, child) <- node.children do
//            if child.isLeaf then
//                println(s"$prefix$char -> val: ${child.value}")
//            display(child, prefix + s"$char ")

//  val CLAMP_DIST = 3
//  val WON = 100_000_000
//  val ABSOLUTE_WIN = 10_000_000
//  val UNBLOCKABLE_WIN = 1_000_000
//  val BLOCKABLE_WIN = 100_000
//  val STRGHT_THREE = 10_000
//  val BLK_THREE = 1_000
//  val DEPTH = 4
//
//  val PATTERNS: Vector[(Vector[Byte], Vector[Byte], Vector[Byte], Int, Int)] = Vector(
//    (Vector(-1, 1, 1, 1, 1, 0), Vector(5), Vector(), ABSOLUTE_WIN, 0),
//    (Vector(0, 1, 1, 1, 1, -1), Vector(0), Vector(), ABSOLUTE_WIN, 0),
//    (Vector(0, 1, 1, 1, 1, 0), Vector(0, 5), Vector(), ABSOLUTE_WIN, 0),
//    (Vector(1, 1, 0, 1, 1), Vector(2), Vector(), ABSOLUTE_WIN, 0),
//    (Vector(1, 0, 1, 1, 1), Vector(1), Vector(), ABSOLUTE_WIN, 0),
//    (Vector(1, 1, 1, 0, 1), Vector(3), Vector(), ABSOLUTE_WIN, 0),
//
//    (Vector(0, 1, 1, 1, 0), Vector(0, 4), Vector(), UNBLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(0, 1, 1, 0, 1, 0), Vector(3), Vector(0, 5), UNBLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(0, 1, 0, 1, 1, 0), Vector(2), Vector(0, 5), UNBLOCKABLE_WIN, BLOCKABLE_WIN),
//
//    (Vector(-1, 1, 1, 1, 0, 0), Vector(4), Vector(5), BLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(0, 0, 1, 1, 1, -1), Vector(1), Vector(0), BLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(-1, 1, 1, 0, 1, 0), Vector(3), Vector(5), BLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(0, 1, 0, 1, 1, -1), Vector(2), Vector(0), BLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(-1, 1, 0, 1, 1, 0), Vector(2), Vector(5), BLOCKABLE_WIN, BLOCKABLE_WIN),
//    (Vector(0, 1, 1, 0, 1, -1), Vector(3), Vector(0), BLOCKABLE_WIN, BLOCKABLE_WIN),
//
//    (Vector(0, 0, 1, 1, 0, 0), Vector(1, 4), Vector(0, 5), STRGHT_THREE, BLK_THREE),
//    (Vector(-1, 0, 1, 1, 0, 0), Vector(1, 4), Vector(5), STRGHT_THREE, BLK_THREE),
//    (Vector(0, 0, 1, 1, 0, -1), Vector(1), Vector(0, 4), STRGHT_THREE, BLK_THREE),
//    (Vector(0, 1, 0, 1, 0), Vector(2), Vector(0, 4), STRGHT_THREE, BLK_THREE),
//    (Vector(0, 1, 0, 0, 1), Vector(), Vector(2, 3), STRGHT_THREE, BLK_THREE),
//    (Vector(0, 1, 0, 0, 1), Vector(), Vector(2, 3), STRGHT_THREE, BLK_THREE),
//
//    (Vector(-1, 1, 1, 0, 0), Vector(3, 4), Vector(), BLK_THREE, 0),
//    (Vector(0, 0, 1, 1, -1), Vector(0, 1), Vector(), BLK_THREE, 0),
//    (Vector(-1, 1, 0, 1, 0), Vector(2, 4), Vector(), BLK_THREE, 0),
//    (Vector(0, 1, 0, 1, -1), Vector(0, 2), Vector(), BLK_THREE, 0)
//  )
//
//
//private def handlePlayerMove(x: Int, y: Int): Boolean =
//    if (finished || !isPlayersTurn) return false
//
//    isPlayersTurn = false
//
//    if !playMove(x, y, -1) then
//        isPlayersTurn = true
//        return false
//    val (bestX, bestY) = agent.updateOpponentMoveAndGetBestMove((x, y))
//
//    if bestX == -1 then
//        printCmd("No possible moves left. Game Over.")
//        finished = true
//        return true
//
//    if !playMove(bestX, bestY, 1) then
//        throw new IllegalArgumentException(s"AI made illegal move: $bestX $bestY")
//
//    isPlayersTurn = true
//    true

//def startCLI(): Unit =
//    printCmd("Welcome to Gomoku!")
//    printCmd("Enter moves in format 'x y' (e.g., '7 7')")
//    printCmd("Board coordinates are 0-14")
//    while !finished do
//
//        printCmd("\nCurrent board:")
//        printCmd(mainBoard.toString)
//        println(isPlayersTurn.toString)
//
//        if isPlayersTurn then
//            var done = false
//            while !done do
//                try {
//                    val move = readLine("Your move (x y): ").split(" ").map(_.toInt)
//                    val x = move(0)
//                    val y = move(1)
//                    if handlePlayerMove(x, y) then done = true
//                    else printCmd("Invalid move, try again!")
//                } catch
//                    case exception: Exception => throw exception
//
//
//    printCmd("\nFinal board:")
//    printCmd(mainBoard.toString)
