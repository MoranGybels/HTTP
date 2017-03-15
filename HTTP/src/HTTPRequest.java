import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class HTTPRequest {
    private String host;
    private int port;
    private boolean error;
    private URI uri;
    private String command;
    private String version;
    //private PrintWriter out;
	private String path;


    public HTTPRequest(String sentence) throws IOException, IllegalArgumentException, URISyntaxException {
        String[] input = sentence.split(" ");
        if (!input[0].equals(new String("HTTPClient"))){
        	throw new IllegalArgumentException("Wrong command: your command should start with HTTPClient");
        }
        else { //index 1 tot 10
        	
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

            //Getting the path and host
    		//Assumption: the given uri is of the form (http://)host/path
    		String addressStr = input[2];
    		if(!addressStr.startsWith("http://")){
    			addressStr = "http://" + addressStr;
    		}
    		    	
    		URI address = new URI(addressStr);
    		setPath(address.getPath());
    		
            setURI(address);

    		if (!getPath().startsWith("/")){
    			setPath("/" + getPath());
    		}
    		setHost(address.getHost());


//            if (getURI().startsWith("http://")){
//                setURI(getURI().substring(7));
//            }
//
//            //set host
//            int index = getURI().indexOf('/');
//            setHost(getURI());
//            if (index!=-1) {
//                setHost(getURI().substring(0, index));
//            }

            
    		//Set port
            setPort(Integer.parseInt(input[3]));
            //TODO: als er geen poort gespecifieerd is moet dit 80 worden?
            
            

        }
    }





    private String getPath() {
		return this.path;
	}



	private void setPath(String path) {
		this.path = path;
	}



/**
 * Create a request in the form of a string to send to the server.
 * @return request: the request to send to the server
 * @throws IOException
 */
	public String createRequest() throws IOException {
    	String request = new String(this.getCommand()+" "+this.getPath()+" "+"HTTP\1.1\r\n");

    	//HTTP/1.1 requires the client to add the host header field
    	String prt = Integer.toString(this.getPort());
    	request += "Host: " + this.getHost() + ":" + prt + "\r\n\r\n";
    	
		//If the command is a put or a post command, the user needs to enter an extra string
    	//to specify the file
		if(command.equals("PUT") || command.equals("POST")){
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please paste/type the data to send and hit enter:");
			String data = inFromUser.readLine();
			inFromUser.close();
			request += data + "\n";

		}
		
		return request;

    	/*
    	receiveFile(clientSocket,100,getURI());//TODO:nu komen hier de eerste 100 karakters en daarna worden ze geprint :)
        String fromServer;
        System.out.println("FROM SERVER: ");
        // Read text from the server and write it to the screen.
        try{
            while ( (fromServer = inFromServer.readLine()) != null) {

                System.out.println(fromServer);
            }
        } catch (IOException e){}
*/

    }


    public URI getURI() {
        return uri;
    }
    public String getURIStr(){
    	return uri.toString();
    }

    public void setURI(URI input) {
        uri = input;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(int port){
        this.port = port;
    }

    public String getHost() {
        return host;
    }
    public int getPort(){
        return port;
    }

    public static void receiveFile(Socket socket,int filesize,String filename) throws IOException
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
    }

}


