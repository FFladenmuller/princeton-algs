//*******************************************************************
//  Author: Frederic Fladenmuller
//  Class: Princeton: Algorithms, part 1
//  Date: 9/3/2018
//
//  This program utilizes a weighted quick union UF data structure to
//  solve the problem of percolation. When a site is opened, union()
//  is called on the newly opened site and any of it's neighbor open
//  sites. When there is a path of open sites from top to bottom, we
//  say that the system has percolated. In the class, it is suggested
//  to use a virtual top site and virtual bottom site to efficiently
//  check whether the bottom is connected to the top. However, if
//  using only one union find data structure, this causes backwash.
//  All sites connected to the bottom virtual site will now appear full,
//  or connected to the top even though they have no neighbouring open
//  sites. To solve this issue, I got rid of the idea of virtual sites
//  completely. Instead, I store the state of each site inside of a byte
//  array. 0 signifies a closed site, 01 an open site, 011 an open site
//  connected to the top, and 0101 an open site connected to the bottom.
//  A site with the status 0111 would signify that it is connected to both
//  the bottom and the top, and thus the system would be percolated.
//  So before a site is unioned with adjacent site, I save the adjacent
//  site's root site status. Then after all the union's, I merge the
//  neighbor site statuses into the newly open site root status.
//*******************************************************************


import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF percolationSystem;
    private int openSites;
    private final int height;
    private final int width;
    private boolean percolated;
    private final byte openSite = 0x1;
    private final byte connectedToTop = 0x2;
    private final byte connectedToBottom = 0x4;
    private byte siteStates[];

    public Percolation(int n) {
        // create n-by-n grid, with all sites blocked
        if (n <= 0 ) throw new IllegalArgumentException("n must be greater than 0.");
        percolationSystem = new WeightedQuickUnionUF(n * n);
        width = n; height = n;
        siteStates = new byte[n * n];
    }
    public void open(int row, int col) {
        // open site (row, col) if it is not open already
        if(isOpen(row, col))
            return;
        validateRowColumnIndices(row, col);
        siteStates[xyTo1d(row,col)] = openSite;
        openSites++;

        if (row == 1)
            siteStates[xyTo1d(row, col)] = openSite | connectedToTop;
        else if (row == height)
            siteStates[xyTo1d(row, col)] = openSite | connectedToBottom;

        unionAdjacentOpenSite(row, col);
    }

    public boolean isOpen(int row, int col) {
        validateRowColumnIndices(row, col);
        return((openSite & siteStates[xyTo1d(row, col)]) == 0x1);
    }

    public boolean isFull(int row, int col) {
        // Make sure the site's root state is connected to top
        validateRowColumnIndices(row, col);
        int siteRoot = percolationSystem.find(xyTo1d(row, col));
        return((connectedToTop & siteStates[siteRoot]) == 0x2);
    }

    public int numberOfOpenSites() {
        return openSites;
    }

    public boolean percolates() {
        return percolated;
    }

    private int xyTo1d(int row, int col) {
        // Convert Grid range [1 -> N, 1 -> N] into 1d [0 -> N -1]
        return (width * (row - 1)) + col - 1;
    }

    private void validateRowColumnIndices(int row, int col) {
        if (row <= 0 || row > height) {
            throw new IllegalArgumentException("row index out of bounds: " + row);
        }
        else if (col <= 0 || col > width) {
            throw new IllegalArgumentException("col index out of bounds: " + col);
        }
    }

    private void unionAdjacentOpenSite(int row, int col) {
        // If top, left, bottom, right is in bounds and is open, union that site with given site
        // Get the state of top, bottom, left, right's roots
        byte topState = 0;
        byte leftState = 0;
        byte bottomState = 0;
        byte rightState = 0;

        if (row - 1 > 0 && isOpen(row - 1, col)) {
            topState = siteStates[percolationSystem.find(xyTo1d(row - 1, col))];
            percolationSystem.union(xyTo1d(row - 1, col), xyTo1d(row, col));
        }
        if (col - 1 > 0 && isOpen(row, col - 1)) {
            leftState = siteStates[percolationSystem.find(xyTo1d(row, col - 1))];
            percolationSystem.union(xyTo1d(row, col - 1), xyTo1d(row, col));
        }
        if (row + 1 <= height && isOpen(row + 1, col)) {
            bottomState = siteStates[percolationSystem.find(xyTo1d(row + 1, col))];
            percolationSystem.union(xyTo1d(row + 1, col), xyTo1d(row, col));
        }
        if (col + 1 <= width && isOpen(row, col + 1)) {
            rightState = siteStates[percolationSystem.find(xyTo1d(row, col + 1))];
            percolationSystem.union(xyTo1d(row, col + 1), xyTo1d(row, col));
        }
        int newRoot = percolationSystem.find(xyTo1d(row, col));
        int openedSite = xyTo1d(row, col);

        mergeSiteStates(topState, leftState, bottomState, rightState, openedSite, newRoot);
    }

    private void mergeSiteStates(byte topState, byte leftState,byte bottomState,
                                 byte rightState,int openedSite, int newRoot)
    {
        // Merge the states of roots of each neighbor of newly opened site
        byte mergeState = (byte)(siteStates[openedSite] | topState);
        mergeState = (byte)(mergeState | leftState);
        mergeState = (byte)(mergeState | bottomState);
        mergeState = (byte)(mergeState | rightState);

        siteStates[newRoot] = mergeState;
        if ((siteStates[newRoot] & 0x7) == 0x7 )
            percolated = true;
    }
}
