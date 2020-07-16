/**
 * Do NOT modify. This is the class with the main function
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

  // index in searchNode with the element
  // check method sees if a given node meets the minimum requirements for cap by ensureCapacity
  // method
  private int findIndex(BTreeNode current, long studentId) {
    // BTreeNode nodeWithId = searchNode(root, studentId);
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

    // if the root is null
    if (this.root == null) {
      this.root = new BTreeNode(t, true);
      this.root.keys.add(0, student.studentId);
      this.root.values.add(0, student.recordId);

      insertToCSV("Student.csv", student);

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
      System.out.println("Writing to CSV");
      insertToCSV("Student.csv", student);

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

  boolean delete(long studentId) {
    /**
     * TODO: Implement this function to delete in the B+Tree. Also, delete in student.csv after
     * deleting in B+Tree, if it exists. Return true if the student is deleted successfully
     * otherwise, return false.
     */
    // search to see if the node exists: search(BTreeNode current, long studentId)
    long exists = search(studentId);
    BTreeNode nodeWithID = searchNode(root, studentId);

    // String filePath = "Student.csv";
    // System.out.println("filePath: " + filePath);
    // writeCSV(filePath, studentId);

    if (exists == -1) { // student id is not found
      return false;
    }

    // start at the root and compare studentId to the one we want.
    // if key doesn't exist in the root, which child tree to go to.
    if (this.root != null && this.root.leaf) {
      // find studentId and remove from b-tree
      int index = root.keys.indexOf(studentId);
      root.keys.remove(index);


      deleteFromCSV("Student.csv", studentId);

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
    return true;
  }


  /**
   * Delete node with studentId and keep structure of tree.
   * 
   * @param node
   * @param studentID
   * @param           index: index of pointer
   */
  private void deleteHelper(BTreeNode node, long studentID) {
    BTreeNode current = node;

    // base case: current node is the root, so we are done
    if (node.equals(root)) {
      return;
    }

    // base case: leaf node - found the node that contains the studentID as a key
    if (current.leaf == true) {
      // find studentID
      int indexInLeaf = current.keys.indexOf(studentID);
      // delete it
      current.keys.remove(indexInLeaf);
      deleteFromCSV("Student.csv", studentID);
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
        for (long key : rightNode.keys) {
          node.keys.add(key);
        }
        // we reassign the root now to be this value:
        this.root = node;
        return;
      } else { // the parent is another internal node or a root with more than 1 key

        // set the value of the node's pointer to be the rightNode pointer (30, 30 instead of 27,
        // 30)
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
    if (leftNode != null) { // move entries from rightmost node into the left node
      BTreeNode parent = this.findParent(root, node);
      int oldParentIndex = findPrePtrIndex(node);
      int leftParentIndex = oldParentIndex - 1;

      BTreeNode leftNodeNext = parent.children.get(leftParentIndex); // left node

      if (parent.equals(root) && (root.getNumKeys() == 1 || root.getNumChildren() <= 2)) {

        long rootKey = root.keys.get(0); // this is the only element in the root
        // our parent is the root:
        // merging will lead to problems with just 1 child
        // we need to reassign our root
        leftNodeNext.keys.add(rootKey); // we add this root there (in terms of order from least to
                                        // greatest)

        for (long key : node.keys) {
          leftNodeNext.keys.add(key);
        }

        // we reassign the root now to be this value:
        this.root = leftNodeNext;
        return;
      } else { // the parent is another internal node or a root with more than 1 key

        for (long key : node.keys) {
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
    int i = 0;
    // First check is making sure loop is within bounds and the second one is finding the index that
    // belongs to the studentId
    // or 1 greater than it
    while ((i + 1 <= node.keys.size()) && (studentId >= node.keys.get(i))) {
      i++;
    }

    return i;
  }


  private int findPrePtrIndex(BTreeNode node) {
    BTreeNode parent = this.findParent(this.root, node);
    long studentId = node.keys.get(0);
    int i = 0;
    // First check is making sure loop is within bounds and the second one is finding the index that
    // belongs to the studentId
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


  /**
   * Please note that this method goes to the bottom left and finds the leftmost leaf node (the
   * smallest leaf node) in the BTree. If there is only 1 leaf node (the root is the leaf), this
   * returns the root
   * 
   * @return
   */
  private BTreeNode findLeftMostLeafNode() {
    System.out.println("findLeftMostLeafNode()");
    // the root is the only leaf node so we return this
    if (root.getNumChildren() == 0 || root.leaf == true) {
      return root;
    } else {
      // the root has children, so we keep going left (index 0):
      BTreeNode current = root.children.get(0);
      while (current.leaf == false) {
        current = current.children.get(0);
      }
      return current;
    }


  }


  /**
   * Please note that this method writes out the Btree to a CSV. It only skips over the student id
   * that is to be deleted if there is one StudentIDToDelete is -1 is we do not want to delete
   * anything from the Tree Otherwise, it will have the Student ID to Delete
   */
  private void writeCSV(String filePath, long StudentIDToDelete) {
    try {
      FileWriter csvWriter = new FileWriter(filePath);

      csvWriter.append("StudentID");
      csvWriter.append(","); // comma-separated
      csvWriter.append("RecordID");
      csvWriter.append("\n");
      // please go all the way to the bottom left of the tree
      // all entries (and student records) will be in leaf node of B+ tree
      // the left most node has the start
      BTreeNode leftmostLeafNode = findLeftMostLeafNode();
      // if the root is the only node (and is therefore also the leaf node)

      if (leftmostLeafNode.equals(root)) {
        for (int i = 0; i < root.getNumKeys(); i++) {
          long studentID = root.keys.get(i);
          if (studentID == StudentIDToDelete) { // we skip writing out this entry
            continue;
          } else {
            long recordID = root.values.get(i);
            char studentIdChar = (char) studentID;
            char recordIdChar = (char) recordID;
            csvWriter.append(studentIdChar);
            csvWriter.append(",");
            csvWriter.append(recordIdChar);
            csvWriter.append("\n");
          }

        }
      } else {
        BTreeNode current = leftmostLeafNode; // we start with this leftmost node and then keep
                                              // going to next
        while (current.next != null) {
          for (int i = 0; i < current.keys.size(); i++) {
            long studentID = root.keys.get(i);
            if (studentID == StudentIDToDelete) { // we skip writing out this entry
              continue;
            } else {
              long recordID = root.values.get(i);
              char studentIdChar = (char) studentID;
              char recordIdChar = (char) recordID;
              csvWriter.append(studentIdChar);
              csvWriter.append(",");
              csvWriter.append(recordIdChar);
              csvWriter.append("\n");
            }
          }
          current = current.next; // we go to the next leaf node (the right node)

        }
      }
      csvWriter.close();
      System.out.println(
          ":) Please note that we have written the (studentID, recordID) results to: " + filePath);
    } catch (Exception e) {
      System.out
          .println("Error :( Please note we were unable to write the B+ Tree out to: " + filePath);

      System.out.println(e.getStackTrace());
    }
  }

  private void insertToCSV(String filepath, Student student) {
    System.out.println("INSERTED STUDENT");
    try {
      FileWriter csvWriter = new FileWriter(filepath, true);

      long studentId = student.studentId;
      long recordId = student.recordId;
      int age = student.age;
      String studentName = student.studentName;
      String major = student.major;
      String level = student.level;

      csvWriter.append(Long.toString(studentId));
      csvWriter.append(",");
      csvWriter.append(studentName);
      csvWriter.append(",");
      csvWriter.append(major);
      csvWriter.append(",");
      csvWriter.append(level);
      csvWriter.append(",");
      csvWriter.append(Integer.toString(age));
      csvWriter.append(",");
      csvWriter.append(Long.toString(recordId));
      csvWriter.append("\n");

      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write all data from csv file to a list and removes row with the studentId. Then, it inserts
   * everything back to the csv file.
   * 
   * @param filepath
   * @param studentId
   */
  private void deleteFromCSV(String filepath, long studentId) {
    System.out.println("DELETED STUDENT");
    List<Student> studentList = new ArrayList<>();



    File studentFile = new File("Student.csv");



    if (studentFile.canRead()) {
      try {
        BufferedReader studentReader = new BufferedReader(new FileReader(studentFile));
        String row;
        while ((row = studentReader.readLine()) != null) { // read each row of Student.csv
          String[] data = row.split(","); // split the data in the row by comma



          // save each value (ID:long, age:int, name:String, major:String, level:String, recordID:
          // long)
          long id = Long.parseLong(data[0]);
          String name = data[1];
          String major = data[2];
          String level = data[3];
          int age = Integer.parseInt(data[4]);
          long recordId = Long.parseLong(data[5]);



          // create a new student and add it to studentList
          Student stu = new Student(id, age, name, major, level, recordId);

          if (stu.studentId == studentId) {
            continue;
          }
          studentList.add(stu);
        }



        studentReader.close();

        // clear the file
        // after reading all of the lines in the Students.csv file in a buffer, delete the
        // contents of the file so that it can be easily re-populated.
        if (studentFile.exists() && studentFile.isFile()) {
          studentFile.delete();
        }

        studentFile.createNewFile();

        // Write back to csv file
        for (Student student : studentList) {
          this.insertToCSV(filepath, student);
        }



      } catch (IOException e) {
        System.out.println("Cannot find Student.csv or cannot read a row.");
        // return null;
      }
    }
  }
}


