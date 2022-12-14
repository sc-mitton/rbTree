package com.rbtree;

import java.util.Random;

public class Driver {
  public static void main(String[] args) {

    Integer[] vals = new Integer[20];
    RedBlackTree<Integer> rbTree = new RedBlackTree<>();
    Random rand = new Random();
    rand.setSeed(3);

    // Insert vals to array buffer
    for (Integer i = 0; i < vals.length; i++) {
      vals[i] = i;
    }

    // Shuffle vals
    for (int i = 0; i < vals.length; i++) {
      int randomIndexToSwap = rand.nextInt(vals.length);
      Integer temp = vals[randomIndexToSwap];
      vals[randomIndexToSwap] = vals[i];
      vals[i] = temp;
    }

    // Print out values
    System.out.print("Values to insert: ");
    for (int i = 0; i < vals.length; i++) {
      System.out.printf("%d ", vals[i]);
    }

    // Insert into tree
    for (int i = 0; i < vals.length; i++) {
      rbTree.insert(vals[i]);
    }

    rbTree.delete(11);
    System.out.println("\n");
    System.out.println(rbTree);

  }

}
