package com.cumberlandGang;

import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
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


    private Instant processStartTime, processEndTime;

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
     */
    public SystemProcess(String procName, String procPath) {
        processName = procName;
        processPath = procPath;

        processStartTime = Instant.now();
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

        this.processName = procName;
        this.processPath = procPath;

        this.processStartTime = Instant.now();
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

    public String getProcessName() {
        return processName;
    }

    public Instant getProcessStartTime() {
        return processStartTime;
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

        if(hasEnded && processEndTime == null) // If processEndTime isn't null it's been checked before
            processEndTime = Instant.now();

        return hasEnded;
    }

    /**
     * Gets the period of time that this process ran for.
     * @return The Duration of this process's life
     */
    public Duration getProcessLifetime()
    {
        return Duration.between(processStartTime, processEndTime);
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
