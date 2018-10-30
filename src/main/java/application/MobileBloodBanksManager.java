package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * Created by jodiakyulas on 30/10/18.
 */
@RestController
public class MobileBloodBanksManager {

    @RequestMapping(value = "/mobileBloodBank", method = RequestMethod.GET)
    public String getMobileBloodBanks() {
        try {
            Document doc = Jsoup.connect("https://redcross.sg/give-blood/where-to-donate-today.html").get();
            Elements contents = doc.getElementsByClass("single_drive");
            final JsonArray arr = new JsonArray();
            populateJsonArray(arr, contents);
            return arr.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void populateJsonArray(JsonArray arr, Elements contents) {
        for (Element content : contents) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("location", content.getElementsByTag("h2").get(0).text());
            final JsonArray driveTimings = new JsonArray();
            populateDriveTimings(driveTimings, content.getElementsByTag("li"));
            obj.add("driveTimings", driveTimings);
            obj.addProperty("address", content.getElementsByClass("drive_address").get(1).text());
            arr.add(obj);
        }
    }

    public void populateDriveTimings(JsonArray arr, Elements contents) {
        for (Element content : contents) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("driveDate", content.getElementsByClass("drive_date").get(0).text());
            obj.addProperty("timings", content.getElementsByClass("time1").get(0).text() + content.getElementsByClass("time2").get(0).text());
            arr.add(obj);
        }

    }
}
