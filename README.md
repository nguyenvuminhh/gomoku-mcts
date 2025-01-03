# Gomoku AI with Monte Carlo Tree Search Algorithm

## General Description
A Gomoku (five in a row in a 15x15 board) implementation using Monte Carlo Tree Search (MCTS) AI.

Link To .jar File: [out/artifacts/gmk_scala_jar](https://github.com/nguyenvuminhh/gomoku-mcts/tree/master/out/artifacts/gmk_scala_jar)

## Features
  - **Configurable settings**: Who goes first, number of expansions, number of simulations per expansion.
  - **Parallelism**: The expansions and simulations are done parallelly.
  - **Concurrency**: The tree expansion process (select->expand->simulate->backpropagate) are done on a separate thread.

## AI Implementation
  - **Select**: Recursively select node with best UCB1 (Upper Confidence Bound) until meeting either a terminal node or a non-fully-expanded node.
  - **Expand**: Add a new successor of the selected node to its children.
  - **Simulation**: Perform *n* simulations on the newly expanded node, until reaching terminal state.
  - **Update/Backpropagation**: Update the simulation result of simulation upward until reaching root node.
  - **Best move selection metric**: Node with a guaranteed loss have metric of *-Inf*. Others' metrics are their visit count
  - **Best move selection**: Sort children in descending order according to their metrics. Iterate throught the sorted children, expand them to the depth of 3.
    If the child does not lead to any guaranteed loss, return that child. If no such child is found, it defaults to the best option based on the sorted list or
    returns ((-1, -1), null) if no valid move exists.
  - **Updating tree**: If the new node is one of the old node's children, replace the root node with the new node (reuse tree). Otherwise create a new one.
  
## Components
  - **Board.scala**: Manage game state and move validation
  - **Game.scala**: Control game flow
  - **MCTSNode.scala**: Representing node in MCTS tree, provide core tree method (select, expand, simulate, backpropagate)
  - **MCTSAgent.scala**: An agent that is in charge of interacting with MCTS tree and provide best move to the game
  - **MainGUI.scala**: Handles the GUI

## Performance and Next Steps
  - AI thinking time scales with the number of simulation required. Recommendation: 9000 expansion with 1-2 simulations/expansion.
  - Currently the bot can only think about 4 steps ahead. The next step is to enable learning by, for example, MuZero implementation.

## Some Screenshots
![image](https://github.com/user-attachments/assets/0d692919-cffa-42f1-9c10-9e0e96eb6e83)
![image](https://github.com/user-attachments/assets/591a7500-bf5b-4584-b768-13f6071e752f)

