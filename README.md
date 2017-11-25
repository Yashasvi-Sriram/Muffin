Find the description, objectives and features [here](https://docs.google.com/document/d/1T4aY3V217nW8-nuqcLnUf_MiMWuC4puRzr35suoU8S8/edit?usp=sharing)

Muffin (Movie Buff inc.)
=
1. Open this as project in eclipse
2. All required jar files are in DEPENDENCIES = `WebContent/WEB-INF/lib/` dir
3. Configure the build path with all jars in DEPENDENCIES dir
4. Create a new tomcat 8.5 server
5. Add all the jars in DEPENDENCIES dir to bootstrap of tomcat server
6. Lombok library needs to be configured specially. you can find the manual [here](https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/)
	a. open terminal in DEPENDENCIES dir
	b. run java -jar lombok.jar
	c. a window shoud popup, it will ask for eclipse binary location
	d. give the eclipse binary location and click install/update
	e. if the installer shows installation successful, then lombok is configured for eclipse
	f. restart eclipse if above step showed installation successful
7. /src/org/muffin/muffin/db/DBConfig.java contains db credential fields
8. All sql files are in `sql/` dir
9. `reset.sql` re-sets the tables, `init.sql` initializes tables with some sample data
10. `big_init.sql` has large data set
11. Run `./src/org/muffin/muffin/RecommendatioSystem.py` with appropriate db config (in first few lines) to setup Automated suggestions
       1. This script uses Machine Learning to give movie & muff suggestions to muff
       1. As it is ML code it may take quite some time to execute
12. Run server
