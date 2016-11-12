package com.cumberlandGang;

import java.sql.*;
import java.util.ArrayList;

/*
* The database can be located anywhere on the user's system, in a sqlite file.
* The schema of the database is as such:
* processes(_processName_: string, processTime: int) (_x_ is underline, not part of the identifier)
*
* The time is kept in seconds, so you may wish to perform some math to put it in a more human-readable format.
*
 */

/**
 * The DatabaseManager handles operations pertaining to
 * the database. Things such as figuring out the total time,
 * updating the total time of one program, etc.
 */
public final class DatabaseManager {

    /**
     * The string used to connect to the database
     */
    private static final String SQLITE_CONNECTION_STRING = "jdbc:sqlite:";

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
    public static synchronized DatabaseManager getConnectionInstance(String path) throws SQLException
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

            // In either of the excepted cases, a new database was created. Thus, we must initialize it.
            instance.initializeDatabase();
        }

        return instance;
    }

    /**
     * Runs the update command with the given SQL.
     * @param sql The SQL to run with the update plan
     */
    protected void runUpdate(String sql)
    {
        try {
            Statement statement = connection.createStatement();

            statement.executeUpdate(sql);
            statement.close();
        } catch(SQLException exception) {
            System.out.println(exception.getStackTrace());
        }
    }

    /**
     * Initializes the database with the format adhered to by this program.
     * That is, create one table (process) with a field for name and time.
     */
    public void initializeDatabase()
    {
        runUpdate( "CREATE TABLE PROCESS " +
                "(NAME TEXT PRIMARY KEY     NOT NULL," +
                " TIME           INT    NOT NULL); ");
    }

    /**
     * Creates a ProgramList Object from the PROCESS table in the database.
     * @return
     */
    public ProgramList readProgramList() {

        ProgramList pList = new ProgramList();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM PROCESS;");

            while (rs.next()) {
                String processName = rs.getString(0);
                int totalTime = rs.getInt(1);

                pList.addProgram(processName, totalTime);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return pList;
    }

    /**
     * Write a ProgramList to the database
     * @param list The list to write into the database
     */
    public void writeProgramList(ProgramList list) {
        try {
            Statement deleteStatement = connection.createStatement();
            deleteStatement.execute("DROP TABLE Programs;");

            initializeDatabase();

            for(ProgramList.Program program : list.programs) {
                deleteStatement.execute(
                        "INSERT INTO PROCESS ('" + program.processName + "','" + program.processTime + "')");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
