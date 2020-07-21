//Name:Vaishnavi Chitloor Venkatesh
//Student ID: 1001724384
//links referred :https://www.javaworld.com/article/2853780/socket-programming-for-scalable-systems.html 
//http://www.csc.villanova.edu/~schragge/CSC8560/project1/webserver_prt.htm 
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public final class WebServer {
	
	// Setting up the port number
	private static int port = 8080;
	
	private static Socket socket   = null; 
    private static ServerSocket server   = null; 
    private static long startTime  ;
    private static long endTime ;
   

	public static void main(String[] args) throws Exception {
		
		//Create a socket
		long startTime = System.currentTimeMillis();
		System.out.println("StartTime:"+startTime);
		server = new ServerSocket(port);
		
//		System.out.print("time in milliseconds = ");
//	    System.out.println(System.currentTimeMillis());
//		long millis = System.currentTimeMillis();
//		System.out.println(millis); 
		while(true) {
			// Accept the incoming connections via the accept()
			
			socket = server.accept();
			// Construct an object to process the HTTP request message.
			HttpRequest request =  new HttpRequest(socket);
			// Create a new Thread to process the request
			Thread thread = new Thread(request);
			thread.start();
			
			
		}
	}
}

final class HttpRequest implements Runnable {

	final static String CRLF = "\r\n";
	Socket socket;
	
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// Most of the processing will take place within processRequest()
			processRequest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void processRequest() throws Exception
	{
		
		FileInputStream fis = null;
		boolean fileExists = true;
		
		// Get a reference to the socket's input and output streams.
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		// Setup Input Stream Filters
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		// Get the request line of the HTTP request message
		String requestLine = br.readLine();

		
		// Display the request line
		System.out.println("Socket Family : AddressFamily.AF_INET");
		System.out.println("Socket name: ('127.0.0.1', 8080)");
		System.out.println("Client's request recieved by the Server");
		System.out.println("This is requestLine");
		System.out.println(requestLine);
		
		// Get and display the header lines.
		System.out.println("This is the headerline");
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		
		// WebServer: Part B
		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();
		// Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false; // if the file does not exists
		}
		
		System.out.println("FileInputStream : " + fis);
		System.out.println("fileExists(True/False)? : " + fileExists);
		System.out.println("Name of the file is : " + fileName);
		
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK" + CRLF;
			contentTypeLine = "Content-Type : " + contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP/1.1 404 Not Found" + CRLF;
			contentTypeLine = "Content-Type : " + contentType(fileName) + CRLF;
			entityBody = "<HTML>" + 
						 "<HEAD><TITLE>Nothing is Found</TITLE></HEAD>" + 
						 "<BODY>NOT FOUND</BODY></HTML>";
			
		}
		
		// print statusline and contentTypeLine
		System.out.println("The status line is : " + statusLine);
		System.out.println(contentTypeLine);
		// we can send the status line and our single header line to the browser by writing into the socket's output stream.
		
		// Send the status line
		os.writeBytes(statusLine);
		// Send the contentTypeLine
		os.writeBytes(contentTypeLine);
		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);
		
		// Send the entity body
		if (fileExists) {
			sendBytes(fis, os);
		fis.close();
		} else {
			os.writeBytes(entityBody);
		}
		long endTime = System.currentTimeMillis();
		
		
		System.out.println("EndTime:"+ endTime);
//		System.out.println("RTT: "+ (endTime-startTime));
		// close all the streams and sockets
		
		System.out.println("Server is  closing all the sockets and streams  ");
		br.close();
		os.close();
		is.close();
		 

		
		socket.close();
	}
	
	//Method to get the MIME Type
	private String contentType(String fileName) throws IOException
	{
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if(fileName.endsWith(".GIFs")) {
			return "image/gif";
		}
		if(fileName.endsWith("JPEGs")) {
			return "image/jpeg";
		}
		
		return "application/octet-stream";
	}
	
	// Method to send the file
	private static void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		// Copy requested file into the socket's output stream.
		while((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}
	
	
}
