package application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import constants.Constants;
import model.Appointment;
import org.postgresql.Driver;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jodiakyulas on 13/10/18.
 */
@RestController
public class AppointmentManager {

    @RequestMapping(value = "/appointment", method= RequestMethod.POST)
    public String createAppointment(@RequestBody Appointment appointment) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String matricID = appointment.userMatricID;
            String location = appointment.location;
            String donationType = appointment.donationType;
            String address = appointment.address;
            String postalCode = appointment.postalCode;
            String appointmentDate = appointment.appointmentDate;
            String appointmentTime = appointment.appointmentTime;
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());

            String query = "INSERT INTO appointments " + "VALUES ('" + matricID + "', '" + location + "', '" + donationType
                    + "', '" + address + "', '" + postalCode + "', '" + appointmentDate + "', '" + appointmentTime + "' , '" + ts.toString() + "')";

            st.executeUpdate(query);

            conn.close();

            return "The appointment is successful.";


        } catch(Exception e) {
            e.printStackTrace();
            return "An error occured. Please try again.";
        }
    }

    @RequestMapping(value="/appointment", method = RequestMethod.GET)
    public String getAppointments(@RequestParam("m") String matricNo) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            // 8 refers to the 8th column of the database table
            String query = "SELECT * FROM appointments WHERE user_matric_id = '" + matricNo + "' ORDER BY 8 DESC";
            ResultSet rs = st.executeQuery(query);

            final JsonArray arr = new JsonArray();

            while (rs.next()) {
                final JsonObject obj = new JsonObject();

                obj.addProperty("userMatricID", rs.getString(1));
                obj.addProperty("location", rs.getString(2));
                obj.addProperty("donationType", rs.getString(3));
                obj.addProperty("address", rs.getString(4));
                obj.addProperty("postalCode", rs.getString(5));
                obj.addProperty("appointmentDate", rs.getString(6));
                obj.addProperty("appointmentTime", rs.getString(7));

                arr.add(obj);
            }

            return arr.toString();


        } catch(Exception e) {
            return "An error occured. Please try again.";
        }
    }

    @RequestMapping(value="/appointment", method = RequestMethod.DELETE)
    public String deleteAppointment(@RequestBody Appointment appointment) {
        try {
            Constants constants = new Constants();
            String databaseURI = constants.databaseURI;
            String databaseUser = constants.databaseUser;
            String databasePassword = constants.password;

            DriverManager.registerDriver(new Driver());
            Connection conn = DriverManager.getConnection(databaseURI, databaseUser, databasePassword);
            Statement st = conn.createStatement();

            String matricID = appointment.userMatricID;
            String location = appointment.location;
            String donationType = appointment.donationType;
            String address = appointment.address;
            String postalCode = appointment.postalCode;
            String appointmentDate = appointment.appointmentDate;
            String appointmentTime = appointment.appointmentTime;

            String query = "DELETE FROM appointments WHERE user_matric_id = '" + matricID + "' AND location = '" + location + "' AND  donation_type='" + donationType
                    + "' AND address = '" + address + "' AND postal_code = '" + postalCode + "' AND appointment_date = '" + appointmentDate + "' AND appointment_time = '" + appointmentTime + "'";

            st.executeUpdate(query);

            conn.close();

            return "Successfully deleted";
        } catch(Exception e) {
            return "An error occured. Please try again.";
        }
    }

}
