
 import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

 public class JTreeFrame extends JFrame {

   JTextArea ta = new JTextArea();

   DefaultMutableTreeNode root;
   public JTreeFrame(String fnm) {

    JTree jt = null;

    try {
    RandomForest me = new RandomForest(this);

    long startTime = System.currentTimeMillis();    //  To print the time taken to process the data

    int status = me.readData(fnm);
    if (status <= 0) return;

    root = me.createDecisionTree();
    jt = new JTree(root);

    long endTime = System.currentTimeMillis();
    long totalTime = (endTime-startTime)/1000;

    append("\n" + totalTime + " Seconds");

    }
    catch(Exception e) {
     append("Exception:\n\n" + e);
    }


    JTabbedPane tbp = new JTabbedPane();

    tbp.add("Tree",new JScrollPane(jt));
    tbp.add("Rules",new JScrollPane(ta));
    tbp.add("Test",new TestData(root));

    add(tbp);

    setTitle("Finla Decision Tree: ");
    setBounds(20,20,500,500);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);


   }

   public void append(String line) {
    ta.append("\n" + line);
   }

   public void appendNoLine(String line) {
    ta.append(line);
   }
 }
