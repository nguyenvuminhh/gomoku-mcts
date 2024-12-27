object Constants:
  val SIZE = 15
  val CLAMP_DIST = 3
  val WON = 100_000_000
  val ABSOLUTE_WIN = 10_000_000
  val UNBLOCKABLE_WIN = 1_000_000
  val BLOCKABLE_WIN = 100_000
  val STRGHT_THREE = 10_000
  val BLK_THREE = 1_000
  val MCTSTHRESHOLD = 20000
  val MCTSSIMULATIONCOUNT = 3
  val DEPTH = 4
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


