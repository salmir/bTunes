package at.ac.tuwien.sepm.musicplayer.persistance.h2;


import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Lena Lenz.
 * todo: delete me when database successfully tested
 */
/*public class H2DatabaseConnection {

    private static Logger logger = Logger.getLogger(H2DatabaseConnection.class);

    private static Connection connection;

    private H2DatabaseConnection() {}

    public static Connection getConnection() {
        if(connection == null) {
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/at.ac.tuwien.sepm.musicplayer", "sa", "");
                connection.setAutoCommit(false);
                logger.debug("Connected to database.");
            } catch (ClassNotFoundException e) {
                logger.error("H2 driver not found.", e);
            } catch (SQLException e) {
                logger.error("Could not open database connection.", e);
            }
            return  connection;
        } else return connection;
    }

    private static void closeConnection() {
        if(connection != null) {
            try {
                connection.commit();
                connection.close();
            } catch (SQLException e) {
                logger.error("Failed to close connection.");
            }
        }
        connection = null;
    }
}
*/