
 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 import javax.swing.table.*;
 import java.util.*;
 import java.io.*;
 import java.sql.*;

 public class LoadData extends JFrame
                       implements ActionListener
 {
   JTable  jtData = new JTable();
   JButton bNext = new JButton("Proceed Constructing the Tree");
   JPanel  jpanel = new JPanel();

   String fnm = "dataset/DS1.txt";
   int    numAttributes = 0 ;

   public LoadData()
   {
      setLayout(new BorderLayout());
      setTitle("Loading Training Data");
      setBounds(20,20,400,400);

      jpanel.add(bNext);
      add(jpanel,"South");

      bNext.addActionListener(this);

      try{

        File  f = new File(fnm);

        BufferedReader buf = new BufferedReader(
                       new InputStreamReader(
                       new FileInputStream(f)));

        String line = buf.readLine();
        StringTokenizer s = new StringTokenizer(line,",");

        numAttributes = s.countTokens();

        DefaultTableModel tm = new DefaultTableModel();

        for (int i=0; i < numAttributes; i++) {
                String st = s.nextToken();
                tm.addColumn(st);
                System.out.println(st);
        }

        while(true) {

          line = buf.readLine();

          if(line==null) break;

          s = new StringTokenizer(line,",");

          Object o[] = new Object[numAttributes];

          for (int i=0; i < numAttributes; i++)
                o[i] = s.nextToken();

          tm.addRow(o);
        }

        jtData.setModel(tm);

        add(new JScrollPane(jtData));
      }
      catch(Exception e){
        System.out.println(e);
        System.exit(0);
      }

      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setVisible(true);
   }

   public void actionPerformed(ActionEvent ae)
   {
      new ProcessKit(fnm,numAttributes);
      dispose();
   }

 }
