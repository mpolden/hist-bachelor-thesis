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

You will need the following play modules:

    play install maven
    play install search-2.0

To run the server:

    cd code/server
    play run

Setting values for development
------------------------------
When running in development mode, a few custom values need to be set, such as
the application server URLs and your Android C2DM device ID. The device ID can
be found by launching the app and looking for "Registered: <the_id>" or
"Existing device ID: <the_id>" in the emulator log.

* Edit code/android/src/main/android/assets/url.properties to contain the following:

    suggestTags = http://<your_ip>:9000/tags/suggest
    newTransactions = http://<your_ip>:9000/transactions/%s
    allTransactions = http://<your_ip>:9000/transactions/all
    saveTransactions = http://<your_ip>:9000/transactions/save

* Edit code/server/conf/fixtures-local.yml to contain the following:

      User(user):
        deviceId: <your_device_id>

Starting a shell on the emulator
--------------------------------
To start a shell on the emulator, use the following command:

    $ANDROID_HOME/adb -s emulator-5554 shell

To remove the existing database (shouldn't be needed in most cases):

    rm /data/data/no.kantega.android.afp/databases/app.db

Ubuntu, adb and running on a real device
----------------------------------------
To run/deploy the application on an actual Android phone, the adb server needs
to be run as root, use the following commands for a quick fix:

    $ANDROID_HOME/platform-tools/adb kill-server
    sudo $ANDROID_HOME/platform-tools/adb start-server

The best solution is however to use a udev rule, like this:

    echo 'SUBSYSTEM=="usb", SYSFS{idVendor}=="0bb4", MODE="0666"' | \
        sudo tee -a /etc/udev/rules.d/51-android.rules.

The '0bb4' is the vendor ID for HTC, if you're using a different brand use the
lsusb command to find it.
To verify that it worked, run the following:

    $ANDROID_HOME/platform-tools/adb devices

Your device ID should be listed in the output.

IntelliJ IDEA, maven-android-plugin and Javadoc
-----------------------------------------------
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
* Move Android 2.3.3 Platform above all the Maven dependencies (except jUnit)

IntelliJ IDEA, Play Framework and Javadoc
-----------------------------------------

* Open Project Structure (Ctrl+Alt+Shift+S)
* Navigate to Modules
* Select server module
* Click the Dependencies tab
* Edit the play.jar dependency (not PlayFramework Dependencies)
* Click Attach Sources...
* Add the full path to $PLAY_HOME/framework/src

IntelliJ IDEA not displaying Git branch
---------------------------------------

* Open $PROJECT_HOME/.idea/vcs.xml
* Make sure the file contains the following

    <?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
      <component name="VcsDirectoryMappings">
        <mapping directory="$PROJECT_DIR$" vcs="Git" />
      </component>
    </project>

Testing C2DM messages
---------------------
Use the included bash script (code/c2dm-test.sh) to test C2DM messages. You
will need the device ID and a message for the transaction.
