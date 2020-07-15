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

    BTreeNode current = this.root;
    long recordId = search(current, studentId);

    return recordId;

  }

  private long search(BTreeNode current, long studentId) {
    // check whether the current node has the key
    // if the current node isn't a leaf node, find the child node to move to.
    long recordId = -1;
    
    if (current.leaf) { // base case
      int index = current.keys.indexOf(studentId);
      
      if (index == -1) {
        return recordId;
      } else {
        recordId = current.values.get(index);
        return recordId;
      }
     
    }
    
    if (!current.leaf) {
      if (studentId < current.keys.get(0)) {
        recordId = search(current.children.get(0), studentId);
      } else if (studentId >= current.keys.get(current.keys.size() - 1)) {
        recordId = search(current.children.get(current.keys.size() - 1), studentId);
      } else {
        int i = 1;
        while (studentId >= current.keys.get(i)) {
          i++;
        }

        recordId = search(current.children.get(i), studentId);
      }
    }

    return recordId;
  }

  boolean delete(long studentId) {
    /**
     * TODO: Implement this function to delete in the B+Tree. Also, delete in student.csv after
     * deleting in B+Tree, if it exists. Return true if the student is deleted successfully
     * otherwise, return false.
     */
    // search to see if the node exists: search(BTreeNode current, long studentId)
    long exists = search(studentId);
    if (exists == -1) { // student id is not found
      return false;
    }

    // start at the root and compare studentId to the one we want.
    // if key doesn't exist in the root, which child tree to go to.
    if (this.root != null && this.root.leaf) {
      // find studentId

      // delete studentId

      // if needed, merge, etc.
    }

    if (!this.root.leaf) { // if the root is an internal node, find the subtree
      int i = 0;
      while ((i + 1 <= root.keys.size()) && (studentId >= root.keys.get(i))) {
        i++;
      }

      // recursive helper
      deleteHelper(this.root.children.get(i), studentId);
    }

    BTreeNode current = root;
    // go down the tree to find the leaf node, starting from the leaf:
    boolean studentIdFound = false;
    int indexOfKeyToDelete;
    if (current.leaf == true) { // we are in the leaf node
      for (int i = 0; i < current.keys.size(); i++) {
        long leafVal = current.keys.get(i);
        if (leafVal == studentId) {
          studentIdFound = true;
          indexOfKeyToDelete = i;
          break;
        }
      }
    } else { // we are not at the leaf node and need to keep going down.
      ArrayList<Long> listOfEntries = current.keys;

      boolean firstItIsLessThan = false;
      int indexToUse;
      int i = 0;
      while ((i + 1 <= listOfEntries.size()) && (studentId >= listOfEntries.get(i))) {
        i++;
      }
      // we found the index that we should go down on.


      // for (int i = 0; i < listOfEntries.size(); i++) {
      // long currentEntry = listOfEntries.get(i);
      // if (currentEntry >= studentId) {
      // firstItIsLessThan = true; // we take the first entry where there is equality or studentId
      // is less than the value in the node
      // indexToUse = i;
      // break;
      // }
      // }
      // if (firstItIsLessThan == false) { // the key is greater than all values in the current node
      // indexToUse = listOfEntries.size() - 1; // we will go into the rightmost node
      // }



    }



    return true;
  }



  private void deleteHelper(BTreeNode node, long studentID) {
    BTreeNode current = node;

    // base case: leaf node
    if (current.leaf == true) {
      // find studentID
      int index = current.keys.indexOf(studentID);
      current.keys.remove(index);
      // delete it
      int numOfKeysInNode = current.keys.size();
      int minNumOfSpots = t;
      if (numOfKeysInNode >= minNumOfSpots) {
        return;
      } else {
        // see about redistribution:
        ArrayList<Long> nextLeafNode = current.next.keys;
        if (nextLeafNode == null) { // MERGING
          // could be the rightmost child and then redistribution isn't possible
          // or could be the only child
        }

        int numOfKeysInNextNode = nextLeafNode.size();
        if (numOfKeysInNextNode >= minNumOfSpots) { // redistribution

        } else { // MERGING

        }

      }
      // merge, etc
    }


  }


  BTree insert(Student student) {
    /**
     * TODO: Implement this function to insert in the B+Tree. Also, insert in student.csv after
     * inserting in B+Tree.
     */

    // if the root is null
    if (this.root == null) {
      this.root = new BTreeNode(t, true);
      this.root.keys.add(0, student.studentId);
      this.root.values.add(0, student.recordId);

      // TODO write to CSV
      System.out.println("Student " + student.studentName + " (ID: " + student.studentId + ")"
          + " has been added.");

      return this;
    }

    // check whether a student with that ID already exists
    if (exists(student.studentId, this.root)) {
      System.out.println("A student with that ID already exists.");
      return this;
    }

    // recursively insert the new ID
    insert(this.root, student);
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
  private void insert(BTreeNode current, Student student) {

    long id = student.studentId;
    long recordId = student.recordId;

    // if the current node is an internal node
    if (current.leaf == false) {

      // find the subtree to move to
      if (id < current.keys.get(0)) {
        // go to leftmost subtree
        insert(current.children.get(0), student);

      } else if (id >= current.keys.get(current.keys.size() - 1)) {
        // go to rightmost subtree
        insert(current.children.get(current.children.size() - 1), student);

      } else { // find which subtree to move to
        int i = 0;
        while (i < current.keys.size()) {
          if (id >= current.keys.get(i)) {
            insert(current.children.get(i), student);
            break;
          } else {
            i++;
          }
        }
      }
    }

    if (current.leaf) { // if the current node is a leaf node
      insertKeyValue(current, id, recordId);

      // TODO write to CSV

      // FIXME remove after testing
      System.out.println("Student " + student.studentName + " (ID: " + student.studentId + ")"
          + " has been added.");

      if (current.keys.size() > (2 * t)) { // if the current node is full after insert, split

        splitLeaf(current);

      } else { // if the current node is full, split it
        return;
      }
    }
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

  /**
   * 
   * @param parent  the parent node of current
   * @param current the current node we're attempting to insert into
   * @param node1
   * @param node2
   * @param student
   * @return
   */
  private BTreeNode splitInternal(BTreeNode current) {
    // TODO
    BTreeNode parent = findParent(this.root, current); // find the parent node
    BTreeNode newSibling = new BTreeNode(t, true); // create first new node
    BTreeNode node2 = new BTreeNode(t, true); // create second new node

    // move (# keys - d) keys and values to the new node
    int i = 0; // index for new sibling's keys
    for (int j = (current.keys.size() - t); j < current.keys.size(); j++) {
      newSibling.keys.add(i, current.keys.get(j));
      newSibling.values.set(i, current.values.get(j));
      i++;
    }

    // update sibling pointers
    if (parent != null) {
      current.next = newSibling;
    }

    if (parent == null) {

    }

    return null;
  }

  private void splitLeaf(BTreeNode node) {
    // TODO

    BTreeNode parent = findParent(this.root, node);
    BTreeNode newNode = new BTreeNode(t, true);

    long separator = node.keys.get((node.keys.size() - 1) / 2); // node to push up

    // move (# keys - d) keys and values to the new node

    newNode.keys.addAll(node.keys.subList(t, node.keys.size()));
    newNode.values.addAll(node.values.subList(t, node.values.size()));

    node.keys.subList(t, node.keys.size()).clear();
    node.values.subList(t, node.values.size()).clear();

    if (parent == null) { // null parent means we split the root node
      this.root = new BTreeNode(t, false);
      this.root.keys.add(separator);
      addChild(this.root, node);
      addChild(this.root, newNode);
    } else { // if we have a non-null parent
      // add the separator key to the parent node
      if (separator < parent.keys.get(0)) {
        parent.keys.add(0, separator);
      } else if (separator >= parent.keys.get(parent.keys.size() - 1)) {
        parent.keys.add(separator);
      } else {
        int i = 1;
        while (i < parent.keys.size() - 1) {
          if (separator > parent.keys.get(i)) {
            i++;
          } else {
            break;
          }
        }

        parent.keys.add(i, separator);
      }

      addChild(parent, newNode);
    }
  }

  private BTreeNode merge(BTreeNode node) {
    // TODO

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

  private void addChild(BTreeNode parent, BTreeNode child) {

    long maxKey = child.keys.get(child.keys.size() - 1);

    if (maxKey < parent.keys.get(0)) {
      parent.children.add(0, child);
      // return;
    } else if (maxKey >= parent.keys.get(parent.keys.size() - 1)) {
      parent.children.add(child);
      // return;
    } else {
      for (int i = 1; i < parent.keys.size() - 1; i++) {
        if (maxKey >= parent.keys.get(i)) {
          parent.children.add(i, child);
          break;
        }
      }
    }

    // update sibling pointers
    for (int i = 0; i < parent.children.size() - 1; i++) {
      parent.children.get(i).next = parent.children.get(i + 1);
    }
  }

  private void insertKeyValue(BTreeNode node, long studentId, long recordId) {

    // if it's smaller than the smallest key in the node
    if (studentId < node.keys.get(0)) {
      node.keys.add(0, studentId);
      node.values.add(0, recordId);

    } else if (studentId > node.keys.get(node.keys.size() - 1)) { // if bigger than biggest key
      node.keys.add(studentId);
      node.values.add(recordId);
    } else {
      int i = 1;
      while (i < node.keys.size() - 1) {
        if (studentId > node.keys.get(i)) {
          i++;
        } else {
          break;
        }
      }

      node.keys.add(i, studentId);
      node.values.add(i, recordId);

    }
  }
}
