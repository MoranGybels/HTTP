import java.io.*;
import java.net.*;
import java.util.ArrayList;

class HTTPClient{
    private boolean error;
    private String URI;
    private String command;
    private String version;
    private PrintWriter out;
    private ArrayList<String> content;


    public static void main(String argv[]) throws Exception{
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        
        String sentence = inFromUser.readLine();
        analyse(sentence);


    }

    private static void analyse(String sentence) throws IOException {
        String[] words = sentence.split("\\s+");
        if (words[0].equals(new String("HTTPClient"))){ //index 1 tot 10
            System.out.println("ok");
            if (words[1].equals(new String("GET"))){
                String uri = words[2];
                if (uri.startsWith("http://")){
                    uri = uri.substring(7);
                }
                int index = uri.indexOf('/');
                String host = uri;
                if (index!=-1) {
                    host = uri.substring(0, index);
                }
                int port = Integer.parseInt(words[3]);
                //System.out.println(host+" "+ uri+" ");
                Socket clientSocket = new Socket(host, port);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.writeBytes("GET "+ uri+ " HTTP/1.1" +"\r\n"+ "Host: "+host +"\r\n\r\n");
                //String modifiedSentence = inFromServer.readLine(); //TODO: meerdere lijnen lezen
                receiveFile(clientSocket,100,uri);
                //System.out.println("FROM SERVER: " + modifiedSentence);
                clientSocket.close();
            }

        }

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
