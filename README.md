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

Download from https://distributions.informationgrid.eu/ingrid-iplug-wfs-dsc/
 
or

build from source with `mvn clean package`.

Execute

```
java -jar ingrid-iplug-wfs-dsc-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at http://www.ingrid-oss.eu/ (sorry only in German)


Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-iplug-wfs-dsc/issues
- Source Code: https://github.com/informationgrid/ingrid-iplug-wfs-dsc
 
### Setup Eclipse project

* import project as Maven-Project
* right click on project and select Maven -> Select Maven Profiles ... (Ctrl+Alt+P)
* choose profile "development"
* run "mvn compile" from Commandline (unpacks base-webapp) 
* run de.ingrid.iplug.wfs.dsc.WfsDscSearchPlug as Java Application
* in browser call "http://localhost:10015" with login "admin/admin"

### Setup IntelliJ IDEA project

* choose action "Add Maven Projects" and select pom.xml
* in Maven panel expand "Profiles" and make sure "development" is checked
* run de.ingrid.iplug.wfs.dsc.WfsDscSearchPlug
* in browser call "http://localhost:10015" with login "admin/admin"

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
