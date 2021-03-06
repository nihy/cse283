
import java.io.*;
import java.net.*;
import java.util.*;

public final class Project1 {

	public static void main(String argv[]) throws Exception {
		int port = 2428;

		// Establish the listen socket
		@SuppressWarnings("resource")
		ServerSocket lstnSocket = new ServerSocket(port);

		// Process HTTP service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			Socket connection = lstnSocket.accept();

			// Create an object to handle HTTP request messages
			HttpRequest request = new HttpRequest(connection);

			Thread thread = new Thread(request);
			thread.start();
		}
	}

	final static class HttpRequest implements Runnable {

		private Socket socket;
		final static String CRLF = "\r\n";

		// Constructor
		public HttpRequest(Socket socket) throws Exception {
			this.socket = socket;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				processRequest(socket);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void processRequest(Socket socket) throws Exception {
		    // Get a reference to the socket's input and output streams.
		    InputStream is = socket.getInputStream();
		    DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		    // Set up input stream filters.
		    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

	        // Get the request line of the HTTP request message.
	        String requestLine = br.readLine();

	        // Extract the filename from the request line.
	        StringTokenizer tokens = new StringTokenizer(requestLine);
	        tokens.nextToken();  // skip over the method, which should be "GET"
	        String fileName = tokens.nextToken();
		
	        // Prepend a "." so that file request is within the current directory.
	        fileName = "." + fileName ;
		
		    // Open the requested file.
	        FileInputStream fis = null ;
	        boolean fileExists = true ;
	        try {
		    fis = new FileInputStream(fileName);
	        } catch (FileNotFoundException e) {
		    fileExists = false ;
	        }

		    // Debug info for private use
		    System.out.println(requestLine);
		    String headerLine = null;
		    while ((headerLine = br.readLine()).length() != 0) {
		        System.out.println(headerLine);
		    }
		
		    // Construct the response message.
	        String statusLine = null;
	        String contentTypeLine = null;
	        String entityBody = null;

	        if (fileExists) {
		        statusLine = "HTTP/1.0 200 OK" + CRLF;
		        contentTypeLine = "Content-Type: " + 
			        contentType(fileName) + CRLF;
	        } else {
		        statusLine = "HTTP/1.0 404 Not Found" + CRLF;
		        contentTypeLine = "Content-Type: text/html" + CRLF;
		        entityBody = "<HTML>" + 
			    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
			    "<BODY>Not Found</BODY></HTML>";
	        }
		    // implement some code here
		
		    // Send the status line.
	        os.writeBytes(statusLine);

	        // Send the content type line.
	        os.writeBytes(contentTypeLine);

	        // Send a blank line to indicate the end of the header lines.
	        os.writeBytes(CRLF);

	        // Send the entity body.
	        if (fileExists) {
		        sendBytes(fis, os);
		        fis.close();
	        } else {
		        os.writeBytes(entityBody) ;
	        }

	        // Close streams and socket.
	        os.close();
	        br.close();
	        socket.close();
	    }
		
		private static void sendBytes(FileInputStream fis, 
				  OutputStream os) throws Exception {
	    // Construct a 1K buffer to hold bytes on their way to the socket.
	    byte[] buffer = new byte[1024];
	    int bytes = 0;
	
	    // Copy requested file into the socket's output stream.
	    while ((bytes = fis.read(buffer)) != -1) {
	    os.write(buffer, 0, bytes);
	    }
  }
		private String contentType(String fileName) {
			if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
				return "text/html";
			else if (fileName.endsWith(".gif"))
				return "image/gif";
			else if (fileName.endsWith(".jpeg"))
				return "image/jpeg";
			else if (fileName.endsWith(".png"))
				return "image/png";
			else if (fileName.endsWith(".pdf"))
				return "application/pdf";
			else if (fileName.endsWith(".zip"))
				return "application/zip";
			else
				return "application/octet-stream";
		}
	}

}