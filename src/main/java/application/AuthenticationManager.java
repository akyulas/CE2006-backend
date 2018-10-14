package application;

import constants.Constants;
import model.LoginUser;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.postgresql.Driver;

import java.sql.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by jodiakyulas on 7/10/18.
 */

@RestController
public class AuthenticationManager {

    @RequestMapping(value = "/register", method= RequestMethod.POST)
    public void registerUser(@RequestBody User user) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String matricNo = user.matricNo;
            String email = user.email;

            String saltedHashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt());

            Integer points = user.points;

            String query = "INSERT INTO users " + "VALUES ('" + matricNo + "', '" + email + "', '" + saltedHashedPassword
                    + "', " + points.toString() + ")";

            st.executeUpdate(query);

            conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/login", method= RequestMethod.POST)
    public String loginUser(@RequestBody LoginUser user) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String query;
            String userName = user.userName;
            String password = user.password;

            if (userName.matches("^(U|G)[0-9]{7}[A-Z]$")) {
                query = "SELECT password FROM users WHERE matricid = '" + userName + "'";
            } else {
                query = "SELECT password FROM users WHERE email = '" + userName + "'";
            }

            ResultSet rs = st.executeQuery(query);
            String hashPassword = "";
            if (rs.next()) {
                hashPassword = rs.getString("password");
            }
            String returnMessage = "";

            if (BCrypt.checkpw(password, hashPassword)) {
                query = "SELECT matricid FROM users WHERE password = '" + hashPassword + "'";
                rs = st.executeQuery(query);
                if (rs.next()) {
                    returnMessage = rs.getString("matricid");
                }
            } else {
                returnMessage = "Log in information is false";
            }

            rs.close();
            conn.close();

            return returnMessage;

        } catch(Exception e) {
            e.printStackTrace();
            return "An error occured";
        }
    }

}
