import java.io.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Handler implements Runnable{
	
	Socket clientSocket;
	String command = null ;
	URI uri = null ;
	String version = null ;
	char sep;

	
	public Handler(Socket socket){
		this.clientSocket = socket; 
	}

	@Override
	public void run(){
		System.out.println("HANDLER");
		BufferedReader inFromClient;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			String clientSentence = inFromClient.readLine();
	        System.out.println("Received: " + clientSentence);
	        analyse(clientSentence);
	        
	        String url = uri.getPath();
		
			
			url = uri.getPath().replaceAll("%20", " ");
			if (url.startsWith("./")) {
				url = url.substring(1);
			}
	     // Constructing local path and log
	        String domain = "localhost";
	     			Path filePath = FileSystems.getDefault().getPath(domain, uri.getPath());
//	     			System.out.println("filepath "+filePath.toString());
//	     			sep = File.separatorChar;
//	     			File f = new File("Serverfiles" + sep + domain + url);
//	     			
//	     			f.getParentFile().mkdirs();
	     	//System.out.println(filePath2.toString());
	        File file = new File(System.getProperty("user.dir")+"/src/"+domain+url);
			switch(command){
			case "GET":
				if(file.exists()){
				//if(Files.exists(filePath)){
					try {
						System.out.println("it exists!");
						statuscode(outToClient, 200);
						byte[] data = doGet(filePath);
						outToClient.write(data);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else{
					try {
						statuscode(outToClient, 404);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			case "HEAD":
			case "PUT":
			case "POST":
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
	}
	
	public void analyse(String sentence){
		String[] input = sentence.split(" ");
		command = input[0];
		
		try {
			uri =  new URI(input[1]);
			System.out.println("uri ");
			System.out.println(uri.getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		version = input[2];
			
		
	}
	
	public String getHeader(String path) throws IOException{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String response = "Date: " + dateFormat.format(calendar.getTime()) + "\n";
		response += "Content-Type: " + Files.probeContentType(Paths.get(path));
		response += "Content-Length: " + Files.size(Paths.get(path)) + "\r\n";
		return response;
	}
	
	public byte[] doGet(Path path) throws IOException{
		return Files.readAllBytes(path);
	}
	
	public void statuscode(DataOutputStream out, int i) throws IOException{
		switch(i){
		case 200:
			String response200 = version + "200 OK \n";
			response200 += getHeader(uri.getPath());
			out.writeBytes(response200);
			break;
		case 400:
			String response400 = version + "400 Bad Request \n";
			response400 += getHeader(uri.getPath()); 
			response400 += "<html><body> \n"
					+ "<h2>No Host: header received </h2> \n"
					+ version + "requests must include the Host: header. \n"
					+ "</body></html> \n";
			out.writeBytes(response400);
			
		case 404:
		case 500:
		case 304:
		default: 
			System.out.println("Invalid status code " + i);
			break;
		}
	}


}
