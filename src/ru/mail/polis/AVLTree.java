package ru.mail.polis;

import java.util.*;

public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {
    private class Node {
        E data;
        int height;
        Node left;
        Node right;

        Node(E data) {
            this.data = data;
            height = 1;
        }

        Node(E data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
            fixHeight();
        }

        int balanceFactor() {
            final int hl = left != null ? left.height : 0;
            final int hr = right != null ? right.height : 0;
            return hr - hl;
        }

        void fixHeight() {
            final int hl = left != null ? left.height : 0;
            final int hr = right != null? right.height : 0;
            height = Math.max(hl, hr) + 1;
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
                    height == node.height &&
                    Objects.equals(left, node.left) &&
                    Objects.equals(right, node.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, height, left, right);
        }
    }

    private Node root;
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
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
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.data;
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
        if (elem != null) {
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
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        if (!isEmpty()) {
            Node curr = root;
            while (curr != null) {
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
        } else if (this.contains(value)) {
            return false;
        }
        if (isEmpty()) {
            root = new Node(value);
        } else {
            insert(root, value);
        }
        size++;
        return true;
    }
    private Node insert(Node p, E value) {
        if (p == null) {
            return new Node(value);
        }
        if (compare(value, p.data) < 0) {
            p.left = insert(p.left, value);
        } else {
            p.right = insert(p.right, value);
        }
        Node r = balance(p);
        if (p.equals(root)) {
            root = r;
        }
        return r;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        } else if (!this.contains(value)) {
            return false;
        }
        if (size() == 1) {
            root = null;
        } else {
            delete(root, value);
        }
        size--;
        return true;
    }
    private Node delete(Node p, E value) {
        if (p == null) {
            return null;
        }
        if (compare(value, p.data) < 0) {
            p.left = delete(p.left, value);
        } else if (compare(value, p.data) > 0) {
            p.right = delete(p.right, value);
        } else {
            Node q = p.left;
            Node r = p.right;
            if (r == null) {
                return q;
            }
            Node min = findMin(r);
            min.right = removeMin(r);
            min.left = q;
            Node f = balance(min);
            if (p.equals(root)) {
                root = f;
            }
            return f;
        }
        Node f = balance(p);
        if (p.equals(root)) {
            root = f;
        }
        return f;
    }
    private Node findMin(Node p) {
        return p.left != null ? findMin(p.left) : p;
    }
    private Node removeMin(Node p) {
        if (p.left == null) {
            return p.right;
        }
        p.left = removeMin(p.left);
        return balance(p);
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    private Node rightRotate(Node p) {
        Node q = p.left;
        p.left = q.right;
        q.right = p;
        p.fixHeight();
        q.fixHeight();
        return q;
    }
    private Node leftRotate(Node q) {
        Node p = q.right;
        q.right = p.left;
        p.left = q;
        q.fixHeight();
        p.fixHeight();
        return p;
    }
    private Node balance(Node p) {
        p.fixHeight();
        if (p.balanceFactor() == 2) {
            if (p.right.balanceFactor() < 0) {
                p.right = rightRotate(p.right);
            }
            return leftRotate(p);
        }
        if (p.balanceFactor() == -2) {
            if (p.left.balanceFactor() > 0) {
                p.left = leftRotate(p.left);
            }
            return rightRotate(p);
        }
        return p;
    }
}
