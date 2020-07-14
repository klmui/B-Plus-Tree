import java.util.ArrayList;

class BTreeNode {

    /**
     * Array of the keys stored in the node.
     */
    //long[] keys;
    ArrayList<Long> keys;
    /**
     * Array of the values[recordID] stored in the node. This will only be filled when the node is a leaf node.
     */
    //long[] values;
    ArrayList<Long> values;
    /**
     * Minimum degree (defines the range for number of keys)
     **/
    int t;
    /**
     * Pointers to the children, if this node is not a leaf.  If
     * this node is a leaf, then null.
     */
    //BTreeNode[] children;
    ArrayList<BTreeNode> children;
    /**
     * number of key-value pairs in the B-tree
     */
    int n;
    /**
     * true when node is leaf. Otherwise false
     */
    boolean leaf;

    /**
     * point to other next node when it is a leaf node. Otherwise null
     */
    BTreeNode next;

    // Constructor
    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        
        this.keys = new ArrayList<>();
        //this.keys = new long[2 * t - 1];
        //this.children = new BTreeNode[2 * t];
        this.children = new ArrayList<>();
        for (int i = 0; i <= (2 * t); i++) { // initialize array with nulls
          this.children.add(null);
        }
        
        this.n = 0;
        this.next = null;
        //this.values = new long[2 * t - 1];
        this.values = new ArrayList<>();
        for (int i = 0; i <= (2 * t - 1); i++) { // initialize array with nulls
          this.values.add(null);
        }
    }
    
    /**
     * 
     * @return The number of child nodes this node has
     */
    public int getNumChildren() {
      
      return this.children.size();
    }
    
    /**
     * 
     * @return The number of keys this node has
     */
    public int getNumKeys() {
      
      return this.keys.size();
    }
    
    public ArrayList<Long> getKeys(){
      return this.keys;
    }
}
