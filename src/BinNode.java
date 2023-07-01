public class BinNode<E> {
    private E data;
    private BinNode<E> left;
    private BinNode<E> right;

    /**
     * Constructs a BinNode with the specified data, left child, and right child.
     *
     * @param data  the data to be stored in the node
     * @param left  the left child of the node
     * @param right the right child of the node
     */
    public BinNode(E data, BinNode<E> left, BinNode<E> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    /**
     * Constructs a BinNode with the specified data and no children.
     *
     * @param data the data to be stored in the node
     */
    public BinNode(E data) {
        this(data, null, null);
    }

    /**
     * Returns the data stored in the node.
     *
     * @return the data stored in the node
     */
    public E getData() {
        return data;
    }

    /**
     * Returns the left child of the node.
     *
     * @return the left child of the node
     */
    public BinNode<E> getLeft() {
        return left;
    }

    /**
     * Returns the right child of the node.
     *
     * @return the right child of the node
     */
    public BinNode<E> getRight() {
        return right;
    }

    /**
     * Sets the data stored in the node.
     *
     * @param data the data to be stored in the node
     */
    public void setData(E data) {
        this.data = data;
    }

    /**
     * Sets the left child of the node.
     *
     * @param left the left child of the node
     */
    public void setLeft(BinNode<E> left) {
        this.left = left;
    }

    /**
     * Sets the right child of the node.
     *
     * @param right the right child of the node
     */
    public void setRight(BinNode<E> right) {
        this.right = right;
    }
}

