import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerCommand {

    private boolean error;
    private String URI;
    private String command;
    private String version;
    //private PrintWriter out;
    private ArrayList<String> content;


    public ServerCommand(ArrayList<String> clientSentences,PrintWriter out) throws IOException, URISyntaxException {
        //this.setOut(out);
        this.error = false;
        String[] input = clientSentences.get(0).split(" ");
        if (input[0].equals(new String("HTTPClient"))) { //index 1 tot 10
            System.out.println("ok");
            clientSentences.remove(0);
            this.content = clientSentences;
            // check length
            if (input.length != 3) {
                out.println("Wrong command expecting 3 arguments");
                setErrorTrue();
                return;
            }
            // check Command
            this.setCommand("Zero");
            if (input[0].equals("HEAD")) {
                setCommand(input[0]);
            }
            if (input[0].equals("GET")) {
                setCommand(input[0]);
            }
            if (input[0].equals("PUT")) {
                setCommand(input[0]);
            }
            if (input[0].equals("POST")) {
                setCommand(input[0]);
            }
            // not a valid command
            if (getCommand().equals("Zero")) {
                setErrorTrue();
                out.println("Wrong command expecting HEAD GET PUT POST");
            }

            //check URI
            setURI(input[1]);

            // check version
            if (input[2].equals("HTTP/1.0")) {
                setVersion(input[2]);
            }
            if (input[2].equals("HTTP/1.1")) {
                setVersion(input[2]);
            }
            // not a valid version
            if (input[0].equals(null)) {
                setErrorTrue();
                out.println("Wrong version expecting HTTP/1.0 HTTP/1.1");
            }
        }
    }


    public void execute() throws IOException{

        if(!isError()){
            if (command.equals("GET")){
                this.doGet() ;
            }
            else if (command.equals("HEAD")){
                this.doHead();
            }
            else if (command.equals("PUT")){
                this.doPut();
            }
            else if (command.equals("POST")){
                this.doPost();
            }

        }

    }

    private void doPost() throws IOException {

        FileWriter out = new FileWriter(getURI(), true); //append = true

        for(String i: content){
            out.write(i);
        }
        out.close();
    }


    private void doPut() {
        // TODO Auto-generated method stub

    }


    private void doHead() throws IOException {
        // file not found =404
        BufferedReader headreader = new BufferedReader(new FileReader(this.getURI()));
        String line;
        boolean print = false;
        while ((line = headreader.readLine()) != null) {

            if(line.equals("<HEAD>")) {
                print = true;
            }
            if(print == true) {
                getOut().print(line);
            }
            if(line.equals("</HEAD>")) {
                print = false;
            }

        }
        headreader.close();
        getOut().println();
    }


    private void doGet() throws IOException {
        BufferedReader getreader = new BufferedReader(new FileReader(this.getURI()));
        String line;
        while ((line = getreader.readLine()) != null) {
            getOut().println(line);
        }
        getreader.close();
    }


    public boolean isError() {
        return error;
    }

    public void setErrorTrue() {
        this.error = true;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String input) {
        URI = input;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public PrintWriter getOut() {
        return out;
    }


    public void setOut(PrintWriter out) {
        this.out = out;
    }

}

