package controllers;

import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import play.mvc.Controller;

public class Users extends Controller {

    private static Logger logger = Logger.getLogger(Users.class.getName());

    public static void register(String registrationId) {
        if (registrationId != null) {
            User user = User.find("registrationId", registrationId).first();
            if (user == null) {
                user = new User();
                user.deviceId = registrationId;
                user.save();
            } else {
                logger.log(Level.WARN,
                        "User already registered, registrationId: " +
                                registrationId);
            }
        } else {
            logger.log(Level.INFO, "Called without registrationId");
        }
    }

    public static void unregister(String registrationId) {
        if (registrationId != null) {
            User user = User.find("registrationId", registrationId).first();
            if (user != null) {
                user = user.delete();
                logger.log(Level.INFO, "Deleted user: " + user.deviceId);
            } else {
                logger.log(Level.WARN, "User not found: " + registrationId);
            }
        } else {
            logger.log(Level.INFO, "Called without registrationId");
        }
    }
}
