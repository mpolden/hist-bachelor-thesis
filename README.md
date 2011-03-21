Awesome Finance Planner
=======================

Awesome Finance Planner is an Android application that intends to help you
easily plan and organize your finances.

This file mainly contains a few technical notes and tips that we've gathered
during the course of the project. It's in no way a complete documentation of
the project.

Git tags
--------
Tags are created after each sprint (Scrum-style sprint) is finished. The tag
format is vX.Y.Z where X, Y and Z are integers. X is the major version, Y is
the sprint number and Z is the day number in that sprint.

Git branches
------------
When implementing major components, we usually create a new branch with an
appropiate name, like 'sqlite' or 'ormlite'.

Building the project
--------------------
We use Maven to build the entire project, including both the Android app and
the server. To build it, do the following:
    cd code
    ANDROID_HOME=/path/to/android-sdk mvn package

Building only the Android app
-----------------------------
The source for the Android app is located in code/android.
To build it, do the following:
    cd code/android
    ANDROID_HOME=/path/to/android-sdk mvn package

Building and running the server
-------------------------------
Our server implementation uses the Play Framework, so a working Play
installation is required to run the server.
    cd code/server
    play run

Starting a shell on the emulator
--------------------------------
To start a shell on the emulator, use the following command:
    $ANDROID_HOME/adb -s emulator-5554 shell
To remove the existing database:
    rm /data/data/no.kantega.android/databases/app.db

IntelliJ IDEA, maven-android-plugin and Javadoc
-----------------------------------------------------------
To make IntelliJ IDEA find the Android Javadoc when using maven-android-plugin,
do the following:
* Open Project Structure (Ctrl+Alt+Shift+S)
* Navigate to Global Libraries
* Select Android 2.3.3 Platform
* Click Attach Documentation...
* Add the full path to $ANDROID_HOME/docs/reference
* Navigate to Modules
* Select your Android module
* Click the Dependencies tab
* Move Android 2.3.3 Platform above all the Maven dependencies

IntelliJ IDEA, Play Framework and Javadoc
-----------------------------------------
* Open Project Structure (Ctrl+Alt+Shift+S)
* Navigate to Modules
* Select server module
* Click the Dependencies tab
* Edit the play.jar dependency (not PlayFramework Dependencies)
* Click Attach Sources...
* Add the full path to $PLAY_HOME/framework/src