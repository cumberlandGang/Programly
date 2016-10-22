# Welcome to Programly
Programmly is a piece of producivity software in active development. The aim of the project is to allow you, the user,
to track how long you spend in each app, and to set a time limit on a per-app basis so you don't spend too much time
faffing about.

# Developer Notes / Contributing
Programmly is under active development. For a detailed overview of the project idea, see below:

## IDE
The IDE used is IntelliJ. IntelliJ users will be able to interact with the project by simply selecting an SDK,
and checking the project out from git. In that case, the dependencies for the project are already handled. Automagically.

For other IDE users, such as Eclipse or Netbeans, you may find it difficult to interact with this project. The best
approach for an Eclipse user would be to import the src directory into a new project.

## Project Structure

**Bold** = class / interface
*italics* = method / field

Programmly relies on two other software projects:

* WMI4Java
* JProcess

WMI4Java is JProcess's dependency. Jprocess allows us to pull processes from the running computer.

These processes are turned into **SystemProcess**'s via the **SystemProcessTracker**. 

## SystemProcess
**SystemProcess** take a process from JProcess, and does a pretty simple conversion to make it into a **SystemProcess**.
The **SystemProcess** simply contains information about the process, including:

* Name
* Path it's been launched from
* Starting time

As well as a reference to the underlying JProcessInfo too.

## SystemProcessTracker
**SystemProcessTracker** runs on its own thread. This allows it to discontinue its own execution without slowing the
rest of the program (which will eventually be doing database stuff) down. **SystemProcessTracker** is not yet written, but
one forseeable blueprint of it is:

* List<SystemProcess> trackedProcesses
* no-arg constructor
* initialize method (static?)
* update (asynchronous. i.e, the list should be locked with a private semaphore to prevent main / SPT racing)
* updateDatabase("programName", Date time) (database should be set in Main.java)

## History
**History** provides access to the database. It wil skim through the things that the SystemProcessTracker put into there
to discern things such as:

* What is the most-used program?
* Provide me a list, sorted, of the most used programs
* Delete all records for (insert program here)
* Blacklist this program

## Main
The Main method should serve two purposes. It should enable you to get information from a running instance of
Programmly, or start a new server instance of Prorammly. The information it should be able to get should be 1-1 with History.


# Developer Notes continued - Database Schema
At the moment, the proposed Schema for the database is:

processHistory(**processName**: Text, totalTime: Date)
blacklisted(**processName**: Text)

# License
Programmly is licensed under the BSD 3-clause license