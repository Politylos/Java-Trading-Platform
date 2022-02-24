# CAB302-TradingPlatform
CAB302 Trading Platform repository
<h1> Documentation Update</h1>
<p> A java docs have been created for both the server and client java packages. This can be found in Documentation/client and Documentation/Server_Server</p>

<h1>Client and Server_Server Split</h1>
<p> the client file will contains all the UI for the program. While the server holds the database connectivity and final say in all sale requests and posts</p>
<h3>Client</h3>
<p> the client is the users access to the trading platform, this is the graphical user interface, helper functions to connect to the server to manipulate requests and fetch current data.</p>
<h3> Server_Server</h3>
<p>The server connects the database a processes all users requests through queing up actions and preforming them in a first in first out queue.</p>


<h1>DATABASE SETUP</h1>
<h2>DATABASE SETUP USING MARIADB</h2>
<h4>Only useful if you have no idea how to set up a database you can skip to the config of the java connector otherwise </h4>
the current database has to be configured for each individual machine. This can be done through using your database server of choice (easiest is MariaDB, since you need to change the least amount of config). 
If you don’t have a MariaDB installed or your preferred database manager here’s the link from blackboard: https://downloads.mariadb.org/ 
Run the installer and you should be promoted at some point to create the password for the root user, make sure you remember it as it will need o be used to connect to the database. 
After that is all set up you can open heidisql, from here you can create a new user profile, the default IP, port and root user should be filled in all you need to do is add the password for root click open.

<h3>How to change the config file for your own MariaDB server RECOMENDED</h3>
Now you can import the sql file from git download or copy the file code to your computer. From heidisql than go file>Load or Run sql file, this should than add the table tradingplatform (might error but you can ignore). Just check that users or organisations have a single data entry each. That is than the database server side of setup done. 
To make the java program connect to the server you than need to edit Server_Server/DBConfig.cfg (just a text file). This data can be filled with the data from your heidisql user, so leave user as root for now and change password to your selected root password. I would also check the IP in the host as I’m not sure if that charges the rest of it can be kept the same though. Save the file you should than be able to connect to the database from java. <br>
<b>DBConfig.cfc connenets \/<br>
host=jdbc:mariadb://{YOUR servers IP}/TradingPlatform?usePipelineAuth=false<br>
username=root<br>
password= password for root<br>
driver=org.mariadb.jdbc.Driver<br>
<h2>Other database SERVER configs</h2></b>
if you have an other preffred database server you will need to change most of the config file I have placed the values you need to fill with your own values below.
Just a quick note if you decided to do this you will need to source your own library java file to connect to your database server if you dont know what it is just search it.<br>
<b>DBConfig.cfc connenets \/<br>
host=jdbc:{your choosen database server}://{YOUR servers IP}/{database name}?usePipelineAuth=false<br>
username={username to connect to server (default is root)}<br>
password= {password for username}<br>
driver={java connector library to access and understand the database}<br></b>
  
<h2>Testing what it should look like </h2>
To test it Run the Run.java file if it works you should get the following console output<br>
-------------------------------<br>
DRIVER: org.mariadb.jdbc.Driver<br>
CONNECTION: org.mariadb.jdbc.MariaDbConnection@5b37e0d2<br>
Shutting down Server_Server<br>
$2a$10$xbSFGBdqbBQgCGNwA3rg9OTlp1.tRxpGVtZA.EU9rQIq2hoaYGkjG<br>
It matches<br>
------------------------------------<br>
<h2>the most likely problem if it doesn't work</h2>
If that doesn’t work you might need to add mariadb-java-client-2.7.2.jar to you libraries, you can find the file in server>libs right click on it and click on add as library. If this was the problem it should run now. If not well IDK, google it or just ask. 

<h1>Note on hashing the passwords</h1>
also on a final note if you want to add any users manually make sure any user’s passwords in the database is the hashed version otherwise you want be able to login once the program becomes more developed
