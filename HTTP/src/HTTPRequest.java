import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * A class of HTTPRequests. To construct an HTTPRequest, the user provides a sentence 
 * like 'HTTPClient command URL port'. 
 * An HTTPRequest object consists of several parameters: the host, the port, the uri, the command, the http version
 * and the path.
 * 
 * @author Elisabeth, Moran
 *
 */
public class HTTPRequest {
    private String host;
    private int port;
    private URI uri;
    private String command;
    private String version;
	private String path;
	private String filePath;

	/**
	 * Constructor of an HTTPRequest. Given a client sentence, it extracts the important parameters of the request.
	 * An HTTPRequest object consists of several parameters: the host, the port, the uri (the full uri), the command, the http version
	 * and the path (the part of the uri after the host).
	 * 
	 * @param sentence
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws URISyntaxException
	 */
    public HTTPRequest(String sentence) throws IOException, IllegalArgumentException, URISyntaxException {
    	
    	//Split sentence in words
        String[] input = sentence.split(" ");
        if (!input[0].equals(new String("HTTPClient"))){
        	throw new IllegalArgumentException("Wrong command: your command should start with HTTPClient");
        }
        
        else { 
        	
            // check length of the request
            if (input.length != 4) {
               throw new IllegalArgumentException("Wrong command expecting 4 arguments");
            }
            
            // check the Command given
            this.setCommand("Zero");
            if (input[1].equals("HEAD")) {
                setCommand(input[1]);
            }
            if (input[1].equals("GET")) {
                setCommand(input[1]);
            }
            if (input[1].equals("PUT")) {
                setCommand(input[1]);
            }
            if (input[1].equals("POST")) {
                setCommand(input[1]);
            }
            // not a valid command
            if (getCommand().equals("Zero")) {
            	throw new IllegalArgumentException("Wrong command: expecting HEAD GET PUT POST");
            }

            //Getting the path and host (without http:// the uri class doesn't work properly)
    		//Assumption: the given uri is of the form (http://)host/path
    		String addressStr = input[2];
    		if(!addressStr.startsWith("http://")){
    			addressStr = "http://" + addressStr;
    		}
    		    	
    		URI address = new URI(addressStr);
    		//The path of the uri
    		setPath(address.getPath());
    		//The full uri
            setURI(address);

            setFilePath(getPath());
    		if (!getPath().startsWith("/")){
    			setPath("/" + getPath());
    		}
    		setHost(address.getHost());
            
    		//Set port
    		if (!address.getHost().contains("localhost") && Integer.parseInt(input[3])!=80){
    			throw new IllegalArgumentException("The port number for remote servers should be 80");
    		}
    		setPort(Integer.parseInt(input[3]));
            
            

        }
    }




    /**
     * Set the file path of this request to path2.
     * @param path2
     */
    public void setFilePath(String path2) {
		this.filePath = path2;
		
	}
    
    /**
     * 
     * @return filePath
     */
    public String getFilePath(){
    	return filePath;
    }


	/**
	 * Get the path of the uri of this request
	 * @return
	 */
	public String getPath() {
		return this.path;
	}


/**
 * 
 * @param path
 */
	private void setPath(String path) {
		this.path = path;
	}



	/**
	 * Create a request in the form of a string to send to the server.
	 * 
	 * @return request: the request to send to the server
	 * @throws IOException
	 */
	public String createRequest(BufferedReader in) throws IOException {
    	String request = new String(this.getCommand()+" "+this.getPath()+" "+"HTTP/1.1\r\n");

    	//HTTP/1.1 requires the client to add the host header field
    	String prt = Integer.toString(this.getPort());
    	request += "Host: " + this.getHost() + "\r\n\r\n";
    	
		//If the command is a put or a post command, the user needs to enter an extra string
    	//to specify the file
		if(command.equals("PUT") || command.equals("POST")){
			System.out.println("Please paste/type the data to send and hit enter: ");
			String data = in.readLine();
			request += data + "\n";

		}
		//Close the input stream
		in.close();
		return request;
    }

	/**
	 * 
	 * @return the uri of the request
	 */
    public URI getURI() {
        return uri;
    }
    /**
     * Get the uri as a string
     * @return uri as a string
     */
    public String getURIStr(){
    	return uri.toString();
    }
    /**
     * Set the uri of the request to the given input.
     * @param input
     */
    public void setURI(URI input) {
        uri = input;
    }
    /**
     * Get the command of the request.
     * @return
     */
    public String getCommand() {
        return command;
    }
    /**
     * Set the command
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }
    /**
     * 
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    /**
     * 
     * @param port
     */
    public void setPort(int port){
        this.port = port;
    }
    
    /**
     * 
     * @return host
     */
    public String getHost() {
        return host;
    }
    /**
     * 
     * @return port number
     */
    public int getPort(){
        return port;
    }
    
    /**
     * 
     * @param socket
     * @param filesize
     * @param filename
     * @throws IOException
     */
 /*   public static void receiveFile(Socket socket,int filesize,String filename) throws IOException
    {
        //after receiving file send ack
        System.out.println("waiting ");
        // int filesize=70; // filesize temporary hardcoded

        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;
// localhost for testing
        System.out.println("Connecting...");

// receive file
        byte [] mybytearray  = new byte [filesize];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray,0,mybytearray.length);
        current = bytesRead;
        System.out.println("recv..."+mybytearray.length);
        do {
            bytesRead =
                    is.read(mybytearray, current, (mybytearray.length-current));
            System.out.println(bytesRead);
            if(bytesRead > 0) current += bytesRead;
        } while(bytesRead > 0);

        bos.write(mybytearray, 0 , current);
        bos.flush();
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        bos.close();
        System.out.println(" File received");
    }*/

}


