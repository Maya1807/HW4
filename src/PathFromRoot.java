public class PathFromRoot {
    public static boolean doesPathExist(BinNode<Character> root, String str) {
        if(root == null || str.length() == 0){
            return true;
        }

        if (str.charAt(0) != root.getData())
            return false;
        else{
            return doesPathExist(root.getLeft(), str.substring(1)) ||
                    doesPathExist(root.getRight(), str.substring(1));
        }
    }

}
