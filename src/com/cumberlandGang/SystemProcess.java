package com.cumberlandGang;

import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * SystemProcess represents a process that is either running or has
 * run on the computer.
 * @author natepisarski <natahnpisarski@gmail.com>, rypriore
 */
public class SystemProcess {

    /**
     * The name of the current process (i.e "firefox")
     */
    private String processName;

    /**
     * The command which started this process.
     * Note: For programs in the running user's $PATH, the command and path
     * will be the same. For programs which are not, the full path will appear here
     *  i.e: /opt/google/bin/google-chrome
     */
    private String processPath;

    /**
     * The time when the process started. This is tracked by the
     * Operating System. Unfortunately, the DATE when a program began running is not
     * tracked, so the date that Programmly began running will be assigned to the
     * processStartTime.
     */
    private Date processStartTime;

    /**
     * The date / time when the process ended. This is tracked
     * via isProcessDead(), and further handled with the
     * SystemProcessTracker
     */
    private Date processEndTime;

    /**
     * The underlying Process information.
     * For programs which are built from programs already running on the
     * computer, this provides access to advanced features not originally
     * intended by SystemProcesses.
     */
    private ProcessInfo underlyingProcess;

    /**
     * The default SystemProcess constructor. This will create a
     * reference to a process that is not confirmed to be running on the
     * computer. As such, no underlyingProcess is generated.
     * @param procName The process name
     * @param procPath The path of the running executable
     * @param start The time the process started
     */
    public SystemProcess(String procName, String procPath, Date start) {
        processName = procName;
        processPath = procPath;

        processStartTime = start;
        processEndTime = null;
    }

    /**
     * Creates a SystemProcess using information about a running process on
     * the computer. This will atuomatically assign the date of said process
     * to have started running today, and the time when it started.
     *
     * This also assigns the name, path, and underlyingProcess.
     * @param info The org.jutils.jprocess.JProcessInfo object to scrape
     */
    public SystemProcess(ProcessInfo info) {

        String procName = info.getName();
        String procPath = info.getCommand();

        LocalDateTime currentDate = LocalDateTime.now();

        // Prefixes the date of this program with today's date in MM/DD/YYYY
        String datePrefix = ""
                + currentDate.getMonthValue()
                + "/"
                + currentDate.getDayOfMonth()
                + "/"
                + currentDate.getYear()
                + " "; // Leaves space for the time of a running process

        // Format is expected to be MM/dd/yyyy kk:mm:ss
        // Example: 02/28/2016 14:26:10
        String processStartString = datePrefix + info.getStartTime();

        // M = month, d=day, y=year, k=24-hour-time hour, m=minute, s=second
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");

        Date startDate = null;

        // Try to parse the combination of today's date and the processes time into a Date object
        try {
            startDate = df.parse(processStartString);
        } catch (ParseException e) {
            System.err.println("[Error! SystemProcess 59:0] SimpleDateFormat incorrectly matches process StartDate.");
            e.printStackTrace();
        }

        this.processName = procName;
        this.processPath = procPath;

        this.processStartTime = startDate;
        this.processEndTime = null; // Don't actually know the EndTime yet

        this.underlyingProcess = info;
    }

    /**
     * Gets the process from the computer using the Process ID
     * @param PID The process-id (as an integer) of the running process
     * @return A constructed SystemProcess
     */
    public static SystemProcess getProcessByPID(int PID) {
        return new SystemProcess(JProcesses.getProcess(PID));
    }

    /**
     * Creates a SystemProcess using the name of the process itself.
     * In the event that multiple processes of the same name are found,
     * the item with the lowest PID is selected
     * @param processName The name of the process to look for
     * @return The SystemProcess object
     */
    public static SystemProcess getProcessByName(String processName) {
        List<ProcessInfo> runningProcesses =  JProcesses.getProcessList();

        // Go through all processes, searching for the name
        for(ProcessInfo inf : runningProcesses) {
            if(inf.getName().equals(processName)) {
                return new SystemProcess(inf);
            }
        }

        return null;
    }

    /**
     * Test to see whether or not the running process is dead. This will automatically
     * adjust the EndTime if it's deemed to have been exited.
     * @return true if the process is no longer running on the system, false otherwise
     */
    public boolean isProcessDead() {
        List<ProcessInfo> runningProcesses = JProcesses.getProcessList();
        boolean hasEnded = true;

        for(ProcessInfo i : runningProcesses) {
            if(new SystemProcess(i).equals(this))
                hasEnded = false;
        }

        if(hasEnded)
            processEndTime = Util.nowToSystemTime(); // Set the EndTime if it's dead

        return hasEnded;
    }

    @Override
    public boolean equals(Object otherProcess) {
        if(!(otherProcess instanceof SystemProcess))
            return false;

        SystemProcess secondProc = (SystemProcess) otherProcess;

        // This will compare the processes based on name, command, and start time
        return
                secondProc.processName.equals(this.processName)
                && secondProc.processStartTime.equals(this.processStartTime)
                &&  secondProc.processPath.equals(this.processPath);

    }
}
