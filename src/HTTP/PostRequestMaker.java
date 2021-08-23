package HTTP;
	  
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.*;
import org.apache.commons.io.IOUtils;

import java.io.PrintWriter;
	import java.io.File;
	
	public class PostRequestMaker implements ActionListener{
		public static JTextField t1,t2;
		public static JPasswordField secretKey;
		public static JTextArea l= new JTextArea("Response");
		public  static JFrame f;   
		public static String url="",parameters="",output="";
		protected static char [] password;  
		public PostRequestMaker()
		{
   			   f = new JFrame("Client");
		       JPanel buttonPane = new JPanel();
		       JPanel fieldsPanel = new JPanel();
		       JLabel URL  = new JLabel("URL");
		       JLabel par  = new JLabel("Parameters");
		       JLabel pass = new JLabel("Secret Key ");
 		       t1 = new JTextField();
		       t2 = new JTextField();
		       secretKey = new JPasswordField();
		       JButton b = new JButton("POST");
		        b.addActionListener(this);
		        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
		        buttonPane.setLayout(new FlowLayout());

		        fieldsPanel.add(URL);
		        fieldsPanel.add(t1);
		        fieldsPanel.add(par);
		        fieldsPanel.add(t2);
		        fieldsPanel.add(pass);
		        fieldsPanel.add(secretKey);
		        buttonPane.add(b);
		        buttonPane.add(l);
		        f.add(fieldsPanel, BorderLayout.PAGE_START);
		        f.add(buttonPane,BorderLayout.CENTER);
		        f.setSize(600, 600);;
		        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        f.setVisible(true);

		}
		public static void main(String[] args) throws Exception {
		    new PostRequestMaker();
			System.out.println("\nWelcome to POST Request Maker!");
			}


		private static void sendPost(String url, String parameters, char[] password2) throws Exception {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");	
			con.connect();
			byte[] input = parameters.getBytes();
			try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
				   wr.write( input );
			}
			
			output+="\nURL : " + url;
			output+="\nRequest Method: " + con.getRequestMethod();
			output+="\nResponse Code: " + con.getResponseCode();
			output+="\nResponse Message: " + con.getResponseMessage();
			InputStream in = new BufferedInputStream(new BufferedInputStream(con.getInputStream()));
			
			String response = IOUtils.toString(in,"UTF-8");
			if(url=="http://localhost:8080/")
			{
				String key = String.valueOf(password2);
		    	String decryptedString = AES.decrypt(response,key) ;
			output+="\nResponse Length: " + decryptedString.length()+"\n";
			output+=decryptedString;
			}
			File file = new File ("output.txt");
			PrintWriter out = new PrintWriter("output.txt");
			output=output+"\n"+response;
			output+="\n";
			
			out.println(response);
			out.flush();
			output+="\nResponse copied to output.txt.";
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String command=arg0.getActionCommand();
			if(command=="POST")
			{
				output="";
				url = t1.getText();
				if(url.isEmpty())
					url="http://localhost:8080/";
				parameters=t2.getText();
				password = secretKey.getPassword();
				int f=0;
				try {
					sendPost(url, parameters,password);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					f=1;
					l.setText("Unable to make the POST Request ");
					//e.printStackTrace();
				}
				if(f==0)
				l.setText(output);
			}
		}
	}
