package com.cumberlandGang;

import java.sql.*;

/**
 * The DatabaseManager handles operations pertaining to
 * the database. Things such as figuring out the total time,
 * updating the total time of one program, etc.
 */
public final class DatabaseManager {

    /**
     * The connection to the SQLite database
     * connection. This is shared across all instances of DatabaseManager
     */
    private static Connection connection;

    /**
     * The current instance of this Singleton
     */
    private static DatabaseManager instance;

    /**
     * The constructor used to establish the class
     * @param conn The connection to the database
     */
    private DatabaseManager(Connection conn) {
        connection = conn;
    }

    /**
     * Gets the connection to the database. Do note that there is
     * currently no safety implemented, and multiple queries at once may
     * cause unexpected behavior.
     *
     * @param path The path to the database that the Manager should be connecting to
     * @return The database connection
     * @throws SQLException if the connection to the database fails for some reason
     */
    private static synchronized DatabaseManager getConnectionInstance(String path) throws SQLException
    {
        try {
            if (connection.getMetaData().getURL().equals(path)) {
                instance = new DatabaseManager(connection);
            }
            else
                throw new SQLException(""); // This is a pretty godawful idea, but for the time being it goto's
        } catch(SQLException sqlException) {

            /*
             * This happens when the requested path does not match that of the current database,
             * or when no connection has already been established
             */

            if(connection == null) { // There has been no established connection
                connection = DriverManager.getConnection("jdbc:sqlite:"+path);

                instance = new DatabaseManager(connection);
            } else { // There is an established connection, but the paths differ

                // The instance must be closed first
                connection.close();

                connection = DriverManager.getConnection("jdbc:sqlite:"+path);

                instance = new DatabaseManager(connection);
            }
        } finally {
        }

        return instance;
    }
}
