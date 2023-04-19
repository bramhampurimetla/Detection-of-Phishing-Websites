import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

public class RandomForest {

	JTreeFrame screen;

	int numAttributes; // The number of attributes including the output
						// attribute
	String[] attributeNames; // The names of all attributes. It is an array of
								// dimension numAttributes. The last attribute
								// is the output attribute

	/*
	 * Possible values for each attribute is stored in a vector. domains is an
	 * array of dimension numAttributes. Each element of this array is a vector
	 * that contains values for the corresponding attribute domains[0] is a
	 * vector containing the values of the 0-th attribute, etc.. The last
	 * attribute is the output attribute
	 */
	Vector[] domains;

	/* The root of the decomposition tree */
	TreeNode root = new TreeNode();
	DefaultMutableTreeNode guiRoot;

	public RandomForest(JTreeFrame screen) {
		this.screen = screen;
	}

	/*
	 * This function returns an integer corresponding to the symbolic value of
	 * the attribute. If the symbol does not exist in the domain, the symbol is
	 * added to the domain of the attribute
	 */
	public int getSymbolValue(int attribute, String symbol) {
		int index = domains[attribute].indexOf(symbol);
		if (index < 0) {
			domains[attribute].addElement(symbol);
			return domains[attribute].size() - 1;
		}
		return index;
	}

	/* Returns all the values of the specified attribute in the data set */
	public int[] getAllValues(Vector data, int attribute) {
		Vector values = new Vector();
		int num = data.size();
		for (int i = 0; i < num; i++) {
			DataPoint point = (DataPoint) data.elementAt(i);
			String symbol = (String) domains[attribute]
					.elementAt(point.attributes[attribute]);
			int index = values.indexOf(symbol);
			if (index < 0) {
				values.addElement(symbol);
			}
		}

		int[] array = new int[values.size()];
		for (int i = 0; i < array.length; i++) {
			String symbol = (String) values.elementAt(i);
			array[i] = domains[attribute].indexOf(symbol);
		}
		values = null;
		return array;
	}

	/*
	 * Returns a subset of data, in which the value of the specfied attribute of
	 * all data points is the specified value
	 */
	public Vector getSubset(Vector data, int attribute, int value) {
		Vector subset = new Vector();

		int num = data.size();
		for (int i = 0; i < num; i++) {
			DataPoint point = (DataPoint) data.elementAt(i);
			if (point.attributes[attribute] == value)
				subset.addElement(point);
		}
		return subset;

	}

	/*
	 * Calculates the entropy of the set of data points. The entropy is
	 * calculated using the values of the output attribute which is the last
	 * element in the array attribtues
	 */
	public double calculateEntropy(Vector data) {

		int numdata = data.size();
		if (numdata == 0)
			return 0;

		int attribute = numAttributes - 1;
		int numvalues = domains[attribute].size();
		double sum = 0;
		for (int i = 0; i < numvalues; i++) {
			int count = 0;
			for (int j = 0; j < numdata; j++) {
				DataPoint point = (DataPoint) data.elementAt(j);
				if (point.attributes[attribute] == i)
					count++;
			}
			double probability = 1. * count / numdata;
			if (count > 0)
				sum += -probability * Math.log(probability);
		}
		return sum;

	}

	/*
	 * This function checks if the specified attribute is used to decompose the
	 * data set in any of the parents of the specfied node in the decomposition
	 * tree. Recursively checks the specified node as well as all parents
	 */
	public boolean alreadyUsedToDecompose(TreeNode node, int attribute) {
		if (node.children != null) {
			if (node.decompositionAttribute == attribute)
				return true;
		}
		if (node.parent == null)
			return false;
		return alreadyUsedToDecompose(node.parent, attribute);
	}

	/*
	 * This function decomposes the specified node according to the J48
	 * algorithm. Recursively divides all children nodes until it is not
	 * possible to divide any further I have changed this code from my earlier
	 * version. I believe that the code in my earlier version prevents useless
	 * decomposition and results in a better decision tree! This is a more
	 * faithful implementation of the standard J48 algorithm
	 */
	public void decomposeNode(TreeNode node) {

		double bestEntropy = 0;
		boolean selected = false;
		int selectedAttribute = 0;

		int numdata = node.data.size();
		int numinputattributes = numAttributes - 1;
		node.entropy = calculateEntropy(node.data);
		if (node.entropy == 0)
			return;

		/*
		 * In the following two loops, the best attribute is located which
		 * causes maximum decrease in entropy
		 */
		for (int i = 0; i < numinputattributes; i++) {
			int numvalues = domains[i].size();
			if (alreadyUsedToDecompose(node, i))
				continue;
			// Use the following variable to store the entropy for the test node
			// created with the attribute i
			double averageentropy = 0;
			for (int j = 0; j < numvalues; j++) {
				Vector subset = getSubset(node.data, i, j);
				if (subset.size() == 0)
					continue;
				double subentropy = calculateEntropy(subset);
				averageentropy += subentropy * subset.size(); // Weighted sum
			}

			averageentropy = averageentropy / numdata; // Taking the weighted
														// average
			if (selected == false) {
				selected = true;
				bestEntropy = averageentropy;
				selectedAttribute = i;
			} else {
				if (averageentropy < bestEntropy) {
					selected = true;
					bestEntropy = averageentropy;
					selectedAttribute = i;
				}
			}

		}

		if (selected == false)
			return;

		// Now divide the dataset using the selected attribute
		int numvalues = domains[selectedAttribute].size();
		node.decompositionAttribute = selectedAttribute;
		node.children = new TreeNode[numvalues];
		for (int j = 0; j < numvalues; j++) {
			node.children[j] = new TreeNode();
			node.children[j].parent = node;
			node.children[j].data = getSubset(node.data, selectedAttribute, j);
			node.children[j].decompositionValue = j;
		}

		// Recursively divides children nodes
		for (int j = 0; j < numvalues; j++) {
			decomposeNode(node.children[j]);
		}

		// There is no more any need to keep the original vector. Release this
		// memory
		node.data = null; // Let the garbage collector recover this memory

	}

	/**
	 * Function to read the data file. The first line of the data file should
	 * contain the names of all attributes. The number of attributes is inferred
	 * from the number of words in this line. The last word is taken as the name
	 * of the output attribute. Each subsequent line contains the values of
	 * attributes for a data point. If any line starts with // it is taken as a
	 * comment and ignored. Blank lines are also ignored.
	 */
	public int readData(String filename) throws Exception {

		FileInputStream in = null;

		try {
			File inputFile = new File(filename);
			in = new FileInputStream(inputFile);
		} catch (Exception e) {
			System.err.println("Unable to open data file: " + filename + "\n"
					+ e);
			return 0;
		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in));

		String input;
		while (true) {
			input = bin.readLine();
			if (input == null) {
				System.err.println("No data found in the data file: "
						+ filename + "\n");
				return 0;
			}
			if (input.startsWith("//"))
				continue;
			if (input.equals(""))
				continue;
			break;
		}

		//StringTokenizer tokenizer = new StringTokenizer(input, ",");
		String[] tokens = input.split(",");
		numAttributes = tokens.length;//tokenizer.countTokens();
		if (numAttributes <= 1) {
			System.err.println("Read line: " + input);
			System.err
					.println("Could not obtain the names of attributes in the line");
			System.err
					.println("Expecting at least one input attribute and one output attribute");
			return 0;
		}

		domains = new Vector[numAttributes];
		for (int i = 0; i < numAttributes; i++)
			domains[i] = new Vector();
		attributeNames = new String[numAttributes];

		for (int i = 0; i < numAttributes; i++) {
			attributeNames[i] = tokens[i];//tokenizer.nextToken();
		}

		while (true) {
			input = bin.readLine();
			if (input == null)
				break;
			if (input.startsWith("//"))
				continue;
			if (input.equals(""))
				continue;

			//tokenizer = new StringTokenizer(input);
			tokens = input.split(",");
			int numtokens = tokens.length;//tokenizer.countTokens();
			if (numtokens != numAttributes) {
				System.err.println("Read " + root.data.size() + " data");
				System.err.println("Last line read: " + input);
				System.err
						.println("Expecting " + numAttributes + " attributes");
				return 0;
			}

			DataPoint point = new DataPoint(numAttributes);
			for (int i = 0; i < numAttributes; i++) {
				point.attributes[i] = getSymbolValue(i, tokens[i]/*tokenizer.nextToken()*/);
			}
			root.data.addElement(point);

		}

		bin.close();

		return 1;

	} // End of function readData

	// -----------------------------------------------------------------------

	/*
	 * This function prints the decision tree in the form of rules. The action
	 * part of the rule is of the form outputAttribute = "symbolicValue" or
	 * outputAttribute = { "Value1", "Value2", .. } The second form is printed
	 * if the node cannot be decomposed any further into an homogenous set
	 */
	public void printTree(TreeNode node, String tab,
			DefaultMutableTreeNode guiNode) {

		int outputattr = numAttributes - 1;

		if (node.children == null) {
			int[] values = getAllValues(node.data, outputattr);
			if (values.length == 1) {
				screen.append(tab + "\t" + attributeNames[outputattr]
						+ " is \"" + domains[outputattr].elementAt(values[0])
						+ "\"; ");
				guiNode.setUserObject(attributeNames[outputattr]);
				guiNode.add(new DefaultMutableTreeNode(domains[outputattr]
						.elementAt(values[0])));
				return;
			}
			screen.appendNoLine(tab + "\t" + attributeNames[outputattr]
					+ " is (");
			guiNode.setUserObject(attributeNames[outputattr]);
			for (int i = 0; i < values.length; i++) {
				screen.appendNoLine("\""
						+ domains[outputattr].elementAt(values[i]) + "\" ");
				if (i != values.length - 1)
					screen.appendNoLine(" , ");
				guiNode.add(new DefaultMutableTreeNode(domains[outputattr]
						.elementAt(values[i])));
			}
			screen.append(")");
			return;
		}

		int numvalues = node.children.length;
		guiNode.setUserObject(attributeNames[node.decompositionAttribute]);

		for (int i = 0; i < numvalues; i++) {

			screen.appendNoLine(tab + "if( "
					+ attributeNames[node.decompositionAttribute] + " is \""
					+ domains[node.decompositionAttribute].elementAt(i)
					+ "\") {");-

			DefaultMutableTreeNode tn = new DefaultMutableTreeNode(
					domains[node.decompositionAttribute].elementAt(i));
			DefaultMutableTreeNode tn2 = new DefaultMutableTreeNode();

			printTree(node.children[i], tab + " ", tn2);
			if (i != numvalues - 1)
				screen.appendNoLine(tab + "} \n\n");
			else
				screen.append(tab + "}");
			tn.add(tn2);
			guiNode.add(tn);
		}

	}

	/*
	 * This function creates the decision tree and prints it in the form of
	 * rules on the console
	 */
	public DefaultMutableTreeNode createDecisionTree() {

		guiRoot = new DefaultMutableTreeNode("DataSet");
		decomposeNode(root);
		printTree(root, "", guiRoot);

		return guiRoot;
	}

}
