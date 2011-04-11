package controllers;

import models.User;
import play.Logger;
import play.mvc.Controller;

/**
 * This controller handles registration of new users
 */
public class Users extends Controller {

    /**
     * Register a new user or update existing registration ID
     *
     * @param registrationId C2DM registration ID of device
     */
    public static void register(String username, String registrationId) {
        User user = User.find("username", username).first();
        if (user == null) {
            user = new User();
            user.username = username;
            user.deviceId = registrationId;
            user.save();
        } else {
            Logger.info("User already registered, changed registrationId" +
                    "\nExisting: %s" +
                    "\nNew: %s",
                    user.deviceId, registrationId);
            user.deviceId = registrationId;
            user.save();
        }
    }

    /**
     * Unregiser a user
     *
     * @param registrationId C2DM registration ID of device
     */
    public static void unregister(String username, String registrationId) {
        User user = User.find("username", username).first();
        if (user != null) {
            user.deviceId = null;
            user.save();
            Logger.info("Removed registration ID for: %s", user.username);
        } else {
            Logger.warn("User not found: %s", username);
        }
    }
}
