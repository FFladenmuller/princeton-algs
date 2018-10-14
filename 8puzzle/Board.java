//*****************************************************************************
// Author: Frederic Fladenmuller
// Class: Princeton Algorithms, part 1
// Date: 10/14/18
//
// This class implements a board for the game 8/15 puzzle, various methods
// to check the state of the board and it's proximity to the goal board,
// and the possible moves from this board.
//*****************************************************************************


import java.util.Stack;

public class Board {
    private char[] blocks;
    private int n;
    private int digitsInN;
    private int manhattan;

    // construct a board from an n-by-n array of blocks'
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {

        if (blocks == null) {
            throw new IllegalArgumentException("Null array input.");
        }

        n = blocks[0].length;
        this.blocks = new char[n * n];

        // create defensive, immutable copy of blocks
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.blocks[xyTo1d(i, j)] = (char) blocks[i][j];
            }
        }

        digitsInN = 1;
        int tmp = n * n - 1;
        while (tmp / 10 > 0) {
            tmp /= 10;
            digitsInN++;
        }
        manhattan = Integer.MAX_VALUE;
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of blocks out of place
    public int hamming() {
        int outOfPlace = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // check if element x contains element x + 1
                // (board starts at 1, board[1] should contain[1]
                int xy = xyTo1d(i, j);
                if (blocks[xy] != xy + 1
                        && blocks[xy] != 0) {
                    outOfPlace++;
                }
            }
        }
        return outOfPlace;
    }

    // The sum of the Manhattan distances (sum of the vertical
    // and horizontal distance) from the blocks to their goal positions
    public int manhattan() {
        if (manhattan == Integer.MAX_VALUE) {
            calcManhattan();
        }
        return this.manhattan;
    }

    private void calcManhattan() {
        int manhattan = 0;
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == 0) {
                continue;
            }
            manhattan += distanceToGoalPos(i, blocks[i]);
        }
        this.manhattan = manhattan;
    }

    // converts two dimensional array element to one dimensional element
    private int xyTo1d(int i, int j) {
        int oneD = j + n * i;
        return oneD;
    }

    // calculates distance from an element at pos1d to it's goal position
    // based on it's number.
    private int distanceToGoalPos(int pos1d, int numberAtPos) {
        // calculate row and column of blocks[i]
        int currentColumn = pos1d % n;
        int currentRow = pos1d / n;

        // calculate goal row and column of element at blocks[i]
        int goalColumn = (numberAtPos % n == 0) ? n - 1 : numberAtPos % n - 1;
        int goalRow = (numberAtPos - 1) / n;

        return Math.abs(goalColumn - currentColumn)
                + Math.abs(goalRow - currentRow);
    }


    private Board constructBlocks(char[] blocks) {
        int[][] copyBlocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                copyBlocks[i][j] = blocks[xyTo1d(i, j)];
            }
        }

        return new Board(copyBlocks);
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (this.manhattan == Integer.MAX_VALUE) {
            calcManhattan();
        }
        return (this.manhattan == 0);
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        // create twin
        Board twin = constructBlocks(blocks);

        for (int i = 0; i < blocks.length; i++) {
            if (twin.blocks[i] != 0 && twin.blocks[i + 1] != 0) {
                char tmp = twin.blocks[i];
                twin.blocks[i] = twin.blocks[i + 1];
                twin.blocks[i + 1] = tmp;
                break;
            }
        }

        return twin;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board other = (Board) y;
        if (other.n != this.n) return false;
        for (int i = 0; i < blocks.length; i++) {
            if (this.blocks[i] != other.blocks[i]) return false;
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> neighborBoards = new Stack<>();
        int emptySpacePos = 0;

        // find position of empty space
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == 0) emptySpacePos = i;
        }

        // add board where left, right, top, bottom of empty space
        // swap with empty space. Watch for corner cases

        // left
        if (emptySpacePos % n != 0) {
            Board leftNeighbor = createNeighbor(emptySpacePos, emptySpacePos - 1);
            neighborBoards.push(leftNeighbor);
        }

        // right
        if (emptySpacePos % n != n - 1) {
            Board rightNeighbor = createNeighbor(emptySpacePos, emptySpacePos + 1);
            neighborBoards.push(rightNeighbor);
        }

        // top
        if (emptySpacePos > n - 1) {
            Board topNeighbor = createNeighbor(emptySpacePos, emptySpacePos - n);
            neighborBoards.push(topNeighbor);
        }

        // bottom
        if (emptySpacePos < (blocks.length - n)) {
            Board bottomNeighbor = createNeighbor(emptySpacePos, emptySpacePos + n);
            neighborBoards.push(bottomNeighbor);
        }
        return neighborBoards;
    }

    // swaps empty space position with the element at swap position
    private Board createNeighbor(int emptySpacePos, int swapPosition) {
        // Swap 0 and element at swap position
        char tmp = blocks[swapPosition];
        blocks[swapPosition] = blocks[emptySpacePos];
        blocks[emptySpacePos] = tmp;
        Board neighbor = this.constructBlocks(blocks);

        // if the swapped element's new location is closer to it's goal
        // position, subtract 1 from manhattan. else add 1. Caching and
        // adding +- 1 should save N^2 iteration each manhattan calculation.
        if (distanceToGoalPos(emptySpacePos, blocks[emptySpacePos]) <
                distanceToGoalPos(swapPosition, blocks[emptySpacePos])) {
            neighbor.manhattan = this.manhattan - 1;
        } else {
            neighbor.manhattan = this.manhattan + 1;
        }

        blocks[emptySpacePos] = blocks[swapPosition];
        blocks[swapPosition] = tmp;
        return neighbor;
    }


    // string representation of board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");

        for (int i = 0; i < blocks.length; i += n) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%" + (digitsInN + 1) + "d", (int) blocks[i + j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

}
