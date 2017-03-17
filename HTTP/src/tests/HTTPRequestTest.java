package tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

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

}
