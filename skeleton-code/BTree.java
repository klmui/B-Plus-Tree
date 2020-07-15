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

    BTreeNode foundNode = searchNode(this.root, studentId);

    if (foundNode == null) {
      System.out.println("The provided ID has not been found in the table.");
      return -1;
    } else {
      int index = foundNode.keys.indexOf(studentId);
      return foundNode.values.get(index);
    }


    // return -1;
  }

  private BTreeNode searchNode(BTreeNode current, long studentId) {

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
      return searchNode(current.children.get(i - 1), studentId);
    } else { // if it is a leaf, the id doesn't exist
      return null;
    }

    // return null;
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
    // search to see if the node exists:  search(BTreeNode current, long studentId)
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

  private BTreeNode split(BTreeNode node) {
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
