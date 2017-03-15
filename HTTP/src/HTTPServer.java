import java.io.*;
import java.net.*;

class HTTPServer{
    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6780);
        while(true) {
        	// Create	 a 'real' socket from the Server socket.
            Socket connectionSocket = welcomeSocket.accept();
            if(connectionSocket != null){
            	Handler request = new Handler(connectionSocket);
            	Thread thread = new Thread(request);
            	thread.start();
            	
            }
        }
    }
}
