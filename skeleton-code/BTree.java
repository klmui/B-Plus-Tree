/**
 * Do NOT modify. This is the class with the main function
 */

import java.util.ArrayList;
import java.util.List;
// import application.BTree.BTreeNode;

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

    // null root check
    if (this.root == null) {
      System.out.println("The tree is empty!");
      return -1;
    }

    BTreeNode foundNode = search(this.root, studentId);

    if (foundNode == null) {
      System.out.println("The provided ID has not been found in the table.");
      return -1;
    } else {
      int index = foundNode.keys.indexOf(studentId);
      return foundNode.values.get(index);
    }


    // return -1;
  }

  private BTreeNode search(BTreeNode current, long studentId) {

    // check whether the current node has the key
    for (int i = 0; i < current.keys.size(); i++) {
      if ((current.keys.get(i) != null) && (current.keys.get(i) == studentId)) {
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

    // return null;
  }

  BTree insert(Student student) {
    /**
     * TODO: Implement this function to insert in the B+Tree. Also, insert in student.csv after
     * inserting in B+Tree.
     */



    if (this.root == null) { // if the root is null, create the first node
      this.root = new BTreeNode(t, true);
      this.root.keys.add(0, student.studentId);
      this.root.values.add(0, student.recordId);

      // FIXME remove after testing
      System.out.println("Student " + student.studentName + " (ID: " + student.studentId + ")"
          + " has been added.");

      // TODO write to CSV

      return this;
    }

    BTreeNode current = this.root;

    // check whether the ID already exists (no duplicates)
    long id = student.studentId;
    boolean exists = exists(id, current);

    // if the ID doesn't already exist in the tree, continue with the insert
    if (!exists) {
      insert(current, student);
    } else {
      System.out.println("A student with that ID already exists.");
    }



    // check whether the Student exists in the tree
    return this;
  }

  /**
   * Helpful source for split:
   * https://github.com/jiaguofang/b-plus-tree/blob/master/src/main/java/com/jgfanng/algo/BPlusTree.java
   * 
   * Helpful source for I/O: https://stackabuse.com/reading-and-writing-csvs-in-java/
   * 
   * @param bTreeNode
   * @param student
   */
  private void insert(BTreeNode bTreeNode, Student student) {

    BTreeNode current = bTreeNode;
    long id = student.studentId;

    // if the current node is an internal node
    if (current.leaf == false) {
      // find the index of the child node to move to
      int i = 0;
      while ((i + 1 <= current.getNumKeys()) && (id >= current.keys.get(i))) {
        i++;
      }

      // move to the child node
      if (current.children.get(i - 1) != null) {
        insert(current.children.get(i - 1), student);
      }
    }

    // if the current node is a leaf node
    if (current.leaf) {
      // check if the current node has space
      if (current.getNumKeys() <= ((2 * t - 1))) {
        // add the key
        current.keys.add(id);

        // sort the key elements from smallest to largest
        current.keys.sort(null);

        // get the index of the newly added id
        int index = current.keys.indexOf(id);
        // TODO write to CSV

        // add the value in the corresponding index
        current.values.set(index, student.recordId);

        // FIXME remove after testing
        System.out.println("Student " + student.studentName + " (ID: " + student.studentId + ")"
            + " has been added.");
        return;
        /*
         * for (int i = 0; i < current.keys.size(); i++) { if (current.keys.get(i) <= id) { if ((i +
         * 1 < current.getNumKeys()) && (id < current.keys.get(i + 1))) { // add the key and value
         * current.keys.add(i, id); current.values.add(i, student.recordId); // FIXME remove after
         * testing System.out.println("Student " + student.studentName + " has been added."); //
         * TODO write to CSV
         * 
         * break; } } else { continue; } }
         */
      } else { // if the leaf does not have room, split

        BTreeNode parent = findParent(this.root, current); // find the parent node
        BTreeNode node1 = new BTreeNode(t, true); // create first new node
        BTreeNode node2 = new BTreeNode(t, true); // create second new node

        // add the new key and value, which will cause overflow
        // add the key
        current.keys.add(id);

        // sort the key elements from smallest to largest
        current.keys.sort(null);

        // get the index of the newly added id
        int index = current.keys.indexOf(id);
        // TODO write to CSV

        // add the value in the corresponding index
        current.values.set(index, student.recordId);
        // FIXME remove after testing
        System.out.println("Student " + student.studentName + " (ID: " + student.studentId + ")"
            + " has been added.");

        /*
         * for (int i = 0; i < current.keys.size(); i++) { if (id <= current.keys.get(i)) {
         * current.keys.add(i, id); current.values.add(i, student.recordId); // FIXME remove after
         * testing System.out.println("Student " + student.studentName + " (ID: " +
         * student.studentId + ")" + " has been added."); }
         */
        // }

        if (parent == null) { // no parent means current == root
          // copy keys from this node to the new node
          int from = (current.getNumKeys() + 1) / 2;
          int to = current.getNumKeys() - 1;

          node1.keys.addAll(current.keys.subList(0, from));

          List<Long> node1Values = current.values.subList(0, from);
          for (int i = 0; i < from; i++) {
            node1.values.set(i, node1Values.get(i));
          }
          // node1.values.addAll(current.values.subList(0, from));

          node2.keys.addAll(current.keys.subList(from, to + 1));

          // copy values from this node to the new node
          List<Long> node2Values = current.values.subList(from, to + 1);
          for (int i = 0; i < node2Values.size(); i++) {
            node2.values.set(i, node2Values.get(i));
          }


          // node2.values.addAll(current.values.subList(from, to + 1));

          // clear copied keys and values from current node
          current.keys.subList(from, to + 1).clear();
          current.keys.subList(0, (from - 1)).clear();
          current.values.clear();
          // current.values.subList(from, to).clear();
          // current.values.subList(0, (from - 1)).clear();

          // add children
          this.addChild(current, node1);
          this.addChild(current, node2);
          // current.children.add(node1);
          // current.children.add(node2);

          // set next pointer
          // node2.next = node1.next;
          // node1.next = node2;
          // current.next = null;

          this.root = current;
          this.root.leaf = false;
          return;
        }

        // copy keys from this node to the new node
        int from = (current.getNumKeys() + 1) / 2;
        int to = current.getNumKeys() - 1;

        node1.keys.addAll(current.keys.subList(0, from));
        node1.values.addAll(current.values.subList(0, from));

        node2.keys.addAll(current.keys.subList(from, to + 1));

        // copy values from this node to the new node
        node2.values.addAll(current.values.subList(from, to + 1));

        // clear copied keys and values from current node
        current.keys.subList(from, to + 1).clear();
        current.keys.subList(0, (from - 1)).clear();
        current.values.clear();
        // current.values.subList(from, to).clear();
        // current.values.subList(0, (from - 1)).clear();

        // add children
        this.addChild(current, node1);
        this.addChild(current, node2);
        // current.children.add(node1);
        // current.children.add(node2);

        // set next pointer
        // node2.next = node1.next;
        // node1.next = node2;
        // current.next = null;


      }
    }
  }

  boolean delete(long studentId) {
    /**
     * TODO: Implement this function to delete in the B+Tree. Also, delete in student.csv after
     * deleting in B+Tree, if it exists. Return true if the student is deleted successfully
     * otherwise, return false.
     */
    return true;
  }

  /**
   * Helpful sources: https://www.geeksforgeeks.org/print-leaf-nodes-left-right-binary-tree/
   * 
   * @return
   */
  List<Long> print() {

    BTreeNode current = this.root;
    List<Long> listOfRecordID = print(current);


    /**
     * TODO: Implement this function to print the B+Tree. Return a list of recordIDs from left to
     * right of leaf nodes.
     *
     */

    return listOfRecordID;
  }

  private List<Long> print(BTreeNode current) {
    // BTreeNode currentNode = current;
    List<Long> list = new ArrayList<Long>();
    // List<Long> recIdList = listOfIDs;

    // check whether the root is null
    if (current == null) {
      // System.out.println("The B+ tree is empty!");
      return list;
    } else if (current.leaf) {
      for (Long value : current.values) {
        if (value != null) {
          list.add(value);
        }
      }

    } else {
      for (BTreeNode child : current.children) {
        if (child != null) {
          list.addAll(print(child));
        }
      }
    }
    return list;
  }

  private BTreeNode split(BTreeNode node) {
    // TODO

    return null;
  }

  private BTreeNode merge(BTreeNode node) {
    // TODO

    return null;
  }

  private BTreeNode findParent(BTreeNode root, BTreeNode node) {
    BTreeNode parent;

    BTreeNode current = root;

    long minKey = node.keys.get(0);

    // search the current node's children for a match
    if (current.children.contains(node)) {
      parent = current;
      return parent;
    }

    // find the subtree to move to
    int index = 0;
    while (minKey >= current.keys.get(index)) {
      index++;
    }
    /*
     * for (int i = 0; i < current.getNumKeys(); i++) { if ((current.keys.get(i) <= minKey) &&
     * (minKey < current.keys.get(i + 1))) { index = i; break; } }
     */

    boolean empty = true;
    for (int i = 0; i < current.children.size(); i++) {
      if (current.children.get(i) != null) {
        empty = false;
        break;
      }
    }

    if (empty) {
      return null;
    }

    if (current.children.isEmpty()) { // if this node has no children, return
      return null;
    }


    BTreeNode node1 = current.children.get(index - 1);

    parent = findParent(current, node1);

    return null;
  }

  private boolean exists(long id, BTreeNode current) {
    // check whether the current node has the key

    if (current.keys.contains(id)) {
      return true;
    }

    /*
     * for (int i = 0; i < current.keys.size(); i++) { if ((current.keys.get(i) != null) &&
     * (current.keys.get(i) == id)) { return true; } }
     */

    // if the current node isn't a leaf node, find the child node to move to.
    if (!current.leaf) {
      int i = 0;
      while ((i < current.getNumKeys()) && (current.keys.get(i) <= id)) {
        i++;
      }
      return exists(id, current.children.get(i - 1));
    } else { // if it is a leaf, the id doesn't exist
      return false;
    }

    // return false;
  }

  private void addChild(BTreeNode parent, BTreeNode child) {

    long minKey = child.keys.get(0);
    int i = 0;

    boolean empty = true;

    for (int j = 0; j < parent.children.size(); j++) { // check if the child list is empty
      if (parent.children.get(j) != null) {
        empty = false;
        break;
      }
    }

    // find the index just past where the child node should go
    while ((i < parent.keys.size()) && minKey > parent.keys.get(i)) {
      i++;
    }

    if (empty) { // if it's empty, add the child at index 0
      parent.children.set(0, child);
      return;
      // } else if (i == 0){

    } else { // if it's not empty, find where the child should go

      if (parent.children.get(i) == null) { // if this child slot is empty, add the child node
        parent.children.set(i, child);
      }

      // update the next pointers
      for (int j = 0; j < parent.children.size(); j++) {
        BTreeNode childToUpdate;

        if (parent.children.get(j) != null) { // select a child node
          childToUpdate = parent.children.get(j);

          // find the next non-null child node
          for (int k = j + 1; k < parent.children.size(); k++) {
            if (parent.children.get(k) != null) {
              childToUpdate.next = parent.children.get(k);
              break;
            }
          }

          continue;
        }


      }
    }


  }
}
