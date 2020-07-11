/**
 * Do NOT modify. This is the class with the main function
 */

import java.util.ArrayList;
import java.util.List;
import application.BTree.BTreeNode;

/**
 * B+Tree Structure Key - StudentId Leaf Node should contain [ key,recordId ]
 */
class BTree {

  /**
   * Pointer to the root node.
   */
  private BTreeNode root;
  /**
   * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
   **/
  private int t;

  BTree(int t) {
    this.root = null;
    this.t = t;
  }

  long search(long studentId) {
    /**
     * TODO: Implement this function to search in the B+Tree. Return recordID for the given
     * StudentID. Otherwise, print out a message that the given studentId has not been found in the
     * table and return -1.
     */

    BTreeNode foundNode = search(this.root, studentId);
    
    if (foundNode == null) {
      System.out.println("The provided ID has not been found in the table.");
      return -1;
    } else {
      int index = foundNode.keys.indexOf(studentId);
      return foundNode.values.get(index);
    }


    //return -1;
  }

  private BTreeNode search(BTreeNode current, long studentId) {

    // check whether the current node has the key
    for (int i = 0; i < current.keys.size(); i++) {
      if (current.keys.get(i) == studentId) {
        return current;
      } 
    }
    
    // if the current node isn't a leaf node, find the child node to move to.
    if (!current.leaf) {
      int i = 1;
      while (i <= current.getNumKeys() && studentId > current.keys.get(i - 1)) {
        i++;
      }
      return search(current.children.get(i - 1), studentId);
    } else { // if it is a leaf, the id doesn't exist
      return null;
    }



    //return null;
  }

  BTree insert(Student student) {
    /**
     * TODO: Implement this function to insert in the B+Tree. Also, insert in student.csv after
     * inserting in B+Tree.
     */

    BTreeNode current = this.root;

    if (current == null) {
      current = new BTreeNode(t, true);
      // TODO write to CSV
      return this;
    }

    // check whether the Student exists in the tree
    return this;
  }

  boolean delete(long studentId) {
    /**
     * TODO: Implement this function to delete in the B+Tree. Also, delete in student.csv after
     * deleting in B+Tree, if it exists. Return true if the student is deleted successfully
     * otherwise, return false.
     */
    return true;
  }

  List<Long> print() {

    List<Long> listOfRecordID = new ArrayList<>();

    /**
     * TODO: Implement this function to print the B+Tree. Return a list of recordIDs from left to
     * right of leaf nodes.
     *
     */

    // check whether the root is null
    if (this.root == null) {
      System.out.println("The B+ tree is empty!");
    }

    System.out.println("------------------------------------------");
    print(this.root, "");
    System.out.println("------------------------------------------");


    return listOfRecordID;
  }

  /**
   * private recursive helper method for printSideways() above.
   * 
   * @param current the node to print from
   * @param indent
   */
  private void print(BTreeNode current, String indent) {
    if (current != null) {
      if (current.getNumChildren() == 1) {
        print(current.children.get(0), indent + "    ");
      } else if (current.getNumChildren() == 2) {
        print(current.children.get(1), indent + "    ");
        System.out.println(indent + current.getKeys());
        print(current.children.get(0), indent + "    ");
      } else if (current.getNumChildren() == 3) {
        print(current.children.get(2), indent + "    ");
        System.out.println(indent + current.getKeys());
        print(current.children.get(1), indent + "    ");
        print(current.children.get(0), indent + "    ");
      } else {
        print(current.children.get(3), indent + "    ");
        print(current.children.get(2), indent + "    ");
        System.out.println(indent + current.getKeys());
        print(current.children.get(1), indent + "    ");
        print(current.children.get(0), indent + "    ");
      }
    }
  }
}
