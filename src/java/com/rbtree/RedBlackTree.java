package com.rbtree;

import java.lang.Comparable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TO DO : Learn how to add docstrings, and then test this datastructure

public class RedBlackTree<T extends Comparable<T>> {
  private Node root;

  public RedBlackTree() {
    root = null;
  }

  // Internal class
  private class Node {
    boolean red;
    Node leftNode;
    Node rightNode;
    Node parent;
    T data;

    public Node(T data, boolean color) {
      this.data = data;
      this.red = color;
      this.leftNode = null;
      this.rightNode = null;
      this.parent = null;
    }

  }

  // Main Data Structure Methods
  public void insert(T data) {

    if (root == null) {
      root = new Node(data, false);
    } else {
      Node newNode = new Node(data, true);
      newNode.parent = insertHelper(root, newNode);
      if (newNode.data.compareTo(newNode.parent.data) < 0) {
        newNode.parent.leftNode = newNode;
      } else {
        newNode.parent.rightNode = newNode;
      }

      // Do any necessary rotating or recolloring
      if (newNode.parent.red == true) {
        refresh(newNode);
      }

    }

  }

  public Node get(T val) {

    Node current = root;

    while (current != null) {
      if (val.compareTo(current.data) < 0) {
        current = current.leftNode;
      } else if (val.compareTo(current.data) > 0) {
        current = current.rightNode;
      } else if (val.compareTo(current.data) == 0) {
        break;
      }
    }

    return current;
  }

  public void delete(T val) {

    Node node = get(val);
    if (node == null) {
      return;
    }
    if (node.parent == null) {
      root = null;
      return;
    }

    if (node.rightNode == null & node.leftNode == null) {
      // Case 1: node is a leaf node and thus has no children
      if (node.parent.leftNode == node) {
        node.parent.leftNode = null;
      } else {
        node.parent.rightNode = null;
      }
    } else if (node.leftNode != null ^ node.rightNode != null) {
      // Case 2: only one child is null
      if (node.parent.leftNode == node) {
        node.parent.leftNode = (node.leftNode != null) ? node.leftNode : node.rightNode;
      } else {
        node.parent.rightNode = (node.leftNode != null) ? node.leftNode : node.rightNode;
      }
    } else {
      // Case 3: neither child is null
      Node successor = inorderSuccessor(node.rightNode);
      delete(successor.data);
      node.data = successor.data;
      return;
    }

    // Fix Up, pass in the child taking node's spot, and whether the replacement
    // node is double black
    if (node.leftNode != null) {
      deleteFix(node.leftNode, false);
    } else if (node.rightNode != null) {
      deleteFix(node.rightNode, false);
    } else if (node.red) {
      deleteFix(node, false);
    } else {
      deleteFix(node, true);
    }

    node = null; // Delete for good
  }

  // Helper methods
  private Node insertHelper(Node node, Node newNode) {
    // Find the parent of the node to be inserted

    Node previousNode = null;
    Node currentNode = node;
    while (currentNode != null) {

      if (newNode.data.compareTo(currentNode.data) < 0) {
        previousNode = currentNode;
        currentNode = currentNode.leftNode;
      } else if (newNode.data.compareTo(currentNode.data) > 0) {
        previousNode = currentNode;
        currentNode = currentNode.rightNode;
      } else {
        break; // No duplicates in rb trees
      }

    }

    return previousNode;
  }

  private void refresh(Node current) {

    while (current != root && current.parent.red) {
      Node uncle = uncle(current);
      if (uncle != null && uncle.red) {
        // Case 1: both parent and uncle are red
        // Change the uncle and parent to black, the gradfather to red, and set the
        // current node to the grandparent
        current.parent.red = false;
        uncle.red = false;
        current.parent.parent.red = true;
        current = current.parent.parent;
      } else if ((uncle == null || !uncle.red)
          && (current.parent.leftNode == current
              && current.parent.parent.leftNode == current.parent)) {
        // Case 2: black uncle, parent & current are left children
        // Recolor current's grandparent to red, current's parent to black
        // Right fotate current
        if (current.parent.parent == root) {
          root = current.parent;
        }
        current.parent.parent.red = true;
        current.parent.red = false;
        rotateRight(current.parent);
      } else if ((uncle == null || !uncle.red)
          && (current.parent.rightNode == current
              && current.parent.parent.rightNode == current.parent)) {
        // Case 3: Mirror of case 2
        // Black uncle, parent & current are right children
        // Recolor current's gradparent to red, current's parent to black
        if (current.parent.parent == root) {
          root = current.parent;
        }
        current.parent.parent.red = true;
        current.parent.red = false;
        rotateLeft(current.parent);
      } else if ((uncle == null || !uncle.red)
          && (current.parent.rightNode == current
              && current.parent.parent.leftNode == current.parent)) {
        // Case 4: Black uncle, current is right child, & current's parent is left child
        current = rotateLeft(current);
      } else if ((uncle == null || !uncle.red)
          && (current.parent.leftNode == current
              && current.parent.parent.rightNode == current.parent)) {
        // Case 5: Mirror of case 4
        current = rotateRight(current);
      }
    }

    if (current == root) {
      current.red = false;
    }

  }

  private void deleteFix(Node u, boolean db) {
    // u: replacement node, unless db = true, then u is the old node
    // db: true if replacement node is double black, meaning both it and the node
    // it replaced were black

    if (u == null & !db) {
      // Case 1: a red leaf was deleted, no fixing necessary
      return;
    }
    if (!db) {
      // Case 2: either the replacement node or the deleted node was red
      // Fix: Color the replacement node black
      if (u.parent.leftNode == u) {
        u.parent.leftNode.red = false;
      } else {
        u.parent.rightNode.red = false;
      }
    } else if (db) {
      // Case 3: Double black situation (both node getting deleted and replacement
      // node are black)

      // For double black situations, this is the sybling
      Node s = (u.parent.rightNode != null) ? u.parent.rightNode : u.parent.leftNode;

      if (s.leftNode == null & s.rightNode == null) {
        // Case 3.3a : s is black and both of its children are black ie null, since
        // neither could be non null nodes and black since that would not have violated
        // rb tree rules
        // Resolution : color s red, color s.parent black if it's red or refix on
        // s.parent
        s.red = true;
        if (s.parent.red) {
          s.parent.red = false;
        } else {
          deleteFix(s.parent, true);
        }
      } else if ((s.parent.leftNode == s & !s.red) & (s.leftNode != null && s.leftNode.red)) {
        // Case 3.3b LL : s is a black left child, it's left child is red, or both
        // children are red
        s.red = s.parent.red;
        s.leftNode.red = false;
        s.parent.red = false;
        rotateRight(s);
      } else if ((s.parent.rightNode == s & !s.red) & (s.rightNode != null && s.rightNode.red)) {
        // Case 3.3b RR : s is a black right child, it's right child is red, or both
        // children are red
        s.red = s.parent.red;
        s.rightNode.red = false;
        s.parent.red = false;
        rotateLeft(s);
      } else if ((s.parent.leftNode == s & !s.red) & (s.rightNode != null && s.rightNode.red)) {
        // Case 3.3b LR : s is black left child, it's only child is right and red
        s.rightNode.red = false;
        s.red = true;
        rotateLeft(s.rightNode);
      } else if ((s.parent.rightNode == s & !s.red) & (s.leftNode != null && s.leftNode.red)) {
        // Case 3.3b RL : s is black right child, it's only child is left and red
        s.leftNode.red = false;
        s.red = true;
        rotateRight(s.leftNode);
      } else if (s.red) {
        // Case 3.3c : s is red
        s.red = false;
        if (s.parent.leftNode == s) {
          s.rightNode.red = true;
          rotateRight(s);
        } else {
          s.leftNode.red = true;
          rotateLeft(s);
        }
      }
    }

  }

  private Node rotateRight(Node rotatingNode) {

    // Has to be done in the right order

    // Step 1: Reconnect grandparent and rotating node
    Node p = rotatingNode.parent;
    rotatingNode.parent = p.parent;
    if (p.parent != null) {
      if (p.parent.leftNode == p) {
        p.parent.leftNode = rotatingNode;
      } else {
        p.parent.rightNode = rotatingNode;
      }
    }

    // Step 2: Reconnect the old parent that's rotated down, to the rotating node's
    // child
    Node b = rotatingNode.rightNode;
    p.leftNode = b;
    if (b != null) {
      b.parent = p;
    }

    // Step 3
    rotatingNode.rightNode = p;
    p.parent = rotatingNode;

    return p;

  }

  private Node rotateLeft(Node rotatingNode) {
    // Has to be done in the right order

    // Step 1: Reconnect grandparent and rotating node
    Node p = rotatingNode.parent;
    rotatingNode.parent = p.parent;
    if (p.parent != null) {
      if (p.parent.leftNode == p) {
        p.parent.leftNode = rotatingNode;
      } else {
        p.parent.rightNode = rotatingNode;
      }
    }

    // Step 2: Reconnect the old parent that's rotated down, to the rotating node's
    // child
    Node b = rotatingNode.leftNode;
    p.rightNode = b;
    if (b != null) {
      b.parent = p;
    }

    // Step 3
    rotatingNode.leftNode = p;
    p.parent = rotatingNode;

    return p;
  }

  private Node uncle(Node node) {
    // Note: could possibly return a null value for uncle of node

    Boolean bool = (node.parent.parent.leftNode == node.parent);
    return (bool) ? node.parent.parent.rightNode : node.parent.parent.leftNode;

  }

  private Node sybling(Node node) {
    Node sybling = (node.parent.rightNode == node) ? node.parent.leftNode : node.parent.rightNode;
    return sybling;
  }

  private void traverse(Node node) {
    // Depth first search

    if (node != null) {
      traverse(node.leftNode);
      System.out.printf("%d ", node.data);
      traverse(node.rightNode);
    }

  }

  private Node inorderSuccessor(Node node) {
    if (node.leftNode == null) {
      return node;
    } else {
      return inorderSuccessor(node.leftNode);
    }
  }

  private StringBuilder breadthFirst(Node node, int space, StringBuilder sb) {

    if (node != null) {
      space += 10;
      sb = breadthFirst(node.rightNode, space, sb);
      sb.append("\n");
      for (int i = 10; i < space; i++) {
        sb.append(" ");
      }
      if (node.red) {
        sb.append("\u001b[31m" + node.data + "\u001b[37m");
      } else {
        sb.append(node.data);
      }
      sb.append("\n");
      sb = breadthFirst(node.leftNode, space, sb);
    }

    return sb;

  }

  public void inOrderTraversal() {
    // Traverse tree in order
    System.out.print("\n");
    traverse(root);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb = breadthFirst(root, 5, sb);

    return sb.toString();
  }

}