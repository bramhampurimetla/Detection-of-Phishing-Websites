
        import java.util.Vector;

        class TreeNode {
		public double entropy;			// The entropy of data points if this node is a leaf node
		public Vector data;			// The set of data points if this is a leaf node
                public int decompositionAttribute;      // If this is not a leaf node, the attribute that is used to divide the set of data points
		public int decompositionValue;		// the attribute-value that is used to divide the parent node
		public TreeNode []children;		// If this is not a leaf node, references to the children nodes
		public TreeNode parent;			// The parent to this node.  The root has parent == null

		public TreeNode() {
			data = new Vector();
		}

        }
