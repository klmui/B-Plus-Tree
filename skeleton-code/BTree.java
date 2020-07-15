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

  private BTreeNode searchNode(BTreeNode current, long studentId) {

    // check whether the current node has the key
    // if the current node isn't a leaf node, find the child node to move to.
    if (!current.leaf) {
      int i = 1;
      while (i <= current.getNumKeys() && studentId > current.keys.get(i - 1)) {
        i++;
      }
      return searchNode(current.children.get(i - 1), studentId);
    } else { // if it is a leaf, the id doesn't exist
      return null;
    }
  }
  
  private BTreeNode nodeWithID(long studentId) {
    // iterate through the BTree and see if the studentId is in that leaf node
    
    //  if exists() then we do this, else we return null
    // we go all the way to the left most leaf node.  then check 
    // and keep going to next, until we find the node with the student id
    // we call the searchNode for each o
    BTreeNode nodeWithId = searchNode(root, studentId);
    return nodeWithId;
    
    
  }
  
  // index in searchNode with the element
  // check method sees if a given node meets the minimum requirements for cap by ensureCapacity method
  private int findIndex(BTreeNode current, long studentId) {
    //BTreeNode nodeWithId = searchNode(root, studentId);
    for (int i = 0; i < current.keys.size(); i++) {
      if (current.keys.get(i) == studentId) {
        return i;
      }
    }
    return -1;
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
    BTreeNode nodeWithID = searchNode(root, studentId);
    
    
    
    if (exists == -1) { // student id is not found
      return false;
    }

    // start at the root and compare studentId to the one we want.
    // if key doesn't exist in the root, which child tree to go to.
    if (this.root != null && this.root.leaf) {
      // find studentId and remove from b-tree
      int index = root.keys.indexOf(studentId);
      root.keys.remove(index);
      
      // TODO: remove from csv
      
      return true;
    } 

    BTreeNode currentNode = this.root;
    
    while ((!currentNode.equals(nodeWithID) && (!currentNode.leaf))) {
      int pointerIndex = findPtrIndexGivenAnId(currentNode, studentId);
      BTreeNode childNode = currentNode.children.get(pointerIndex);
      currentNode = childNode;
      
    }
    
    deleteHelper(currentNode, studentId);
    
//    if (!this.root.leaf) { // if the studentId is in an internal node, find the subtree
//      int i = 0;
//      // First check is making sure loop is within bounds and the second one is finding the index that belongs to the studentId
//      // or 1 greater than it
//      while ((i + 1 <= root.keys.size()) && (studentId >= root.keys.get(i))) {
//        i++;
//      }
      
      // recursive helper
      // Could be i or i - 1
//      deleteHelper(c, studentId);
//    }
    
//    BTreeNode current = root;
//    // go down the tree to find the leaf node, starting from the leaf:
//    boolean studentIdFound = false; 
//    int indexOfKeyToDelete; 
//    if (current.leaf == true){ // we are in the leaf node
//      for (int i = 0; i < current.keys.size(); i++) {
//        long leafVal = current.keys.get(i);
//        if (leafVal == studentId) {
//          studentIdFound = true;
//          indexOfKeyToDelete = i; 
//          break;
//        }
//      }
//    } else { // we are not at the leaf node and need to keep going down 
//      ArrayList<Long> listOfEntries = current.keys;
//
//      boolean firstItIsLessThan = false; 
//      int indexToUse; 
//      int i = 0;
//      while ((i + 1 <= listOfEntries.size()) && (studentId >= listOfEntries.get(i))) {
//        i++;
//      }
//      // we found the index that we should go down on
//      
//      
//    }
    
    return true;
  }
  
  
  /**
   * Delete node with studentId and keep structure of tree.
   * 
   * @param node
   * @param studentID
   * @param index: index of pointer
   */
  private void deleteHelper(BTreeNode node, long studentID) {
    BTreeNode current = node;
    
    // base case:  current node is the root, so we are done
    if(node.equals(root)) {
      return;
    }
    
    // base case: leaf node - found the node that contains the studentID as a key
    if (current.leaf == true) {
      // find studentID
      int indexInLeaf = current.keys.indexOf(studentID);
      // delete it
      current.keys.remove(indexInLeaf);
    }
    
    // ensure node meets min occupancy req
    if (ensureCapacity(node)) {
      return;
    }
      
      
    // min occupancy rate isn't met
    // see about redistribution:
    
    // 1st attempt: Look right and try to take keys
    BTreeNode rightNode = this.getRightSibling(node);
    if (rightNode != null && this.ensureCapacity(rightNode)) {
      // Take keys from right node
      long firstStudentIdInRightNode = rightNode.keys.get(0);
      rightNode.keys.remove(0);
      node.keys.add(firstStudentIdInRightNode);
      
      // Update parent node
      BTreeNode parent = this.findParent(root, node);
      int parentPtrIndex = this.findPrePtrIndex(node);
      parent.keys.set(parentPtrIndex, firstStudentIdInRightNode);
      
      return;
    }
    
    // 2nd attempt: Look left and try to take keys
    BTreeNode leftNode = this.getLeftSibling(node);
    if (leftNode != null && this.ensureCapacity(leftNode)) {
      // Take keys from right node
      long firstStudentIdInLeftNode = leftNode.keys.get(0);
      leftNode.keys.remove(0);
      node.keys.add(0, firstStudentIdInLeftNode);
      
      // Update parent node
      BTreeNode parent = this.findParent(root, node);
      int parentPtrIndex = this.findPrePtrIndex(node);
      parent.keys.set(parentPtrIndex, firstStudentIdInLeftNode);
      
      return;
    }
    
    // 3rd attempt: merge right node into left node
    if (rightNode != null) {
        BTreeNode parent = this.findParent(root, node);
        int oldParentIndex = findPrePtrIndex(node);
        
        // Store next ref ptr from rightNode and update node's next ptr
        BTreeNode rightNodeNext = rightNode.next;
        node.next = rightNodeNext;
        int newParentIndex = findPrePtrIndex(rightNode);
        
        if (parent.equals(root) && (root.getNumKeys() == 1 || root.getNumChildren() <= 2)) {
          // RED0 THE ROOT AND UPDATE IT
          // we are an internal node and we have a right node we are merging on
          // Bring down key from root into oldParentIndex and merge, and reassign root

          long rootKey = root.keys.get(0); // this is the only element in the root
          // our parent is the root:
          // merging will lead to problems with just 1 child
          // we need to reassign our root
          node.keys.add(rootKey); // we add this root there (in terms of order from least to greatest)
          
          // Left: node values, Middle: root key, Right: right node values
          for (long key: rightNode.keys) {
            node.keys.add(key);
          }
          // we reassign the root now to be this value:
          this.root = node;
          return;
        } else { // the parent is another internal node or a root with more than 1 key 
        
          // set the value of the node's pointer to be the rightNode pointer (30, 30 instead of 27, 30)
          parent.keys.set(oldParentIndex, parent.keys.get(newParentIndex));
          
          // Putting all values from right node into node
          for (long key : rightNode.keys) {
            node.keys.add(key);
          }
          
          rightNode = null;
          
          // Remove parent with node
          parent.keys.remove(newParentIndex);
          
          // Keep structure of tree
          if (ensureCapacity(parent)) {
            return;
          } else {
            deleteHelper(parent, studentID);
          }
          
          return;
        }
    }
      
    // 4th attempt: merge left node into right node
    if (leftNode != null) {  // move entries from rightmost node into the left node
        BTreeNode parent = this.findParent(root, node);
        int oldParentIndex = findPrePtrIndex(node);
        int leftParentIndex = oldParentIndex - 1;
   
        BTreeNode leftNodeNext = parent.children.get(leftParentIndex); // left node
      
      if (parent.equals(root) && (root.getNumKeys() == 1 || root.getNumChildren() <= 2)) {
        
        long rootKey = root.keys.get(0); // this is the only element in the root
        // our parent is the root:
        // merging will lead to problems with just 1 child
        // we need to reassign our root
        leftNodeNext.keys.add(rootKey); // we add this root there (in terms of order from least to greatest)
        
        for (long key: node.keys) {
          leftNodeNext.keys.add(key);
        }
       
        // we reassign the root now to be this value:
        this.root = leftNodeNext;
        return;
      } else { // the parent is another internal node or a root with more than 1 key        
      
        for (long key: node.keys) {
          leftNodeNext.keys.add(key);
        }
        node = null;
        
        // Remove parent with node
        parent.keys.remove(leftParentIndex);
        
        // Keep structure of tree
        if (ensureCapacity(parent)) {
          return;
        } else {
          deleteHelper(parent, studentID);
        }
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
  
  /**
   * This checks to see if a given node has met the capacity constraints
   * 
   * 
   */
  private boolean ensureCapacity(BTreeNode node) {
    int numOfKeysInNode = node.keys.size();
    int minNumOfSpots = t;
    if (numOfKeysInNode >= minNumOfSpots) {
      return true;
    }
    return false; 
  }
  
  private BTreeNode getLeftSibling(BTreeNode node) {
    BTreeNode parent = this.findParent(this.root, node);
    int index = findPrePtrIndex(node);
    
    try {
      return parent.children.get(index - 1);
    } catch (Exception e) {
      return null;
    }
  }
  
  private BTreeNode getRightSibling(BTreeNode node) {
   BTreeNode parent = this.findParent(this.root, node);
   int index = findPrePtrIndex(node);
      
    try {
      return parent.children.get(index + 1);
    } catch (Exception e) {
      return null;
    }
  }
  
  
  private int findPtrIndexGivenAnId(BTreeNode node, long studentId) {
    BTreeNode parent = this.findParent(this.root, node);
    int i = 0;
    // First check is making sure loop is within bounds and the second one is finding the index that belongs to the studentId
    // or 1 greater than it
    while ((i + 1 <= parent.keys.size()) && (studentId >= parent.keys.get(i))) {
      i++;
    }
    
    return i;
  }
  
  
  private int findPrePtrIndex(BTreeNode node) {
    BTreeNode parent = this.findParent(this.root, node);
    long studentId = node.keys.get(0);
    int i = 0;
    // First check is making sure loop is within bounds and the second one is finding the index that belongs to the studentId
    // or 1 greater than it
    while ((i + 1 <= parent.keys.size()) && (studentId >= parent.keys.get(i))) {
      i++;
    }
    
    return i;
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
