package application;

import com.google.common.collect.Lists;
import constants.Constants;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.apache.commons.lang.RandomStringUtils;
import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.InternetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jodiakyulas on 4/11/18.
 */
@RestController
public class PointsRedemptionManager {

    @Autowired
    EmailService emailService;

    @RequestMapping(value = "/getPoints", method = RequestMethod.GET)
    public Integer checkForValidEmail(@RequestParam("m") String matricID) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String query = "SELECT points FROM users WHERE matricid='" + matricID+ "'";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            return Integer.parseInt(rs.getString(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    @RequestMapping(value = "/deductPoints/{matricID}/{deductAmount}/{merchantName}/{merchantText}", method = RequestMethod.GET)
    public String deductPoints(@PathVariable String matricID, @PathVariable String deductAmount, @PathVariable String merchantName, @PathVariable String merchantText) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String query = "SELECT points FROM users WHERE matricid='" + matricID + "'";
            ResultSet rs = st.executeQuery(query);

            rs.next();

            int userPoints = Integer.parseInt(rs.getString(1));
            int userDeductPoints = Integer.parseInt(deductAmount);
            int userFinalPoints = userPoints - userDeductPoints;


            query = "UPDATE users SET points ='" + userFinalPoints + "' WHERE matricid='" + matricID + "'";
            st.executeUpdate(query);

            query = "SELECT email FROM users WHERE matricid='" + matricID + "'";
            rs = st.executeQuery(query);
            rs.next();

            String email = rs.getString(1);
            sendVouncher(email, merchantName, merchantText);

            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAILED";
    }

    private void sendVouncher(String emailAddress, String merchantName, String merchantText) {

        try {
            Email email = DefaultEmail.builder()
                    .from(new InternetAddress("bloodbankforce2006@gmail.com", "Blood Bank Team"))
                    .to(Lists.newArrayList(new InternetAddress(emailAddress)))
                    .subject("Recovery passcode")
                    .body("")
                    .encoding("UTF-8").build();

            final Map<String, Object> modelObject = new HashMap<>();
            modelObject.put("merchantName", merchantName);
            modelObject.put("merchantText", merchantText);
            modelObject.put("vouncherNumber", getRandomString());

            emailService.send(email, "vouncher_template.ftl", modelObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(25);
    }

}
