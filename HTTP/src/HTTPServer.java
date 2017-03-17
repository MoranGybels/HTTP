import java.io.*;
import java.net.*;

class HTTPServer{
    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6000);
        //Socket welcomeSocket = new Socket("localhost", 6780);
//        while(true) {
//            Socket connectionSocket = welcomeSocket.accept();
//            BufferedReader inFromClient =new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//            String clientSentence = inFromClient.readLine();
//            System.out.println("Received: " + clientSentence);
//            //String capsSentence = clientSentence.toUpperCase() + '\n'; outToClient.writeBytes(capsSentence);
    	System.out.println("Listening to port 6780");
    	while (true) { 
			Socket connectionSocket = welcomeSocket.accept();
			System.out.println("connectionSocket  " + connectionSocket);
			if(connectionSocket != null){
				Handler request = new Handler(connectionSocket);
				Thread thread = new Thread(request);
				thread.start();
				System.out.println("THREAD CREATED");
			}
			

        }
    }
}
