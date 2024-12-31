/**
 * Object Constants to hold game settings
 */
object Constants extends Serializable:
  /**
   * Board size
   */
  var SIZE = 15

  /**
   * Minimum number of simulation to be made before giving out decision
   */
  var MCTSTHRESHOLD = 15000

  /**
   * Number of simulation per expanded node
   */
  var MCTSSIMULATIONCOUNT = 1

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
   *
   */
  def setDifficulties(level: Int): Unit =
    if level == 1 then
      MCTSTHRESHOLD = 2500
      MCTSSIMULATIONCOUNT = 1
    else if level == 2 then
      MCTSTHRESHOLD = 5000
      MCTSSIMULATIONCOUNT = 1
    else if level == 3 then
      MCTSTHRESHOLD = 10000
      MCTSSIMULATIONCOUNT = 1
    else if level == 4 then
      MCTSTHRESHOLD = 15000
      MCTSSIMULATIONCOUNT = 1
    else if level == 5 then
      MCTSTHRESHOLD = 20000
      MCTSSIMULATIONCOUNT = 3

  /**
   * A print method for logging
   *
   * @param message message to be printed
   */
  def printCmd(message: String): Unit = if VERBOSE then println(message)
