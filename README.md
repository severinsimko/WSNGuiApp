# WSNGuiApp
Main class is in package mainparts and the most important classes are in package controllers, in underdevelopment are only classed used for testing purposes. The app was developed in NetBeans IDE.

## Prerequisites:

* You have to have mysql installed.
* In mainparts/DBConnection you can configure the credentials for DB
* From default we used following: user: WSN pass:123 and DB testdb with table measureddata 
* If you want to create a table use this schema: (create table measureddata (id INT NOT NULL,date TIMESTAMP(3),light INT,accelx INT,accely INT,accelz INT,temperature INT,humidity INT);)


I attached 2 scripts *insertDB* and *insertDB2*, I used them to simulate the real-time behaviour.
 From linux environment, this commands can be used to simulate it:
```
watch -n 1 bash insertDB
```
and similarly,
```
watch -n 1 bash insertDB2
```
If you want to have both graphs in real-time light graph you have to run both commands at the same time and the commands will every second fill the DB with random data, one for sensor id 1 and the second for id 2.

## Recommended steps to run the APP:
* install and configure the mysql DB as written above
* watch -n 1 bash insertDB from command line 1
* watch -n 1 bash insertDB2 from command line 2
* run the application- so you will see the real-time behaviour

If you want to run the application with real base-station and sensors you have to uncomment the main method in mainparts/ApplicationDisplay as following:
```
try {

            testSens.start();
            config = new ControlPanelConfiguration(testSens);
        } catch (Error e) {

            e.printStackTrace();
        } finally {

            launch(null);
        }
```

try-catch block has to be uncommented.
 
Visit my [LinkedIn](https://www.linkedin.com/in/severinsimko) profile .
