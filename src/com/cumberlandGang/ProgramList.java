package com.cumberlandGang;

import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * ProgramList is the model for the database. It keeps track of the programs, users, and their total times.
 */
public class ProgramList {

    /**
     * Programs represent programs which have run on the system. They
     * track the total amount of time they have run, as well ass the name of the process
     */
    public class Program {

        /**
         * The name of the process being tracked
         */
        public String processName;

        /**
         * The total time, in seconds, that the program has run
         */
        public long processTime;

        /**
         * A representation of a program
         * @param name The name of the program
         * @param time The time, in seconds, that the program has run
         */
        public Program(String name, long time) {
            processName = name;
            processTime = time;
        }

        /**
         * Adds some number of seconds to this program list
         * @param seconds The number of seconds to add
         */
        public void addDuration(long seconds) {
            processTime += seconds;
        }

        /**
         * Adds the given Duration to the current Program
         * @param dur The duration to add
         */
        public void addDuration(Duration dur) {
            addDuration(dur.getSeconds());
        }
    }

    /**
     * The list of programs
     */
    public List<Program> programs;

    /**
     * Default constructor, which simply initializes the list
     */
    public ProgramList() {
        programs = new ArrayList<>();
    }

    /**
     * Adds the given program to this program list
     * @param program The program to add to the list
     */
    public void addProgram(Program program){
        programs.add(program);
    }

    /**
     * Add a program to this list given its component parts
     * @param name The name of the program
     * @param time The time, in seconds, that the program has been running
     */
    public void addProgram(String name, long time){
        programs.add(
                new Program(name, time)
        );
    }

    public void addFrom(List<SystemProcess> processList) {
        processList.forEach(
                (x ->
                        programs.add(
                                new Program(x.getProcessName(), x.getProcessLifetime().getSeconds())
                        )
                )
        );
    }
}

/*
* TODO: Implement List for ProgramList
*/