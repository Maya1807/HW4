public class PathFromRoot {
    /**
     * Checks if a path exists from the root node to a leaf node in the binary tree that matches the given string.
     *
     * @param root the root node of the binary tree
     * @param str  the string representing the desired path
     * @return true if a matching path exists, false otherwise
     */
    public static boolean doesPathExist(BinNode<Character> root, String str) {
        if (root == null || str.length() == 0) {
            return true;
        }

        if (str.charAt(0) != root.getData())
            return false;
        else {
            return doesPathExist(root.getLeft(), str.substring(1)) ||
                    doesPathExist(root.getRight(), str.substring(1));
        }
    }
}

