package com.cumberlandGang;

import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The SystemProcessTracker controls the monitoring of SystemProceseses.
 * The tracker can initialize a list of SystemProcesses from the system and
 * monitor their livelihood.
 */
public class SystemProcessTracker {

    /**
     * The processes from the system
     */
    private List<SystemProcess> processes;

    /**
     * A list of dead processes
     */
    private List<SystemProcess> deadProcesses;

    /**
     * The database, which will be updated when processes end.
     */
    private DatabaseManager database;

    /**
     * Creates a new SystemProcessTracker from a list of processes.
     * @param processes The processes to use for this tracker
     * @param manager The manager for the database processes
     */
    public SystemProcessTracker(List<SystemProcess> processes, DatabaseManager manager)
    {

        this.processes = processes;
        this.database  = manager;

        deadProcesses = new ArrayList<>();
    }


    /**
     * Initializes a list of processes from the operating system
     * @param dbPath The path to the database file, or its desired location
     * @return A new Tracker with all processes. This includes child processes
     */
    public static  SystemProcessTracker initialize(String dbPath)
    {
        List<ProcessInfo> osProcesses = JProcesses.getProcessList();
        List<SystemProcess> convertedProcesses = new ArrayList<>();

        // Add all operating system processes to this tracker
        osProcesses.forEach(
                (x -> convertedProcesses.add(
                        new SystemProcess(x)
                ))
        );

        // Attempt to connect to the database
        DatabaseManager db = null;
        try {
            db = DatabaseManager.getConnectionInstance(dbPath);
        } catch (SQLException exception) {
            System.err.println("[SystemProcessTracker.java]: Something went wrong when trying to establish" +
                    " a database connection to " + dbPath + ". Here are some hints as to what:\n");

            System.err.println("------MESSAGE---------");
            System.err.println(exception.getMessage());

            System.err.println("------STACKTRACE------");
            System.err.println(exception.getStackTrace());

            System.err.println("------EXITING PROGRAM------");
            System.exit(1);
        }

        return new SystemProcessTracker(convertedProcesses, db);
    }

    /**
     * Iterate over the list of processes, setting the ending time for the processes which have completed.
     */
    public void updateProcesses()
    {
        for(SystemProcess process : processes) {

            // Move the process to the 'dead' queue if it's dead to save resources
            if(process.isProcessDead()) {
                deadProcesses.add(process);
                processes.remove(process);
            }
        }
    }

    /**
     * Updates the database, writing the dead processes to the database.
     */
    public synchronized void updateDatabase()
    {
        ProgramList list = new ProgramList();
        list.addFrom(deadProcesses);
        database.writeProgramList(list);
    }
}
