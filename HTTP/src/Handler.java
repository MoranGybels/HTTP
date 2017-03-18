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
	String uri = null ;
	String version = null ;
	char sep;
	String path = null; 

	
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
	        System.out.println("SENTENCE GEANALYSEERD" );
	        
	        String url = uri;
		
			
			url = uri.replaceAll("%20", " ");
			if (url.startsWith("./")) {
				url = url.substring(1);
			}
	     // Constructing local path and log
	        String domain = "localhost";
	     	Path filePath = FileSystems.getDefault().getPath(domain, url);
	     	System.out.println("filepath "+filePath.toString());
	     	sep = File.separatorChar;
	     	File f = new File( sep + domain + url);
	     	System.out.println("NIEUWE FILE: " + f.exists());
	     			
	     	f.getParentFile().mkdirs();
	     	//System.out.println(filePath2.toString());
//	        File file = new File(System.getProperty("user.dir")+"/src/"+domain+url);
//	        path = file.getAbsolutePath();
//	        Files.createDirectories(filePath.getParent());
			switch(command){
			case "GET":
				//if(file.exists()){
				if(Files.exists(filePath)){
					try {
						System.out.println("it exists!");
						statuscode(outToClient, 200);
						FileInputStream data = doGet(f);
						outToClient.writeChars(data.toString());
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else{
					try {
						statuscode( outToClient, 404);
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
		
		uri = input[1];
		
		version = input[2];
		System.out.println(version);
	}
	
	public String getHeader() throws IOException{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String response = "Date: " + dateFormat.format(calendar.getTime()) + "\n";
		System.out.println("HEADER VOOR FILE ACCESS");
		response += "Content-Type: " + Files.probeContentType(Paths.get(path));
		System.out.println("CONTENT-TYPE HEADER TOEGEVOEGD");
		response += "Content-Length: " + Files.size(Paths.get(path)) + "\r\n";
		System.out.println("CONTENT-LENGTH HEADER TOEGEVOEGD");
		return response;
	}
	
	public FileInputStream doGet(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}
	
	public void statuscode(DataOutputStream out, int i) throws IOException{
		System.out.println("FOut in statuscode functie? ");
		switch(i){
		case 200:
			System.out.println("STATUSCODE");
			String response200 = version + "200 OK \n";
			System.out.println("NOG STEEDS NIETS MET FILE GEDAAN");
			response200 += getHeader();
			System.out.println("HEADER TOEGEVOEGD");
			out.writeBytes(response200);
			break;
		case 400:
			String response400 = version + "400 Bad Request \n";
			response400 += getHeader(); 
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
