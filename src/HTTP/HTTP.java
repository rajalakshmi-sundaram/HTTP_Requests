package HTTP;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.*;


public class HTTP extends JFrame implements Runnable{ 

	public static String nohtml,message="standard-response-to-client"; 
	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "test.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	// port to listen connection
	static final int PORT = 8080;
	public static JTextArea ta; 
	static final boolean verbose = true;
	public static String output="";
	private Socket connect;
	public  static JFrame f;   
	
	public HTTP()
	{
		f = new JFrame("Server details");
		ta = new JTextArea("Server started.\nListening for connections on port : " + PORT + " ...\n");
		f.add(ta);
		f.setSize(300, 400);;
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.setVisible(true);
	}
	public HTTP(Socket c)
	{
		output="";
		connect=c;
	}
	
	public static void main(String[] args) {
		
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			new HTTP();
		
			
			while (true) {
				HTTP myServer = new HTTP(serverConnect.accept());
				
				if (verbose) {
					output+="\nConnection opened. (\n" + new Date() + ")";
				}
				
				// create dedicated thread to manage the client connection
				Thread thread = new Thread(myServer);
				thread.start();
			}
			
		} catch (IOException e) {
			
		}
	}
	
	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		String fileRequested ="";
		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			String input = in.readLine();
			output+="\n"+input;
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			fileRequested = parse.nextToken().toLowerCase();
			if(fileRequested.contains("?")) {
				String[] file_message = fileRequested.split("\\?");
				fileRequested = file_message[0];
				String[] kv = file_message[1].split("=");
				message = kv[1];
			}
			if (!method.equals("GET")  &&  !method.equals("HEAD") && !method.equals("POST")) 
				{
				if (verbose) 
				{
					output=output+"\n501 Not Implemented : " + method + " method.";
				}
				}
			
			else if(method.equals("POST"))
			{
				final String secretKey = "password";
				output=output+"\nMessage : "+message;
				
				String encryptedString = AES.encrypt(message, secretKey) ;
				byte[] putData =encryptedString.getBytes() ;
				String line="";
				int i=0;
				while(i<8)
				{
					line=in.readLine();
					output=output+"\n"+line;
					i++;
				}
				out.println("HTTP/1.1 200 OK");
				out.println(); // blank line between headers and content, very important !
				out.flush(); // flush character output stream buffer
				dataOut.write(putData,0,putData.length);
				dataOut.flush();
			}
			else {
				// GET or HEAD method
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}
				File file = new File(WEB_ROOT, fileRequested);
				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);
				
				if (method.equals("GET")) { // GET method so we return content
					byte[] fileData = readFileData(file, fileLength);
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server by Rajalakshmi : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println(); // blank line between headers and content, very important !
					out.flush(); // flush character output stream buffer
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
					dataOut.close();
					connect.close();
				}
				
				if (verbose) {
					System.out.println("File " + fileRequested + " of type " + content + " returned");
					}
				}} catch (FileNotFoundException fnfe) {
			
				if(!fileRequested.contentEquals("/favicon.ico")){
				System.out.println(fileRequested+"hello");
				System.err.println("Error with file not found exception : " + fnfe.getMessage());}
			
			
		} catch (IOException ioe) {
			
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			
			if (verbose) {
				output+="\nConnection closed.\n";
				ta.setText(output);
			}
		}	
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		
		return fileData;
	}
	
	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
}