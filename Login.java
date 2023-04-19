import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.*;

public class Login extends JFrame implements ActionListener
{
	JTextField untf;
	JPasswordField pwpf;
	JLabel unl,pwl,imgl;
	JButton Next,Close;
	Image img;
	ImageIcon ic;
	public Login()
	{
	try
	     {
	          img=ImageIO.read(getClass().getResource("/10.jpg"));
	          ic=new ImageIcon(img);
	          imgl=new JLabel(ic,JLabel.CENTER);
                             addc(imgl,10,20,140,150);	

        
               	         setLayout(null);
	         unl=new JLabel("User Name : ");
	         untf=new JTextField(15);
	         untf.setFont(new Font("Times New Roman",Font.BOLD,12));

	         addc(unl,160,40,100,25);
	         addc(untf,240,40,150,25);
	        
   	         pwl=new JLabel("PassWord : ");
	         pwpf=new JPasswordField(15);
	         pwpf.setEchoChar('*');
	         pwpf.setFont(new Font("Times New Roman",Font.BOLD,12));
	         
                            addc(pwl,160,80,100,25);
	         addc(pwpf,240,80,150,25);

	         Next=new JButton("Next");
	         Next.setMnemonic('N');
	         Next.addActionListener(this);

	         addc(Next,170,140,100,25);
	        

	         Close=new JButton("Close");
	         Close.setMnemonic('C');
	         Close.addActionListener(this);
	         
	         addc(Close,280,140,100,25);
                            
 	         setTitle("Login Screen");
	         setVisible(true);
	         setSize(400,250);
	         setResizable(false);
	         setDefaultCloseOperation(EXIT_ON_CLOSE);
	         show();
	         
                        }catch(Exception e)
	      {
	       System.out.println("Exception in Constructor:"+e);
	       System.exit(0);
	      }
	}
public void addc(JComponent c,int x,int y,int w,int h)
	{
	c.setBounds(x,y,w,h);
	add(c);
	}
public void actionPerformed(ActionEvent ae)
	{
	JButton b=(JButton)  ae.getSource();
	if(b==Close)
                    System.exit(0);
                   if(b==Next)
	{
	       if((untf.getText().equals("admin"))&&(pwpf.getText().equals("admin")))	
	       {
	             	    new LoadData();
	       	dispose();
	         }
	       else
	      {
                JOptionPane.showMessageDialog(this,"Invalid User Name or Password","Access Denied",JOptionPane.ERROR_MESSAGE);
	       }
	}
         }
public static void main(String args[])
	{
		new Login();
	}
}
