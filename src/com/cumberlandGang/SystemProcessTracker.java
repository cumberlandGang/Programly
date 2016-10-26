package com.cumberlandGang;

import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

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
     * Creates a new SystemProcessTracker from a list of processes.
     * @param processes The processes to use for this tracker
     */
    public SystemProcessTracker(List<SystemProcess> processes)
    {
        this.processes = processes;
    }

    /**
     * Initializes a list of processes from the operating system
     * @return A new Tracker with all processes. This includes child processes
     */
    public static  SystemProcessTracker initialize()
    {
        List<ProcessInfo> osProcesses = JProcesses.getProcessList();
        List<SystemProcess> convertedProcesses = new ArrayList<>();

        osProcesses.forEach(
                (x -> convertedProcesses.add(
                        new SystemProcess(x)
                ))
        );

        return new SystemProcessTracker(convertedProcesses);
    }


}
