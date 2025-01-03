package logic

/**
 * Object GameSettings to hold game settings
 */
object GameSettings extends Serializable:
  /**
   * Board size
   */
  var SIZE = 15

  /**
   * A variable indicate if AI goes first
   */
  var AIFIRST = false

  /**
   * Minimum number of simulation to be made before giving out decision
   */
  var MCTSTHRESHOLD = 15000

  /**
   * Number of simulation per expanded node
   */
  var MCTSSIMULATIONCOUNT = 3

  /**
   * Print out loud or not
   */
  private val VERBOSE: Boolean = true

  /**
   * 2 variables to indicate whether to import/export node
   */
  val IMPORTNODE = false
  val EXPORTNODE = false

  /**
   * A method used to set difficulties
   */
  def setDifficulties(level: Int): Unit =
    MCTSTHRESHOLD = 3000*level*MCTSSIMULATIONCOUNT

  /**
   * A print method for logging
   *
   * @param message message to be printed
   */
  def printCmd(message: String): Unit = if VERBOSE then println(message)
