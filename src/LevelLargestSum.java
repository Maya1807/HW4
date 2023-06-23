import java.util.ArrayDeque;
import java.util.Deque;

public class LevelLargestSum {
    public static int getLevelWithLargestSum(BinNode<Integer> root) {
        if (root == null)
            return -1;

        int maxSum = root.getData();
        int maxSumLevel = 0;
        Deque<BinNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int count = queue.size();
            int currSum = 0;
            while (count > 0) {
                count--;
                BinNode temp = queue.poll();
                if (temp != null) {
                    currSum = currSum + (int) temp.getData();


                    if (temp.getLeft() != null)
                        queue.add(temp.getLeft());
                    if (temp.getRight() != null)
                        queue.add(temp.getRight());
                }
            }

            if(currSum > maxSum){
                maxSum = currSum;
                maxSumLevel++;
            }

        }
        return maxSumLevel;
    }
}
