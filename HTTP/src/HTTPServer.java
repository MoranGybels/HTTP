import java.io.*;
import java.net.*;

class HTTPServer{
	/**
	 * Creates a new socket at a fixed portnumber 1567 and starts for each request a new handler and thread. 
	 * @param argv
	 * @throws Exception
	 */
    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(1567);
    	while (true) { 
			Socket connectionSocket = welcomeSocket.accept();
			if(connectionSocket != null){
				Handler request = new Handler(connectionSocket);
				Thread thread = new Thread(request);
				thread.start();
			}
			

        }
    }
}
