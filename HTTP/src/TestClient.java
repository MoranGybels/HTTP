/**
 * Created by adminheremans on 13/03/17.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class TestClient {
    public static void main(String argv[]) throws Exception{
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        Socket clientSocket = new Socket("www.example.com", 80);
        //Socket clientSocket = new Socket("localhost", 6780); -> dit zou een connectie maken met de HTTPServer die wij maakten
        //voor get en zo kan je dus naar een andere server gaan om te vragen of je een pagina mag hebben en zo, maar voor put bv
        // moet je met de lokale server werken want op www.example.com kan je zelf niets veranderen natuurlijk
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String sentence;
       // ArrayList <String> commands = new ArrayList<>();
        String commandString = new String("");
        //sentence = inFromUser.readLine();
        while(!(sentence = inFromUser.readLine()).isEmpty()){
        	
        	//commands.add(sentence);
        	commandString+=(sentence+"\r\n");
        }
        commandString+=("\r\n");
        System.out.println(commandString);
        outToServer.writeBytes(commandString);
        //outToServer.writeBytes("GET /index.html HTTP/1.0" +"\r\n\r\n");
        
        String fromServer;
        System.out.println("FROM SERVER: ");
        // Read text from the server and write it to the screen.
        try{
        	while ( (fromServer = inFromServer.readLine()) != null) {
        
    		System.out.println(fromServer);
        	}
        } catch (IOException e){}
            
            // Close the socket.
        clientSocket.close();
    }
}

