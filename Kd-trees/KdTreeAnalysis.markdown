___
### Q1: Give the total memory usage in bytes (using tilde notation) of 
### your 2d-tree data structure as a function of the number of points n.
___

#### Memory Analysis
```java
   private static class Node {
        private Point2D p;      // the point
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean orientation;
   }
```

Node | Object Overhead | Point2D  | Reference (x4) |  size(int) |  orientation(bool) | Padding
---  |   ---           |   ---    |      ---       |      ---   |             ---    |       ---
 1   |     16 Bytes    | 32 Bytes |    32 Bytes    |   4 bytes  |        1 byte      | 3 bytes
 
##### Total Memory per Node :  88  Bytes
##### Memory of 2D Structure: ~88N Bytes

___
### Q2: Give the expected running time in seconds (using tilde notation) 
### to build a 2d-tree on n random points in the unit square.
___
#### More precise analysis
 
 N    |  T (Seconds) | T / Prev (T)
 ---  |  ---           |    ---
100K |     1.175       |     n/a
200K |     1.729       |     1.47
400K |     3.131       |     1.81
800K |     5.638       |     1.80

Using the formula T(N) = a N^b (where b = log base 2 of converge of column c)
###### T(N) = 5.4 x 10^-5 N^.85

#### Tilde analysis:
~(t1) NlgN, where t1 = average time for one insertion.

Explanation: I take the most commonly used operation, put, which recursively calls 
itself by some number of times until null is found. How many times is this? 
Roughly lg N if the tree is well balanced, or sqrt of N if not (on average). I multiply 
this by the time it takes to perform this operation.

___
### Q3: How many nearest-neighbor calculations can your 2d-tree implementation perform
### per second for input100K.txt (100,000 points) and input1M.txt (1 million points), 
### where the query points are random points in the unit square? 
___
    
##### Results given are the mean of 50 tests. 
 N    |  Brute | 2d Tree
 ---  |  ---           |    ---
100K |    614       |    1366064
 1M |     12      |     3978727
