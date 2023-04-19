import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class TestData extends JPanel implements ActionListener {

	JTable jtData = new JTable();
	JButton bNext = new JButton("Load Test Data");
	JButton bA = new JButton("Analyse");
	JPanel jpanel = new JPanel();

	DefaultTableModel tm;
	int numAttributes = 0;

	JButton b = new JButton("Close");

	DefaultMutableTreeNode tree;

	public TestData(DefaultMutableTreeNode tree) {
		setLayout(new BorderLayout());

		this.tree = tree;

		jpanel.add(bNext);
		jpanel.add(bA);
		jpanel.add(b);
		add(jpanel, "South");

		bNext.addActionListener(this);
		bA.addActionListener(this);
		b.addActionListener(this);
		add(new JScrollPane(jtData));
		setVisible(true);
		setBounds(20, 20, 700, 500);

	}

	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == b) {
			System.exit(0);
		} else if (ae.getSource() == bA) {
			for (int i = 0; i < tm.getRowCount(); i++) {
				String result = analyse(i, tree);
				tm.setValueAt(result, i, tm.getColumnCount() - 1);
			}
		} else {

			try {

				JFileChooser fch = new JFileChooser("dataset/");

				if (fch.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
					return;

				File f = fch.getSelectedFile();

				BufferedReader buf = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));

				String line = buf.readLine();
				String[] s = line.split(",");

				numAttributes = s.length;

				tm = new DefaultTableModel();

				for (int i = 0; i < numAttributes; i++) {
					String st = s[i];
					tm.addColumn(st);
					System.out.println(st);
				}
				tm.addColumn("Result");

				while (true) {

					line = buf.readLine();

					if (line == null)
						break;

					s = line.split(",");
					tm.addRow(s);
				}

				jtData.setModel(tm);

			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
		}

	}

	public String analyse(int rowid, DefaultMutableTreeNode root) {
		String result = "NA";

		if (root.getChildCount() == 1
				&& ((DefaultMutableTreeNode) root.getChildAt(0))
						.getChildCount() == 0)
			result = ((DefaultMutableTreeNode) root.getChildAt(0))
					.getUserObject().toString();
		else {
			String attrib = root.getUserObject().toString();
			String value = "";
			for (int i = 0; i < tm.getColumnCount() - 1; i++) {
				System.out.println(tm.getColumnName(i));
				System.out.println(attrib);
				if (tm.getColumnName(i).equals(attrib)) {
					value = (String) tm.getValueAt(rowid, i);
					break;
				}
			}
			if (value.equals("")) {
				System.out.println("leased");
				Random r = new Random(tm.getRowCount());
				return r.nextInt(100) < 50 ? "1" : "0";
			} else {
				for (int i = 0; i < root.getChildCount(); i++) {
					DefaultMutableTreeNode child = ((DefaultMutableTreeNode) root
							.getChildAt(i));
					if (child.getUserObject().equals(value)) {
						try {
							result = analyse(rowid,
									(DefaultMutableTreeNode) child
											.getChildAt(0));
						} catch (Exception ex) {
							Random r = new Random(tm.getRowCount());
							result= r.nextInt(100) < 50 ? "1" : "0";
						}
						break;
					}
				}
			}
		}

		return result;
	}
}
