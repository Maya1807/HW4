import java.util.ArrayDeque;
import java.util.Deque;

public class LevelLargestSum {
    public static int getLevelWithLargestSum(BinNode<Integer> root) {
        if (root == null)
            return -1;

        int maxSum = root.getData();
        int maxSumLevel = 0;
        int currentLevel = 0;
        Deque<BinNode<Integer>> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int count = queue.size();
            int currSum = 0;
            while (count > 0) {
                count--;
                BinNode<Integer> temp = queue.poll();
                if (temp != null) {
                    currSum += temp.getData();
                    if (temp.getLeft() != null)
                        queue.add(temp.getLeft());
                    if (temp.getRight() != null)
                        queue.add(temp.getRight());
                }
            }

            if(currSum > maxSum){
                maxSum = currSum;
                maxSumLevel = currentLevel;
            }
            currentLevel++;
        }
        return maxSumLevel;
    }
}
