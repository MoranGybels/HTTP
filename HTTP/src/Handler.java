import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Handler implements Runnable{
	
	Socket clientSocket;
	
	public Handler(Socket socket){
		this.clientSocket = socket; 
	}

	@Override
	public void run() {
		try{
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
		String clientSentence = null;
		do{
			clientSentence = inFromClient.readLine();
            System.out.println("Received: " + clientSentence);
            String capsSentence = clientSentence.toUpperCase() + '\n'; outToClient.writeBytes(capsSentence);
		}while (clientSentence != null && clientSentence.equals("\n"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


}
