import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Handler implements Runnable{
	
	Socket clientSocket;
	String command = null ;
	URI uri = null ;
	String version = null ;
	
	public Handler(Socket socket){
		this.clientSocket = socket; 
	}

	@Override
	public void run() {
		try{
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
		String[] clientSentence = null;
		do{
			clientSentence = inFromClient.readLine();
            System.out.println("Received: " + clientSentence);
            String capsSentence = clientSentence.toUpperCase() + '\n'; outToClient.writeBytes(capsSentence);
		}while (clientSentence != null && clientSentence.equals("\n"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		analyse(clientSentence);
		switch(command){
		case "GET":
			if(Files.exists(uri.getPath())){
				statuscode(200);
				byte[] data = doGet(uri.getPath());
				outToClient.write(data);
				break;
			}
			else{
				statuscode(404);
				break;
			}
			
		case "HEAD":
		case "PUT":
		case "POST":
		}
		
	}
	
	public void analyse(String[] sentence){
		String[] input = sentence.get(0).split(" ");
		command = input[0];
		uri = input[1];
		version = input[2];
		if version.contains("HTTP/1.1")
			if (headers.get("Host") == null){
				String response = "HTTP/1.1 400 Bad Request\n";
				out.writeBytes(response);
			}
		
	}
	
	public String getHeader(Path path){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String response = "Date: " + dateFormat.format(calendar.getTime()) + "\n";
		response += "Content-Type: " + Files.probeContentType(path);
		response += "Content-Length: " + Files.size(path) + "\r\n";
		return response;
	}
	
	public byte[] doGet(Path path) throws IOExeption {
		return Files.readAllBytes(path);
	}
	
	public void statuscode(int i){
		switch(statuscode){
		case 200:
			String response = version + "200 OK \n";
			response += getHeader(uri.getPath());
			outToClient.writeBytes(response);
			break;
		case 400:
			String response = version + "400 Bad Request \n"
			response += getHeader(uri.getPath()); 
			response += "<html><body> \n"
					+ "<h2>No Host: header received </h2> \n"
					+ version + "requests must include the Host: header. \n"
					+ "</body></html> \n";
			
		case 404:
		case 500:
		case 304:
		default: 
			System.out.println("Invalid status code " + i);
			break;
		}
	}


}
