//*****************************************************************************
// Author: Frederic Fladenmuller
// Class: Princeton Algorithms, part 1
// Date: 10/14/18
//
// This class implements a method for solving the 8/15 puzzle problem,
// using the A* algorithm.
//*****************************************************************************

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private Stack<Board> boardSequence;
    private boolean solvable;
    private int moves;

    private class searchNode implements Comparable<searchNode> {
        Board board;
        int priority;
        int manhattan;
        searchNode last;
        int moves;

        searchNode(Board board, int movesSoFar, searchNode last) {
            this.board = board;
            this.moves = movesSoFar;
            this.priority = movesSoFar + board.manhattan();
            this.last = last;
        }

        public int compareTo(searchNode that) {
            if (this.priority > that.priority) return 1;
            else if (that.priority > this.priority) return -1;
            else if (this.manhattan < that.manhattan) return -1;
            else if (this.manhattan > that.manhattan) return 1;
            else {
                return 0;
            }
        }

    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {

        if (initial == null) {
            throw new IllegalArgumentException("Null board input.");
        }
        int moves = 0;
        solvable = false;

        boardSequence = new Stack<>();
        // initialize search for init and twin
        searchNode initFirst = new searchNode(initial, 0, null);
        searchNode twinFirst = new searchNode(initial.twin(), 0, null);

        // create game trees
        MinPQ<searchNode> initGameTree = new MinPQ<>();
        MinPQ<searchNode> twinGameTree = new MinPQ<>();
        initGameTree.insert(initFirst);
        twinGameTree.insert(twinFirst);

        searchNode initMin = initGameTree.delMin();
        searchNode twinMin = twinGameTree.delMin();

        while (!initMin.board.isGoal() && !twinMin.board.isGoal()) {
            // add neighbors for init
            treeUpdateNeighbors(initMin, initGameTree, initMin.moves + 1);

            // add neighbors for twin
            treeUpdateNeighbors(twinMin, twinGameTree, twinMin.moves + 1);

            // update min node for init
            initMin = initGameTree.delMin();

            // update min node for twin
            twinMin = twinGameTree.delMin();
        }

        if (initMin.board.isGoal())
            solvable = true;

        // reconstruct solution
        boardSequence.push(initMin.board);
        for (searchNode ptr = initMin; ptr.last != null; ptr = ptr.last) {
            if (ptr.last != null) {
                moves++;
            }
            boardSequence.push(ptr.last.board);
        }
        this.moves = moves;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solvable;
    }

    // add neighbors of a search node's board to a game tree(MinPQ)
    private void treeUpdateNeighbors(searchNode minNode,
                                     MinPQ<searchNode> gameTree,
                                     int solvableMoves) {
        Iterable<Board> neighbors = minNode.board.neighbors();
        for (Board neighbor : neighbors) {
            searchNode node = new searchNode(neighbor, solvableMoves, minNode);
            if (minNode.last != null) {
                if (!node.board.equals(minNode.last.board))
                    gameTree.insert(node);
            } else {
                gameTree.insert(node);
            }
        }
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!solvable) {
            return -1;
        }
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!solvable) {
            return null;
        }
        return boardSequence;
    }


    // solve a slider puzzle
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

    }
}
