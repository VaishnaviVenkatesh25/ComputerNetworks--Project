//Name:Vaishnavi Chitloor Venkatesh
//Student ID: 1001724384
//links referred:
//https://www.javaworld.com/article/2853780/socket-programming-for-scalable-systems.html 
//http://www.csc.villanova.edu/~schragge/CSC8560/project1/webserver_prt.htm 
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client {
	
	// initialize socket and input output streams 
    private static Socket socket            = null; 
    private static BufferedReader br 		= null;
    private static BufferedReader input     = null;
    private static InputStream is			= null;
    private static PrintStream out 			= null;
    private static long startTime  ;
    private static long endTime ;
    
    // setup the ipaddress and port number 
    private static String address;
    private static int port; 

	public static void main(String[] args) throws IOException {
		
		// establish the socket connection
		try {
			
			// takes inputs from terminal
			// Enter the ipaddress
			System.out.println("Please Enter the IPaddress");
			input = new BufferedReader(new InputStreamReader(System.in));
			address = input.readLine();
			if(address.length() == 0) {
				System.out.println("Address of the Default ipaddress is considered");
				address = "127.0.0.1";
			}
			
			System.out.println("Enter the port");
			port = Integer.parseInt(input.readLine());
			if(port != 8080) {
				System.out.println("Server is listening on port, port 8080 is considered");
				port = 8080;
			}
			
			System.out.println("Please Enter the file name");
			String s = input.readLine();
			// If the file name is not entered, the default file �index.html� is used.
			if(s.length() == 0) {
				System.out.println("If file name is not provided, the default file �index.html� is used.");
				s = "index.html";
			}
			
			// connect to the server
			System.out.println("Socket Family : AddressFamily.AF_INET");
			System.out.println("Socket name: ('127.0.0.1', 8080)");
			
			
			socket = new Socket(address, port);
			System.out.println("Client is connected now ");
			
			  
            // Get a reference to the socket's input and output streams.
            is = socket.getInputStream();
            // sends output to the socket 
            out = new PrintStream( socket.getOutputStream() );
            
            // Setup Input Stream Filters
    		br = new BufferedReader(new InputStreamReader(is));
    		// Follow the HTTP protocol of GET /<filename > HTTP/1.0 followed by an empty line
    		startTime = System.currentTimeMillis();
			System.out.println("StartTime:"+startTime);
    		out.println( "GET " + "/" + s + " HTTP/1.0" );
    		out.println();
    		
            
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Read data from the server until we finish reading the document
		String line = br.readLine();
		String status = line; // get the status and print it on the console
		System.out.println("Status : " + status);
		
		line = br.readLine();
		String contentType = line; // get the contentType and print it on the console.
		System.out.println(contentType);
		
		line = br.readLine();
       	while( line != null )
        {
       		System.out.println( line );
       		line = br.readLine();
        }
       	long endTime = System.currentTimeMillis();
       	System.out.println("EndTime:"+endTime);
		System.out.println("RTT: "+ (endTime-startTime));
       
  
       	// close the connection 
        try
        { 
        	System.out.println("Closing all the sockets and streams  ");
        	input.close();
        	br.close();
            socket.close(); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        } 
	}
}
