	/*  The class to represent a data point consisting of numAttributes values of attributes  */
	class DataPoint {

		/* The values of all attributes stored in this array.  i-th element in this array
		   is the index to the element in the vector domains representing the symbolic value of
		   the attribute.  For example, if attributes[2] is 1, then the actual value of the
		   2-nd attribute is obtained by domains[2].elementAt(1).  This representation makes
		   comparing values of attributes easier - it involves only integer comparison and
		   no string comparison.
		   The last attribute is the output attribute
		*/
		public int []attributes;

		public DataPoint(int numattributes) {
			attributes = new int[numattributes];
		}
        }
