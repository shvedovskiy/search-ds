package ru.mail.polis;

import java.util.*;

public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {
    private int size;
    private final Comparator<E> comparator;
    private Node nil = new Node(false, null, null, null, null);
    private Node root = nil;

    public RedBlackTree() {
        this(null);
    }
    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty, no first element");
        }
        Node curr = root;
        while (curr.left != nil) {
            curr = curr.left;
        }
        return curr.data;
    }
    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty, no last element");
        }
        Node curr = root;
        while (curr.right != nil) {
            curr = curr.right;
        }
        return curr.data;
    }
    private Node last(Node elem) {
        Node curr = elem;
        while (curr.right != nil) {
            curr = curr.right;
        }
        return curr;
    }

    @Override
    public List<E> inorderTraverse() {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty");
        }
        List<E> res = new LinkedList<>();
        inorderTraverse(root, res);
        return res;
    }
    private void inorderTraverse(Node elem, List<E> lst) {
        if (elem != nil) {
            inorderTraverse(elem.left, lst);
            lst.add(elem.data);
            inorderTraverse(elem.right, lst);
        }
    }


    @Override
    public int size() {
        return size;
    }
    @Override
    public boolean isEmpty() {
        return root == nil;
    }
    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        if (!isEmpty()) {
            Node curr = root;
            while (curr != nil) {
                int cmp = compare(curr.data, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        Node z;
        Node y = nil;
        Node x = root;

        while (x != nil) {
            y = x;
            if (compare(value, x.data) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        if (y == nil) {
            z = new Node(false, value, y, nil, nil);
            root = z;
        } else if (compare(value, y.data) < 0) {
            z = new Node(true, value, y, nil, nil);
            y.left = z;
        } else {
            z = new Node(true, value, y, nil, nil);
            y.right = z;
        }
        insertFixup(z);
        size++;
        return true;
    }
    private void insertFixup(Node z) {
        while (z.parent.isRed) {
            Node y;
            if (z.parent == z.parent.parent.left) {
                y = z.parent.parent.right;
                if (y.isRed) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    rightRotate(z.parent.parent);
                }
            } else {
                y = z.parent.parent.left;
                if (y.isRed) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.isRed = false;
    }
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;

        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }
    private void rightRotate(Node y) {
        Node x = y.left;
        y.left = x.right;

        if (x.right != nil) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == nil) {
            root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }
        x.right = y;
        y.parent = x;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        Node z = search(value);
        if (z == null) {
            return false;
        }
        Node y = z;
        Node x;
        boolean isRedOriginal = y.isRed;

        if (z.left == nil) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == nil) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = last(z.right);
            isRedOriginal = y.isRed;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.isRed = z.isRed;
        }
        if (!isRedOriginal) {
            removeFixup(x);
        }
        size--;
        return true;
    }
    private Node search(E value) {
        Node x = root;
        while (x != nil) {
            if (compare(value, x.data) == 0) {
                return x;
            } else if (compare(value, x.data) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return null;
    }
    private void transplant(Node u, Node v) {
        if (u.parent == nil) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }
    private void removeFixup(Node x) {
        while (x != root && !x.isRed) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.isRed) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (!w.left.isRed && !w.right.isRed) {
                    w.isRed = true;
                    x = x.parent;
                } else {
                    if (!w.right.isRed) {
                        w.left.isRed = false;
                        w.isRed = true;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.right.isRed = false;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.isRed) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (!w.right.isRed && !w.left.isRed) {
                    w.isRed = true;
                    x = x.parent;
                } else {
                    if (!w.left.isRed) {
                        w.right.isRed = false;
                        w.isRed = true;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.left.isRed = false;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.isRed = false;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    private class Node {
        boolean isRed;
        E data;
        Node parent;
        Node right;
        Node left;

        Node() {
            isRed = false;
        }
        Node(boolean isRed, E data, Node parent, Node right, Node left) {
            this.isRed = isRed;
            this.data = data;
            this.parent = parent;
            this.right = right;
            this.left = left;
        }
        Node(Node node) {
            isRed = node.isRed;
            data = node.data;
            parent = node.parent;
            right = node.right;
            left = node.left;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return data == node.data &&
                    isRed == node.isRed &&
                    Objects.equals(parent, node.parent) &&
                    Objects.equals(left, node.left) &&
                    Objects.equals(right, node.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, isRed, parent, left, right);
        }
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(50);
        tree.add(25);
        tree.add(75);
        tree.add(100);
        tree.add(70);
        tree.add(60);
        tree.add(55);
        tree.remove(55);
        tree.remove(75);
        tree.add(75);
        System.out.println("lol");
    }
}
