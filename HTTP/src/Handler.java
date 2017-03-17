import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
		
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
		String clientSentence = inFromClient.readLine();
        System.out.println("Received: " + clientSentence);
		
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
				statuscode(outToclient, 404);
				break;
			}
			
		case "HEAD":
		case "PUT":
		case "POST":
		}
		
	}
	
	public void analyse(String sentence){
		String[] input = sentence.split(" ");
		command = input[0];
		uri =  new URI(input[1]);
		version = input[2];
		if (version.contains("HTTP/1.1"))
			if (headers.get("Host") == null){
				String response = "HTTP/1.1 400 Bad Request\n";
				out.writeBytes(response);
			}
		
	}
	
	public String getHeader(String path){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String response = "Date: " + dateFormat.format(calendar.getTime()) + "\n";
		response += "Content-Type: " + Files.probeContentType(path);
		response += "Content-Length: " + Files.size(path) + "\r\n";
		return response;
	}
	
	public byte[] doGet(String path) throws IOException{
		return Files.readAllBytes();
	}
	
	public void statuscode(DataOutputStream out, int i){
		switch(i){
		case 200:
			String response200 = version + "200 OK \n";
			response200 += getHeader(uri.getPath());
			out.writeBytes(response200);
			break;
		case 400:
			String response400 = version + "400 Bad Request \n"
			response400 += getHeader(uri.getPath()); 
			response400 += "<html><body> \n"
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
