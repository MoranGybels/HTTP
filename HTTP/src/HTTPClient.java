import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;


//Used for parsing
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * The client side of our HTTP project, used for sending requests and receiving answers over sockets.
 * 
 * @author Elisabeth, Moran
 *
 */
class HTTPClient{
    private static int contentLength;
	private boolean error;
    private String URI;
    private String command;
    private String version;
    private PrintWriter out;
    private ArrayList<String> content;

	 /**
	 * Main method of the HTTP client. It allows the user to give a command of the form 'HTTPClient 
	 * command URL port' and gives back the answer of the server. It uses the HTTP/1.1 version 
	 * to contact the server.
	 * 
	 * @param argv
	 * @throws Exception
	 */
    public static void main(String argv[]) throws Exception{
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

        //ArrayList <String> commands = new ArrayList<>();

//        while(!(sentence = inFromUser.readLine()).isEmpty()){
//
//            commands.add(sentence);
//            //commandString+=(sentence+"\r\n");
//        }
//        //commandString.concat("\r\n");
//        Command command = new Command(commands);

        //Read input from the user and make an HTTP request with it
        String sentence = inFromUser.readLine();
		//inFromUser.close();
		
		//create a request for the server
        HTTPRequest request = new HTTPRequest(sentence);
        String req= request.createRequest(inFromUser);
        
        
        //Create a socket to the host, using the given port number, 
        //and create an outputstream and an inputstream
        Socket clientSocket = new Socket(request.getHost(),request.getPort());
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        InputStream inFromServer = clientSocket.getInputStream();
        
        //Write to the server
        System.out.println("request :"+ req);
        outToServer.writeBytes(req);
		outToServer.flush();
		//System.out.println("gelukt");
		
		// Read text from the server and write it to the screen. First line is always a status line
		String serverRes = rdLine(inFromServer, false);
		System.out.println("FROM SERVER: " + serverRes);
		

		//Process the header of the response, until we encounter two \n or \r\n after one another
		//and find the content length
		setContentLength(0);
		while(!serverRes.equals("") && !serverRes.equals("\r")) {// && !serverRes.equals("\n") && !serverRes.equals("\r\n")) not needed: see rdLine implementation
			serverRes = rdLine(inFromServer,false);
			System.out.println(serverRes);
			// Grab the last piece of this line; this will be the length of the content in bytes
			if(serverRes.startsWith("Content-Length")){
				//Content-Length is assumed of the form Content-Length : int
				String[] headerSplit = serverRes.split("\\s+");
				String contentLengthStr = headerSplit[headerSplit.length - 1];
				if (contentLengthStr.charAt(contentLengthStr.length() - 1) == '\r'){
					contentLengthStr = contentLengthStr.substring(0, contentLengthStr.length()-1);
				}
				setContentLength(Integer.parseInt(contentLengthStr));
			}
		}
		//TODO: onze localhost server moet een enter geven op het einde vna put of post zodat het niet vastloopt
		
		//Now we start reading what the server sent us, print it out for the user and put it in a file,
		//until we have all the content
		//While doing so, we grab the URI's of all images we have to retrieve
		char sep = File.separatorChar;
		if(!request.getFilePath().endsWith(".html")){
			if(request.getFilePath().equals("")){
				request.setFilePath(request.getFilePath() + "index.html");
			}else{
				request.setFilePath(request.getFilePath()+sep + "index.html") ;
			}
		}
		File file = new File("RequestedFiles" + sep + request.getHost() + sep + request.getFilePath());
		file.getParentFile().mkdirs();

		//Create the file. If it existed, delete it.
		if (file.exists()){
			file.delete();
		}
		file.createNewFile();
		PrintWriter out = new PrintWriter(file);	//The printwriter writes lines to the specified file	

		//Print the response of the server out for the user and also store it in the file
		if(!request.getCommand().equals("HEAD")){
			while (contentLength>0){
				serverRes = rdLine(inFromServer, true);
				System.out.println(serverRes);
				out.println(serverRes);
				out.flush();
				//Subtract one extra because rdLine drops \n too
				//setContentLength(getContentLength() - (serverRes.length() + 1));
				//TODO verwijder deze print
				//System.out.println("contlength:" + getContentLength());

			}
		}
		
		System.out.println("File succesfully stored");
		out.flush();
		out.close();
		
		//Now we grab all relative URLs of images from the file, using the Jsoup library as an html parser
		LinkedList<String> images = new LinkedList<String>();
		Document doc = Jsoup.parse(file, "UTF-8");
		Elements img = doc.getElementsByTag("img");
		for (Element el : img) {
			String src = el.attr("src");
			System.out.println(src);
			//We try to avoid absolute URLs, because we weren't supposed to be able to process these
			if(!src.startsWith("http:/") && !src.startsWith("www.")){
				if(!src.startsWith("/")){
					src = "/" + src;
				}
				images.add(src);
				
			}
		}
		System.out.println(images.size() + " IMAGES TO GET");
		
		//Now to get those images
		int i = 1;
		for(String url : images){
				//If this image is the last one, request the closure of the connection via the connection header
				if (i++ == images.size()) {
					//System.out.println("GET " + url + " HTTP/1.1\r\nConnection: Close\r\nHost: " + request.getHost()  + "\r\n\r\n");
					outToServer.writeBytes("GET " + url + " HTTP/1.1\r\nConnection: Close\r\nHost: " + request.getHost() + "\r\n\r\n");
			    }
				else{
					//System.out.println("GET " + url + " HTTP/1.1\r\nHost: " + request.getHost()  + "\r\n\r\n");
					outToServer.writeBytes("GET " + url + " HTTP/1.1\r\nHost: " + request.getHost() + "\r\n\r\n");
				
			}
			
			System.out.println("Responses for image fetching: ");
			serverRes = rdLine(inFromServer,false);
			
			
			// start download if the status code says 'ok'
			if(serverRes.contains("200")){
				System.out.println(serverRes);
				//We now start with the header of the response and find the content length
				contentLength = -1;
				while(!serverRes.equals("") && !serverRes.equals("\r")){// && !serverRes.equals("\n") && !serverRes.equals("\r\n")) not needed: see rdLine implementation
					serverRes = rdLine(inFromServer, false);
					System.out.println(serverRes);
					// Grab the last piece of this line; this will be the length of the content in bytes
					if(serverRes.startsWith("Content-Length")){
						//Content-Length is assumed of the form Content-Length : int
						String[] headerSplit = serverRes.split("\\s+");
						String contentLengthStr = headerSplit[headerSplit.length - 1];
						if (contentLengthStr.charAt(contentLengthStr.length() - 1) == '\r'){
							contentLengthStr = contentLengthStr.substring(0, contentLengthStr.length()-1);
						}
						int length = Integer.parseInt(contentLengthStr);
						setContentLength(length);

					}
				}		
				//Download the image (the body of the response).
				//We don't use rdLine because we want to get the bytes and store them as they are
				imageDownload(inFromServer,url,request.getHost());
			}
			
			else{
				System.out.println("Image " + url + " could not be downloaded.");
				System.out.println( serverRes);
				//We should still read what the server has to say, go through the header and find content length
				contentLength = -1;
				while(!serverRes.equals("") && !serverRes.equals("\r")){// && !serverRes.equals("\n") && !serverRes.equals("\r\n")) not needed: see rdLine implementation
					serverRes = rdLine(inFromServer, false);
					System.out.println(serverRes);
					// Grab the last piece of this line; this will be the length of the content in bytes
					if(serverRes.startsWith("Content-Length")){
						//Content-Length is assumed of the form Content-Length : int
						String[] headerSplit = serverRes.split("\\s+");
						String contentLengthStr = headerSplit[headerSplit.length - 1];
						if (contentLengthStr.charAt(contentLengthStr.length() - 1) == '\r'){
							contentLengthStr = contentLengthStr.substring(0, contentLengthStr.length()-1);
						}
						int length = Integer.parseInt(contentLengthStr);
						setContentLength(length);
					}
				}
				while (contentLength>0){
					serverRes = rdLine(inFromServer, true);
					System.out.println(serverRes);
				}
			}
		}
		
		//m.writeMap();
		// Close the socket and its connected streams.
		inFromServer.close();
		outToServer.close();
		clientSocket.close();

    }
  

    

	/**
	 * Manually read the next line from the InputStream, and convert the integers from 0-255 you read into characters.
	 * If the flag is false, the contentlength isn't changed after reading. Else, the content length is lowered with
	 * the number of characters read
	 * 
	 * @param inFromServer
	 * @return
	 * @throws IOException
	 */
	private static String rdLine(InputStream inFromServer, boolean flag) throws IOException {
		//This cast works for characters in the ISO-8859-1 character set,
		//which should be sufficient for this assignment. This is also the standard for http/1.0 and 1.1.
		char nextChar = (char) inFromServer.read();
		String res = "";
		if (flag == true){
			lowerContentLength();
			if(getContentLength() == 0){
				res+=nextChar;
				return res;
			}
		}
		//We make sure we don't get the (carriage return and) newline at the end
		while(nextChar != '\n'){
			res += nextChar;
			nextChar = (char) inFromServer.read();
			if (flag == true){
				lowerContentLength();
				if(getContentLength() == 0){
					res+=nextChar;
					return res;
				}
			}
		}
		return res;
		
	} 
	
	/**
	 * Sets the content length.
	 * @param length
	 */
	private static void setContentLength(int length){
		contentLength = length;
	}
	
	/**
	 * Gets the content length.
	 * @return
	 */
	private static int getContentLength(){
		return contentLength;
	}
	
	/**
	 * Lower the length of the content to get.
	 */
	private static void lowerContentLength(){
		contentLength -= 1;
	}
	
	/**
	 * Download the image behind the given relative url with given length via the inputstream that is supplied.
	 * 
	 * @param in
	 * @param url
	 * @param length
	 * @throws IOException
	 */
	private static void imageDownload(InputStream in, String url, String host) throws IOException {
		char sep = File.separatorChar;
		url = "RequestedFiles" + sep + host +  sep + url;
		File f = new File(url);
		//Create the parent path, if one exists
		if(f.getParentFile() != null){
			f.getParentFile().mkdirs();
		}
		//Create the file. If it existed, delete it.
		if (f.exists()){
			f.delete();
		}
		f.createNewFile();
		
		// We are now going to write bytes, so we can't use a printstreamer. We opted for a FileOutputStream.
		FileOutputStream fileOut = new FileOutputStream(f, true);
		int length = getContentLength();
		// We use a buffer size of 1kB: not too small, not too large.
		byte[] buffer = new byte[getContentLength()];
		while(length  >= 1024){
			int read = in.read(buffer,0,1024);
			fileOut.write(buffer,0,read);
			fileOut.flush();
			length  -= read;
		}
		// There's less than 1 kB remaining
		while(length  > 0){
			// Make a new array to carry the last bunch of bytes.
			byte[] rest = new byte[length];
			int read = in.read(rest,0,length);
			fileOut.write(rest,0, read);
			fileOut.flush();
			length -= read;
		}
		// Release resources.
		fileOut.flush();
		fileOut.close();
	}
	


}
