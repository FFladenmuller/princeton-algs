//*****************************************************************************
// Author: Frederic Fladenmuller
// Class: Princeton Algorithms, part 1
// Date: 10/14/18
//
// Finds all line segments with 4 or more collinear points by sorting points first
// by natural order, then sorting all points by their respective slopes to i, where
// i is an incrementing pointer to the points array. I deal with line segments by
// keeping the status of min point and telling whether it is already in a vertical
// or horizontal line segment in a byte array. I also use a hashtable to store
// lines that the min point is already in.
//*****************************************************************************


import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private int totalSegments;
    private final ArrayList<LineSegment> segments;
    private final LineSegment[] segmentsFinal;
    private byte[] pointInLineStatus;
    private Node[] connections;
    private final Point[] pointsCpy;

    private class Node {
        public Point connection;
        public Node next;
    }

    public FastCollinearPoints(Point[] points) {
        // finds all line segments containing 4 or more points

        if (points == null)
            throw new IllegalArgumentException("Null points array.");

        totalSegments = 0;
        int n = points.length;
        pointsCpy = new Point[n];


        // check for repeated point and null point
        for (int i = 0; i < pointsCpy.length; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException("Null point in points array");
        }

        // copy points to new immutable array
        for (int i = 0; i < n; i++) {
            pointsCpy[i] = points[i];
        }

        // sort points by natural order
        Arrays.sort(pointsCpy);

        for (int i = 0; i < pointsCpy.length - 1; i++) {
            if (pointsCpy[i].compareTo(pointsCpy[i + 1]) == 0) {
                throw new IllegalArgumentException("Repeated point in points array.");
            }
        }


        connections = new Node[points.length];
        pointInLineStatus = new byte[points.length];
        double[] slopeToP = new double[points.length];
        segments = new ArrayList<>();

        // check for repeated point and null point
        for (int i = 0; i < pointsCpy.length - 1; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("Null point in points array");
            } else if (points[i].compareTo(points[i + 1]) == 0) {
                throw new IllegalArgumentException("Repeated point in points array.");
            }
        }
        if (points[points.length - 1] == null) {
            throw new IllegalArgumentException("Null point in points array");
        }

        Point[] pointsClone = pointsCpy.clone();

        for (int i = 0; i < pointsCpy.length; i++) {
            // sort array in order of  slope to points[i]
            Point p = pointsClone[i];
            Arrays.sort(pointsCpy, p.slopeOrder());
            int pointsInSegment = 0;
            slopeToP[0] = pointsCpy[0].slopeTo(p);

            for (int j = 1; j < points.length; j++) {
                // If left point slope to p equals this point slope to p
                slopeToP[j] = pointsCpy[j].slopeTo(p);
                if (slopeToP[j] == slopeToP[j - 1]) {
                    pointsInSegment++;
                } else {
                    if (pointsInSegment >= 2) {
                        Arrays.sort(pointsCpy, (j - 1) - pointsInSegment, j);
                        //Add lineseg
                        Point min = (p.compareTo(pointsCpy[(j - 1) - pointsInSegment]) < 0)
                                ? p : pointsCpy[j - 1 - pointsInSegment];
                        Point max = (p.compareTo(pointsCpy[j - 1]) > 0)
                                ? p : pointsCpy[j - 1];
                        addSegment(Arrays.binarySearch(pointsClone, min), max,
                                slopeToP[j - 1], min, max);
                    }
                    pointsInSegment = 0;
                }
            }
            if (pointsInSegment >= 2) {
                int j = pointsCpy.length - 1;
                Arrays.sort(pointsCpy, j - pointsInSegment, pointsCpy.length);
                Point min = (p.compareTo(pointsCpy[j - pointsInSegment]) < 0)
                        ? p : pointsCpy[j - pointsInSegment];

                Point max = (p.compareTo(pointsCpy[j]) > 0) ? p : pointsCpy[j];
                addSegment(Arrays.binarySearch(pointsClone, min), max,
                        slopeToP[j], min, max);
            }

        }
        n = segments.size();
        segmentsFinal = new LineSegment[n];
        for (int i = 0; i < n; i++) {
            segmentsFinal[i] = segments.get(i);
        }

    }

    private void addSegment(int minOriginalPosition, Point maxPoint, double slope, Point min, Point max) {
        // 01   indicates vertical line with point p
        // 010  indicates horizontal line with point p
        // 0100 indicates regular line with point p

        // if vertical line status stored in byte array at p and slope is vertical,
        // discard (duplicate)
        if ((pointInLineStatus[minOriginalPosition] & 1) == 1 && slope == Double.POSITIVE_INFINITY) return;

            // else if horizontal line status stored in byte array at p and slope is horizontal,
            // discard (duplicate)
        else if ((pointInLineStatus[minOriginalPosition] & 2) == 2 && slope == 0.0) return;

        //check if connected to max already by regular line
        Node ptr = connections[minOriginalPosition];
        while (ptr != null) {
            if (ptr.connection == max)
                return;
            else {
                ptr = ptr.next;
            }
        }
        segments.add(new LineSegment(min, max));
        totalSegments++;
        Node newNode = new Node();
        newNode.connection = maxPoint;
        newNode.next = connections[minOriginalPosition];
        connections[minOriginalPosition] = newNode;

        if (slope == Double.POSITIVE_INFINITY)
            pointInLineStatus[minOriginalPosition] = (byte) (pointInLineStatus[minOriginalPosition] | 1);

        else if (slope == 0.0)
            pointInLineStatus[minOriginalPosition] = (byte) (pointInLineStatus[minOriginalPosition] | 2);
        else
            pointInLineStatus[minOriginalPosition] = (byte) (pointInLineStatus[minOriginalPosition] | 4);
    }


    public int numberOfSegments() {
        return totalSegments;
    }

    public LineSegment[] segments() {
        LineSegment[] segCopy = new LineSegment[segmentsFinal.length];
        for (int i = 0; i < segmentsFinal.length; i++) {
            segCopy[i] = segmentsFinal[i];
        }
        return segCopy;
    }
}
