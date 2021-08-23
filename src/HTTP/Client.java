package HTTP;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.jsoup.Jsoup;
public class Client implements ActionListener{
private static HttpURLConnection connection;
public static JTextArea ta1 = new JTextArea("");
public static JTextField pname;
public static String rc="",page_name,h1; 
private static String SpliceText(String text, int maxWidth)
{
    StringBuilder sb = new StringBuilder(text);

    for (int i = 0; i < (sb.length()/ maxWidth); i++)
    {
        int insertPosition = i * maxWidth;
        sb.insert(insertPosition, "\n");
    }

    return sb.toString();
}
	public Client() {
		
	JFrame frame = new JFrame("Wikipedia Reader");  
	JPanel panel = new JPanel();  
	panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
	JPanel panel1 = new JPanel();  
	panel1.setLayout(new FlowLayout());  
	JPanel panel2 = new JPanel();  
	panel2.setLayout(new FlowLayout());
	JPanel panel3 = new JPanel();  
	panel3.setLayout(new FlowLayout());
	JPanel panel4 = new JPanel();  
	panel4.setLayout(new FlowLayout());
	JLabel label = new JLabel("Page Contents: ", SwingConstants.CENTER);  
	
	label.setPreferredSize(new Dimension(600,40));
	JButton b = new JButton();
	JButton button = new JButton();
	b.setText("home");
	button.setText("Search");  

	JLabel page,home;
	home = new JLabel("Read from local server : ");
	page = new JLabel("Enter page name: ");
	
	pname = new JTextField(20);
	pname.setColumns(20);
	
	b.addActionListener(this);
	button.addActionListener(this);
	frame.add(panel);
	panel.add(panel1);
	panel.add(panel2);
	panel.add(panel3);
	panel.add(panel4);
	panel1.add(home);
	panel1.add(b);
	panel2.add(page);
	panel2.add(pname);
	panel2.add(button);  
	panel3.add(label);  
	
	panel3.add(ta1);
	
	frame.setSize(1000,600);  
	frame.setLocationRelativeTo(null);  
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	frame.setVisible(true);
	}
public void excec(String URL)
{
	
	BufferedReader reader;
	String line;
	//append each line and note the response content
	StringBuffer responseContent = new StringBuffer();
	rc="";
	try {
		//define URL url 
		URL url = new URL(URL);
		
		//open a URL connection to this API end point 
		try {
			connection = (HttpURLConnection)url.openConnection();
			//request set up
			try {
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			}catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			int status = connection.getResponseCode();
			System.out.println("Status of URL connection : "+status);
			//if status = 200 connection successful
			if(status > 299)
			{
				//our connection has a problem, read the error into the buffer
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				//while we still have something to read from the buffer
				while((line=reader.readLine())!=null)
				{
					responseContent.append(line);
				}
				reader.close();
			}
			else {
				//our connection has no problem, read the  into the buffer
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				//while we still have something to read from the buffer
				
				while((line=reader.readLine())!=null)
				{
					responseContent.append(line);
					rc+=line;
				}
				reader.close();
			}
			
			h1 = Jsoup.parse(rc).text();
			/*rc = rc.replaceAll("\\<.*?>","\t");
			rc = rc.replaceAll("\\{.*?}","\n");
			rc = rc.replaceAll("\\\t*?\t", "");
			rc = rc.replaceAll("\\&*?;", "");*/
			h1=SpliceText(h1,109);
			
		     
		} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
}
public static void main(String args[])
	{
	//Note : both URL object creation and connection have to be surrounded with try catch block
	Client c=new Client();
	//define a BufferedReader to read the input stream
	
	}
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	String command=arg0.getActionCommand();
	if(command=="Search")
	{
		ta1.setBounds(30, 300, 600, 30);
		page_name = pname.getText();
		excec("https://en.wikipedia.org/wiki/"+page_name);
		ta1.setWrapStyleWord(true);
		ta1.setText(h1);
		
		System.out.println("Search Button clicked");
	}
	if(command == "home")
	{
		excec("http://localhost:8080/");
		ta1.setText(h1);
		System.out.println("Home Button clicked");
	}
}  
}
