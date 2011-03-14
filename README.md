Awesome Finance Planner
================================

This is Android application that intends to help you easily plan and organize
your finances.

Tags
----
Tags are created after each sprint is finished. The tag format is vX.Y.Z where
X, Y and Z are integers. X is the major version, Y is the sprint number and Z
is the day number in that sprint.

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
    rm /data/data/no.kantega.android/databases/transaction.db
