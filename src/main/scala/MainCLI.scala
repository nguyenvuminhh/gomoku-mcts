import java.time.Instant
import java.time.Duration

object MainCLI:
    def main(args: Array[String]): Unit =
        val game = new Game(VERBOSE = true)
        game.startCLI()

