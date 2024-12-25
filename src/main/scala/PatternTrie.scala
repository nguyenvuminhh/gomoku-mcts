import Constants.{ABSOLUTE_WIN, UNBLOCKABLE_WIN, BLOCKABLE_WIN, STRGHT_THREE, BLK_THREE}

class TrieNode:
    val children: scala.collection.mutable.Map[Int, TrieNode] = scala.collection.mutable.Map()
    var isLeaf: Boolean = false
    val value: scala.collection.mutable.Map[Int, Double] = scala.collection.mutable.Map()

class Trie:
    val root: TrieNode = TrieNode()

    def insert(pattern: (Vector[Byte], Vector[Byte], Vector[Byte], Int, Int)): Unit =
        var current = root
        val (patternArray, cells1, cells2, val1, val2) = pattern
        for char <- patternArray do
            if !current.children.contains(char) then
                current.children(char) = TrieNode()
            current = current.children(char)
        current.isLeaf = true
        for cell <- cells1 do
            current.value(cell) = val1
        for cell <- cells2 do
            current.value(cell) = val2

    def display(node: TrieNode = root, prefix: String = ""): Unit =
        for (char, child) <- node.children do
            if child.isLeaf then
                println(s"$prefix$char -> val: ${child.value}")
            display(child, prefix + s"$char ")
