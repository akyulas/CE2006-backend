package application;

import com.google.common.collect.Lists;
import com.sun.org.apache.regexp.internal.RE;
import constants.Constants;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.apache.commons.lang.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.InternetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by jodiakyulas on 31/10/18.
 */
@RestController
public class PasswordRecoveryManager {

    @Autowired
    EmailService emailService;

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public String checkForValidEmail(@RequestParam("m") String email) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String query = "SELECT COUNT(*) FROM users WHERE email='" + email + "'";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            if (Integer.parseInt(rs.getString(1)) == 1) {
                sendEmail(email);
                return "Succeeded";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    @RequestMapping(path = "/password/{email}/{password}", method = RequestMethod.GET)
    public String changePassword(@PathVariable String password, @PathVariable String email) {
        try {

            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String saltedHashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            String query = "UPDATE users SET password = '" + saltedHashedPassword + "' WHERE email='" + email +"'";
            st.executeUpdate(query);

            return "Succeeded";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    @RequestMapping(path = "/passcode/{passcode}/{email}", method = RequestMethod.GET)
    public String validatePassCode(@PathVariable String passcode, @PathVariable String email) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String query = "SELECT * FROM passcode WHERE email='" + email + "'";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            String lol = rs.getString(2);
            if (rs.getString(2).equals(passcode)) {
                conn.close();
                return "Succeeded";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    private void sendEmail(String emailAddress) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String passcode = getRandomString();
            String emailBody = getEmailBody(passcode);

            Email email = DefaultEmail.builder()
                    .from(new InternetAddress("bloodbankforce2006@gmail.com", "Blood Bank Team"))
                    .to(Lists.newArrayList(new InternetAddress(emailAddress)))
                    .subject("Recovery passcode")
                    .body(emailBody)
                    .encoding("UTF-8").build();

            emailService.send(email);

            String query = "SELECT COUNT(*) FROM passcode WHERE email='" + emailAddress + "'";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            if (Integer.parseInt(rs.getString(1)) == 1) {
                query = "UPDATE passcode SET passcode = '" + passcode + "' WHERE email='" + emailAddress +"'";
                st.executeUpdate(query);
            } else {
                query = "INSERT INTO passcode " + "VALUES ('" + emailAddress + "', '" + passcode + "')";
                st.executeUpdate(query);
            }

            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmailBody(String passcode) {
        return "This is your passcode: " + passcode + ". Please enter it in the app.";
    }

    private String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(15);
    }



}
