import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.SwingWorker;

class S4 extends Frame implements ActionListener {
    Label l1, l2;
    TextArea t1, t2;
    Button b1, b2;
    SwingWorker<Void, String> serverWorker; 
    Socket clientSocket; 
    DataOutputStream dos; 

    S4() {
        super("Server");
        l1 = new Label("From Client");
        l2 = new Label("To Client");

        t1 = new TextArea("", 10, 100, TextArea.SCROLLBARS_BOTH);
        t2 = new TextArea("", 10, 100, TextArea.SCROLLBARS_BOTH);

        b1 = new Button("Send");
        b2 = new Button("Exit");

        b1.addActionListener(this);
        b2.addActionListener(this);

        setLayout(null);

        l1.setBounds(50, 50, 100, 20);
        t1.setBounds(50, 80, 200, 100);
        l2.setBounds(50, 190, 100, 20);
        t2.setBounds(50, 220, 200, 100);
        b1.setBounds(50, 330, 75, 20);
        b2.setBounds(150, 330, 75, 20);

        setSize(300, 400);

        add(l1);
        add(l2);
        add(t1);
        add(t2);
        add(b1);
        add(b2);

        t1.setEditable(false);
        setVisible(true);

        serverWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                ServerSocket ss = new ServerSocket(5055);
                while (true) {
                    clientSocket = ss.accept();
                    dos = new DataOutputStream(clientSocket.getOutputStream());
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    while (true) 
		    {
                	String str = dis.readUTF();
                	publish(str); // Send the received data to the process method
               	    } 
                }
            }

            @Override
        protected void process(java.util.List<String> chunks) {
       		for (String str : chunks) {
       		 String currentText = t1.getText();
        	currentText = currentText + "\n" + str;
        	t1.setText(currentText);
    		}
	}

       };

        serverWorker.execute(); // Start the SwingWorker
    }

    public void actionPerformed(ActionEvent e) {
        Button b = (Button) e.getSource();
        if (b == b2) {
            System.exit(0);
        } else {
            String str = t2.getText();
            try {
                dos.writeUTF(str); // Send the 'str' to the connected client
                t2.setText(""); // Clear the input field after sending
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
}
	
		
		

class LoginData implements Serializable
{
 String id;
 String pwd;
	
 LoginData()
 {
  id = "";
  pwd = "";
 }
	
 LoginData(String a, String b)
 {
  id = a;
  pwd = b;
 }
	
 static boolean isValidPassword(String str)
 {
  int n = str.length();
  if(n<8)
   return false;
  int i=0;
  int uc = 0;
  int lc = 0;
  int dc = 0;
  int sc = 0;
  while(i<n)
  {
   char ch = str.charAt(i); // return ith pos char from string
   if(ch>=65 && ch<=90)
    uc++;
   else 
   {
    if(ch>=97 && ch<=122)
     lc++;
    else
    {
     if(ch>=48 && ch<=57)
      dc++;
     else
      sc++;
    }
   }
   i++;
  }
  return (uc>0 && lc>0 && dc>0 && sc>0);
 }
}

class IPD
{
 LinkedList<LoginData>ls;
 LoginData obj;
	
 IPD()
 {
  ls = new LinkedList<LoginData>();
 }
	
 void load()
 {
  FileInputStream fis = null;
  ObjectInputStream ois = null;
  try
  {
   fis = new FileInputStream("login.dat");
   ois = new ObjectInputStream(fis);
   ls = (LinkedList<LoginData>)ois.readObject(); // copying file data to ll
   ois.close();
   fis.close();
  }
  catch(Exception e) {}
 }
	
 void save()
 {
  FileOutputStream fos = null;
  ObjectOutputStream oos = null;
  try
  {
   fos = new FileOutputStream("login.dat");
   oos = new ObjectOutputStream(fos);
   oos.writeObject(ls); // writing ll to the file
   oos.close();
   fos.close();
  }
  catch(Exception e) {}
 }
	
 void search(String sid)
 {
  int i = 0, n = ls.size();
  while(i<n)
  {
   obj = ls.get(i);
   if((sid.equals(obj.id)) == true)
     break;
   else
    i++;
  }
  if(i==n)
   obj = null; // record not found
 }
	
 boolean isValidUser(String id)
 {
  search(id);
  if(obj==null)
   return false;
  if(id.equals(obj.id))
   return true;
  else
   return false;
 }
	
 boolean isValidUserPassword(String id, String pw)
 {
  search(id);
  if(obj==null)
   return false;
  if(id.equals(obj.id) && pw.equals(obj.pwd))
   return true;
  else
   return false;
 }
}

class NewUser extends JDialog implements ActionListener, FocusListener
{
 JLabel l1, l2, l3, l4, l5;
 JTextField t1, t2, t5;
 JPasswordField t4;
 JButton b1, b2;
 Choice year;
 String fnm, lnm, byear;
 String id;
 IPD upobj; // to make ll available
	
 NewUser(JFrame frm, String title, boolean state, IPD aobj)
 {
  super(frm, title, state);
  
  upobj = aobj; 
  
  l1 = new JLabel("First name");
  l2 = new JLabel("Last name");
  l3 = new JLabel("Birth year");
  l4 = new JLabel("Type password");
  l5 = new JLabel("Retype password");
  t1 = new JTextField(20);
  t2 = new JTextField(20);
  t4 = new JPasswordField();
  t5 = new JTextField(20);
  year = new Choice();
  
  for(int i=1923; i<=2023; i++)
   year.add(""+i);
  
  t1.addFocusListener(this);
  t2.addFocusListener(this);
  t4.addFocusListener(this);
  t5.addFocusListener(this);
  b1 = new JButton("Add");
  b2 = new JButton("Back");
  
  b1.addActionListener(this);
  b2.addActionListener(this);
  
  setLayout(new GridLayout(6, 2, 5, 5));
  
  add(l1);
  add(t1);
  add(l2);
  add(t2);
  add(l3);
  add(year);
  add(l4);
  add(t4);
  add(l5);
  add(t5);
  add(b1);
  add(b2);
  
  setSize(300, 500);
  setVisible(true);
 }
	
 public void actionPerformed(ActionEvent e)
 {
  JButton b = (JButton)e.getSource();
  if(b==b1)
  {
	String a = t4.getText();
   	String c = t5.getText();
   	if(a.equals(c)==false)
   	{
		t5.setText("");
		JOptionPane.showMessageDialog(null,"Password doesn't match");
                t5.requestFocus();
		return;
   	}
   id = fnm+lnm;
   if(year.getSelectedIndex()==-1)// if choice is unselected
   {
    year.requestFocus();
    return;
   }
   byear = "" + year.getSelectedItem();
   id = id+byear;
   if(upobj.isValidUser(id)==true)
   {
    JOptionPane.showMessageDialog(null, "Record exist");
   }
   else
   {
    String pwd = t4.getText();
    LoginData obj = new LoginData(id, pwd);
    upobj.ls.add(obj);
    JOptionPane.showMessageDialog(null,"User added Sucessfully");
   }
  }
  setVisible(false);
 }
	
 public void focusGained(FocusEvent e)
 {
  
 }
	
 public void focusLost(FocusEvent e)
 {
  JTextField t = (JTextField)e.getSource();
  if(t==t1)
  {
   	fnm = (t1.getText()).trim();
	if(fnm.isEmpty()==true)
	{
		t1.requestFocus();
	}
  }
  if(t==t2)
  {
       lnm = (t2.getText()).trim();
	if(lnm.isEmpty()==true)
	{
		t2.requestFocus();
	}
  }
  if(t==t4)
  {
   	String a = t4.getText();
   	if(LoginData.isValidPassword(a))
  	{
   		 t5.requestFocus();
   	}
  	else
	{
		t4.setText("");
		JOptionPane.showMessageDialog(null,"Password must contain at least 8 characters " + "including uppercase, lowercase, digits, and special characters.");
		t4.requestFocus();
	}
  
  }
 }
}

class LoginUser extends JDialog implements ActionListener
{
 JLabel l1, l2;
 JTextField t1;
 JPasswordField t2;
 JButton b1, b2;
 IPD upobj;
 LoginData obj;
 String id;
 String pw;
	
 LoginUser(JFrame frm, String title, boolean state, IPD tobj)
 {
  super(frm, title, state);
  
  upobj = tobj;
  obj = null;
  l1 = new JLabel("ID");
  l2 = new JLabel("Password");
  t1 = new JTextField(20);
  t2 = new JPasswordField(20);
  b1 = new JButton("Login");
  b2 = new JButton("Back");
  
  b1.addActionListener(this);
  b2.addActionListener(this);
  
  setLayout(new GridLayout(3, 2, 5, 5));
  add(l1);
  add(t1);
  add(l2);
  add(t2);
  add(b1);
  add(b2);
  
  setSize(300, 200);
  setVisible(true);
 }
	
 public void actionPerformed(ActionEvent e)
 {
  JButton b = (JButton)e.getSource();
  
  if(b==b1)
  {
   id = t1.getText();
   pw = t2.getText();
   if(upobj.isValidUserPassword(id, pw)==true)
   {
	S4 a=new S4();
   }
   else
   {
    JOptionPane.showMessageDialog(null, "Login invalid");
	t1.setText("");
        t2.setText("");
   }
  }
  setVisible(false);
 }
}
		

public class ChatappServer extends JFrame implements ActionListener
{
 IPD upobj;
 NewUser nobj;
 JButton bn; // new user
 JButton bl; // login
	
ChatappServer()
 {
  super("Application");
  
  bn = new JButton("New User");
  bl = new JButton("Login");
  
  bn.addActionListener(this);
  bl.addActionListener(this);
  
  setLayout(null);
  
  bn.setBounds(100,50, 150, 40);
  bl.setBounds(100, 100, 150, 40);

 
  
  add(bn);
  add(bl);

        
  
  setSize(350, 250);
  upobj = new IPD();
  
  addWindowListener(new WindowAdapter() 
  {
     public void windowOpened(WindowEvent e)
     {
      upobj.load();
     }
     public void windowClosing(WindowEvent e)
     {
      upobj.save();
      System.exit(0);
     }});
  setVisible(true);
  }
	
 public void actionPerformed(ActionEvent e)
 {
  JButton b = (JButton)e.getSource();
	if(b==bn)
	{
   		nobj = new NewUser(null, "New User", true, upobj);
	}
  if(b==bl)
  {
   LoginUser lobj = new LoginUser(null, "Login", true, upobj);
  }

 }
	
 public static void main(String[]args)
 {
  ChatappServer a = new ChatappServer();
 }
}