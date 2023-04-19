
 import java.awt.*;
 import javax.swing.*;
 import java.awt.event.*;

 class ProcessKit extends JDialog implements ActionListener {

   JProgressBar pbStatus = new JProgressBar();

   JLabel l1 = new JLabel("Please Wait While Preparing..");
  
   Timer  jt;

   String fnm;

   ProcessKit(String fnm,int count) {

    this.fnm = fnm;

    setTitle("Please Wait..!");

    add(l1,"North");
    add(pbStatus);

    setResizable(false);
    setBounds(50,50,375,100);
    setVisible(true);

    jt = new Timer(400,this);

    pbStatus.setMinimum(0);
    pbStatus.setMaximum(count);
    pbStatus.setValue(0);
    pbStatus.setStringPainted(true);
   
    jt.start();
   }

   public void actionPerformed(ActionEvent ae) {

          pbStatus.setValue(pbStatus.getValue()+1);

          String pr = Math.round(pbStatus.
                      getPercentComplete()*100) + " %";

          pbStatus.setString(pr);

          if(pbStatus.getValue()==pbStatus.getMaximum())
          {
           jt.stop();
           new JTreeFrame(fnm);
           dispose();
          }    
   }
 }
