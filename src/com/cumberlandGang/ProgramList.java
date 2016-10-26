package com.cumberlandGang;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * ProgramList is the model for the database. It keeps track of the programs, users, and their total times.
 */
public class ProgramList {
    private class Program {
        public String processName;
        public Period processTime;

        public Program(String name, Period time) {
            processName = name;
            processTime = time;
        }
    }

    public List<Program> programs;

    public ProgramList() {
        programs = new ArrayList<>();
    }
}
