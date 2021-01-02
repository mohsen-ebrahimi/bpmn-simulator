# BPMN Simulator and Testing Framework
This framework helps the workflow engineers to simulate their bpmn processes
and test them using various assertions.

 ## Contribution
* You should clone this project using the following URL:
```
https://github.com/mohsen-ebrahimi/bpmn-simulator.git
```
* The project uses MySQL as its repository. You need to create a schema
named `camunda-simulator`.
* Configure your database URL, username and password in `application.yml`.

Then you can either:
* execute tests by `mvn clean test` command
* or run the project using `Application.java` and check the Camunda dashboard
in the following address. Note that the default username/password is `demo/demo`.
```
http://localhost:9000/
```
