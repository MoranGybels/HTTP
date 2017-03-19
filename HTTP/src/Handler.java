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
		BufferedReader inFromClient;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			String clientSentence = inFromClient.readLine();
	        System.out.println("Received: " + clientSentence);
	        analyse(clientSentence);
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
	     	File f = new File(domain + url);
	     			
	     	f.getParentFile().mkdirs();
	     	LinkedList<String> data = getData(inFromClient);
	     	//System.out.println(filePath2.toString());
//	        File file = new File(System.getProperty("user.dir")+"/src/"+domain+url);
//	        path = file.getAbsolutePath();
//	        Files.createDirectories(filePath.getParent());
			switch(command){
			case "GET":
				if(f.exists()){
				//if(Files.exists(filePath)){
					try {
						statuscode(f, outToClient, 200);
						byte[] body = doGet(Paths.get(f.getPath()));
						outToClient.write(body);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else{
					try {
						statuscode(f, outToClient, 404);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			case "HEAD":
				if(f.exists()){
					statuscode(f, outToClient, 200);
					break;
				} else{
					statuscode(f, outToClient, 404);
					break;
				}
			case "PUT":
				if(doPut(f, data)){
					statuscode(f, outToClient, 200);
					break;
				} else{
					statuscode(f, outToClient, 500);
					break; 
				}
				
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
	
	public String getHeader(File file) throws IOException{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String response = "Date: " + dateFormat.format(calendar.getTime()) + "\n";
		response += "Content-Type: " + Files.probeContentType(file.toPath()) + "\n";
		response += "Content-Length: " + file.length() + "\r\n";
		return response;
	}
	
	public byte[] doGet(Path path) throws IOException{
		return Files.readAllBytes(path);
	}
	
	private boolean doPut(File f, LinkedList<String> body) {
		if (f.exists()){
			f.delete();
		}
		try {
			f.createNewFile();
			PrintWriter out = new PrintWriter(f);
			for (String str:body) {
				out.println(str);
				out.flush();
			}
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public LinkedList<String> getData(BufferedReader inFromClient) throws IOException {
		LinkedList<String> data = new LinkedList<String>();

		while (inFromClient.ready()) {
			String nextLine = inFromClient.readLine();
			if (nextLine.equals("") || nextLine.equals("\n")
					|| nextLine.equals("\r\n") || nextLine.equals("\r")) {
				return data;
			}
			data.add(nextLine);
		}
		return data;
	}
	
	public void statuscode(File file, DataOutputStream out, int i) throws IOException{
		System.out.println("FOut in statuscode functie? ");
		switch(i){
		case 200:
			String response200 = version + "	200 OK \n";
			response200 += getHeader(file);
			out.writeBytes(response200);
			break;
		case 400:
			String response400 = version + "	400 Bad Request \n";
			response400 += getHeader(file); 
			response400 += "<html><body> \n"
					+ "<h2>No Host: header received </h2> \n"
					+ version + "requests must include the Host: header. \n"
					+ "</body></html> \n";
			out.writeBytes(response400);
			break;
		case 404:
			String response404 = version + "	404 Not Found \n";
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", Locale.US);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			response404 += "Date: " + dateFormat.format(calendar.getTime()) + "\n";
			out.writeBytes(response404);
			break;
		case 500:
			String response500 = version + "	500 Server Error \n";
			response500 += getHeader(file);
			out.writeBytes(response500);
			break;
		case 304:
			String response304 = version + "	304 Not Modified \n";
			response304 += getHeader(file);
			out.writeBytes(response304);
			break;
		default: 
			System.out.println("Invalid status code " + i);
			break;
		}
	}


}
