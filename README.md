# Java Coding Challenge - Task Management API Suite

This is a Spring Boot application built using Maven that provides an API suite 
using HTTP to allow users to manage their task data.

You can build a jar file from the command line in the project root directory:
  
```
mvn package
```

You can run the application from Maven using the Spring Boot Maven plugin:

```
mvn spring-boot:run
```

This project uses an embedded Tomcat server to host the application
that will be available at [http://localhost:8080](http://localhost:8080)  

An in-memory (H2) database is used, which gets populated with data on startup. 
The database is available via the console `http://localhost:8080/h2-console/` 
where it is possible to view database tables using the JDBC URL `jdbc:h2:mem:testdb`.

A H2 database is used since it supports `IF EXISTS`, which derby does not.
This allows the database to be conditionally dropped if required before being created by the
Spring Framework when the application starts up.

The table structure is provided below:

**Table name** - *tasks*

**Table columns:**
- *id* int not null generated always as identity,
- *title* varchar(256) not null,
- *description* varchar(1024),
- *due_date* date,
- *status* varchar(10),
- *creation_date* date not null,
- *primary key (id)*


## Implementation

The API provides functionality for the following actions: 
 
1. Fetch all tasks.
1. Fetch all overdue tasks.
1. Fetch data for a single task.
1. Add a new task.
1. Modify a task.
1. Delete a task.


## Pre-requisites
1. Java 8 needs to be installed on the system and environment variable JAVA_HOME should be set correctly to the JDK path.  
   Check by running below command in command prompt  
   `java -version`  
   
2. Maven needs to be installed on the system.  
   Check by running below command in command prompt  
   `mvn -v`  

