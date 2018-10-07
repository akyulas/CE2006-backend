package application;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


/**
 * Created by jodiakyulas on 29/9/18.
 */
@RestController
public class APIWrapper {

    private String APIURL = "https://data.gov.sg/api/action/datastore_search?resource_id=c2f8f2c0-d7ad-4c9e-8d8b-250c342a1d6c&limit=5";

    @RequestMapping(value = "/queryAPI", method= RequestMethod.GET)
    public String queryAPI() throws IOException {
        System.setProperty("http.agent", "Chrome");
        URL url = new URL(APIURL);
        URLConnection conn = url.openConnection();
        Scanner scanner = new Scanner(conn.getInputStream());
        return scanner.useDelimiter("\\A").next();
    }
}
