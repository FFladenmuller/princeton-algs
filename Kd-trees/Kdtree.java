import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {
    private int size;
    private Node root;
    private Point2D champion;
    private double championDistance;

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private static class Node {
        private Point2D p;      // the point
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean orientation;

        public Node(Point2D p, boolean orientation) {
            this.p = p;
            lb = null;
            rt = null;
            this.orientation = orientation;
        }
    }

    // construct an empty set of points
    public KdTree() {
        size = 0;
        root = null;
        champion = null;
        championDistance = Double.MAX_VALUE;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null argument to insert()");
        if (!contains(p)) {
            root = put(root, p, VERTICAL);
        }
    }

    private Node put(Node t, Point2D p, boolean orientation) {
        if (t == null) {
            size++;
            return new Node(p, orientation);
        }

        int cmp = switchCompare(t, p.x(), p.y());
        orientation = (orientation == VERTICAL) ? HORIZONTAL : VERTICAL;

        if (cmp < 0) {
            t.lb = put(t.lb, p, orientation);
        } else {
            t.rt = put(t.rt, p, orientation);
        }
        return t;
    }

    // compare on x if orientation is vertical (1) or y if horizontal (0)
    private int switchCompare(Node t, double x, double y) {
        if (t.orientation == VERTICAL) {
            if (x > t.p.x()) return 1;
            else if (x < t.p.x()) return -1;
            return 0;
        }
        if (y > t.p.y()) return 1;
        else if (y < t.p.y()) return -1;
        return 0;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null argument to contains()");
        Node ptr = root;
        byte orientation = 1;
        while (ptr != null) {
            int cmp = switchCompare(ptr, p.x(), p.y());
            orientation = (orientation == 1) ? (byte) 0 : (byte) 1;
            if (cmp < 0) {
                ptr = ptr.lb;
            } else {
                if (ptr.p.equals(p)) {
                    return true;
                }
                ptr = ptr.rt;
            }
        }
        return false;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("null argument to range()");
        Queue<Point2D> pointsInRange = new Queue<>();
        findRange(pointsInRange, rect, root);
        return pointsInRange;
    }

    // find the points in the 2d range of query rectangle
    private void findRange(Queue<Point2D> q, RectHV rect, Node t) {
        if (t == null) {
            return;
        }

        if (rect.contains(t.p)) {
            q.enqueue(t.p);
        }
        /* compare x/y min & max depending on orientation.
           if cmpMin & cmpMax > 0, the min and max x/y of
           rect will be greater than point, so it is either
           above or to the right of root. In this case search
           right subtree. If both are less, it is below or to the left,
           so search left subtree. Otherwise, it must root's line,
           so we must search both subtrees.
         */
        int cmpMin = switchCompare(t, rect.xmin(), rect.ymin());
        int cmpMax = switchCompare(t, rect.xmax(), rect.ymax());

        if (cmpMin > 0 && cmpMax > 0) {
            findRange(q, rect, t.rt);
        } else if (cmpMin < 0 && cmpMax < 0) {
            findRange(q, rect, t.lb);
        } else {
            findRange(q, rect, t.rt);
            findRange(q, rect, t.lb);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("null argument to nearest()");
        if (root == null) {
            return null;
        }
        championDistance = Double.MAX_VALUE;
        findNearest(p, root, new RectHV(0.0, 0.0, 1.0, 1.0));
        return champion;
    }

    private RectHV updateRect(boolean orientation, int cmp, RectHV oldRect, Node t) {
        double xmin = oldRect.xmin();
        double xmax = oldRect.xmax();
        double ymin = oldRect.ymin();
        double ymax = oldRect.ymax();

        if (orientation == VERTICAL) {
            if (cmp > 0 || cmp == 0) {
                xmin = t.p.x();
            } else {
                xmax = t.p.x();
            }
        } else {
            if (cmp > 0 || cmp == 0) {
                ymin = t.p.y();
            } else {
                ymax = t.p.y();
            }
        }
        return new RectHV(xmin, ymin, xmax, ymax);
    }

    /*Nearest-neighbor search. To find a closest point to a given query
      point, start at the root and recursively search in both subtrees using
      the following pruning rule: if the closest point discovered so far is closer
      than the distance between the query point and the rectangle corresponding
      to a node, there is no need to explore that node (or its subtrees).
      That is, search a node only only if it might contain a point that is
      closer than the best one found so far. The effectiveness of the pruning rule
      depends on quickly finding a nearby point. \
      Explanation credit @http://coursera.cs.princeton.edu/algs4/assignments/kdtree.html
     */
    private void findNearest(Point2D query, Node t, RectHV rect) {
        if (t == null) {
            return;
        }

        double queryDistance = t.p.distanceSquaredTo(query);
        if (queryDistance < championDistance) {
            champion = t.p;
            championDistance = queryDistance;
        }
        int cmp = switchCompare(t, query.x(), query.y());

        // figure out rect for a point
        RectHV leftRect = updateRect(t.orientation, -1, rect, t);
        RectHV rightRect = updateRect(t.orientation, 1, rect, t);
        if (cmp < 0) {
            if (t.lb != null) {
                findNearest(query, t.lb, leftRect);
            }
            if (t.rt != null && rightRect.distanceSquaredTo(query) <= championDistance)
                findNearest(query, t.rt, rightRect);
        } else {
            if (t.rt != null) {
                findNearest(query, t.rt, rightRect);
            }
            if (t.lb != null && leftRect.distanceSquaredTo(query) <= championDistance)
                findNearest(query, t.lb, leftRect);
        }
    }
}
