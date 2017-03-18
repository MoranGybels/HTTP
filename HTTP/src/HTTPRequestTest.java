
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import java.net.*;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
public class HTTPRequestTest {
	
	@Test
    public void testHTTPRequestWrong() throws IllegalArgumentException, IOException, URISyntaxException {
        HTTPRequest wrong = new HTTPRequest("HTTPCLIENT GET www.google.com 80");
        
	}
	
	@Test
	public void testHTTPRequestRight() throws IllegalArgumentException, IOException, URISyntaxException {
		HTTPRequest right = new HTTPRequest("HTTPClient GET http://www.google.com/index.html 80");
		System.out.println(right.createRequest(new BufferedReader(new Reader())));
		
	}
	
	@Test
	public void testuri() throws URISyntaxException, IllegalArgumentException, IOException{
		URI google= new URI("http://www.google.com/index.html");
		System.out.println(google.getPath());
		System.out.println(google.getPort());
		HTTPRequest goog = new HTTPRequest("HTTPClient GET tcpipguide.com 80");
		
		System.out.println(goog.getFilePath());
		System.out.println(goog.getPath());

	}
	
	@Test
	public void testfile(){
		File file = new File("localhost/index.html");
		try{ new Scanner(file);
			
		}
		catch(FileNotFoundException hallo){
			System.out.print("exc");
			//Path filePath = FileSystems.getDefault().getPath("localhost", "index.html");

		//System.out.println(Files.exists(filePath));
	}
	}

}
