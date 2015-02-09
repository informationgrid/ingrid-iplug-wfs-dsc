WFS iPlug
========

The WFS-iPlug connects a Web Feature Service to the InGrid data space.

Features
--------

- harvests a Web Feature Service at a certain schedule
- flexible indexing functionality
- provides search functionality on the harvested data
- GUI for easy administration


Requirements
-------------

- a running InGrid Software System

Installation
------------

Download from https://dev.informationgrid.eu/ingrid-distributions/ingrid-iplug-wfs-dsc/
 
or

build from source with `mvn assembly-single`.

Execute

```
java -jar ingrid-iplug-wfs-dsc-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at https://dev.informationgrid.eu/


Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-iplug-wfs-dsc/issues
- Source Code: https://github.com/informationgrid/ingrid-iplug-wfs-dsc
 
### Set up eclipse project

```
mvn eclipse:eclipse
```

and import project into eclipse.

### Debug under eclipse

- create a `conf` directory in the projects root directory
- copy the following files from `test/resources` into the directory `/conf`
  - ingrid.auth
  - plugdescription.xml
  - communication.xml
  - log4j.properties
- execute `mvn install` to expand the base web application
- set up a java application Run Configuration
- start Class is `de.ingrid.iplug.dsc.JettyStarter`
- add the VM argument `-Djetty.webapp=src/main/webapp` to the Run Configuration
- add src/main/resources to class path


Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
