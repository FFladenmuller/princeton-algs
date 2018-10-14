//*****************************************************************************
// Author: Frederic Fladenmuller
// Class: Princeton Algorithms, part 1
// Date: 10/14/18
//
// Finds all line segments with 4 or more collinear points via brute force
// check of all possible sets of 4.
//*****************************************************************************


import java.util.ArrayList;

public class BruteCollinearPoints {
    private int totalSegments;
    private ArrayList<endPointPair> pairs;
    private final Point[] pointsCpy;
    private final LineSegment[] segmentsFinal;

    public BruteCollinearPoints(Point[] points) {
        // finds all line segments containing 4 points

        // check for null array, null points, repeated points

        if (points == null)
            throw new IllegalArgumentException("Null points array.");


        int n = points.length;
        pointsCpy = new Point[n];
        for (int i = 0; i < n; i++) {
            pointsCpy[i] = points[i];
        }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("Null point in points.");
            }
        }

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                if (points[j].compareTo(points[i]) == 0 && j != i)
                    throw new IllegalArgumentException("Repeated point in array.");
            }
        }

        pairs = new ArrayList<>();
        totalSegments = 0;

        // Iterate through points, checking each possible set of 4
        for (int p = 0; p < pointsCpy.length; p++) {
            for (int q = p + 1; q < pointsCpy.length; q++) {
                for (int r = q + 1; r < pointsCpy.length; r++) {
                    for (int s = r + 1; s < pointsCpy.length; s++) {
                        if (pointsCpy[p].slopeTo(pointsCpy[q]) ==
                                pointsCpy[p].slopeTo(pointsCpy[r]) &&
                                pointsCpy[p].slopeTo(pointsCpy[r]) ==
                                        pointsCpy[p].slopeTo(pointsCpy[s])) {


                            // Find highest, lowest y values between these 4 points,
                            // these are endpoints
                            Point[] lineSegment = new Point[]
                                    {pointsCpy[p], pointsCpy[q], pointsCpy[r], pointsCpy[s]};
                            Point max = pointsCpy[p];
                            Point min = pointsCpy[p];
                            for (int i = 0; i < lineSegment.length - 1; i++) {
                                if (max.compareTo(lineSegment[i + 1]) < 0) {
                                    max = lineSegment[i + 1];
                                } else if (min.compareTo(lineSegment[i + 1]) > 0) {
                                    min = lineSegment[i + 1];
                                }
                            }

                            // If this line segment's slopeTo another line segment's max and min are same, part of
                            // that line segment as well
                            boolean partOfExistingSegment = false;
                            for (int i = 0; i < pairs.size(); i++) {
                                Point endPointMax = pairs.get(i).max;
                                Point endPointMin = pairs.get(i).min;
                                if (min.slopeTo(endPointMax) ==
                                        endPointMax.slopeTo(endPointMin) && max.slopeTo(endPointMin) == endPointMin.slopeTo(endPointMax)) {
                                    partOfExistingSegment = true;
                                    if (max.compareTo(endPointMax) > 0)
                                        pairs.get(i).max = max;
                                    if (min.compareTo(endPointMin) < 0)
                                        pairs.get(i).min = min;
                                }
                            }
                            if (!partOfExistingSegment) {
                                pairs.add(new endPointPair(min, max));
                                totalSegments++;
                            }
                        }

                    }
                }
            }
        }
        segmentsFinal = new LineSegment[totalSegments];
        for (int i = 0; i < totalSegments; i++) {
            segmentsFinal[i] = new LineSegment(pairs.get(i).min,
                    pairs.get(i).max);
        }
    }

    public int numberOfSegments() {
        // the number of line segments
        return totalSegments;
    }

    public LineSegment[] segments() {
        LineSegment[] segCopy = new LineSegment[segmentsFinal.length];
        for (int i = 0; i < segmentsFinal.length; i++) {
            segCopy[i] = segmentsFinal[i];
        }
        return segCopy;
    }

    private class endPointPair {
        private Point min;
        private Point max;

        public endPointPair(Point min, Point max) {
            this.min = min;
            this.max = max;
        }

    }
}
