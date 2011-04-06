package controllers;

import models.User;
import play.Logger;
import play.mvc.Controller;

/**
 * This controller handles registration of new users
 */
public class Users extends Controller {

    /**
     * Register a new user
     *
     * @param registrationId C2DM registration ID of device
     */
    public static void register(String registrationId) {
        if (registrationId != null) {
            User user = User.find("registrationId", registrationId).first();
            if (user == null) {
                user = new User();
                user.deviceId = registrationId;
                user.save();
            } else {
                Logger.warn("User already registered, registrationId: %s",
                        registrationId);
            }
        } else {
            Logger.info("Called without registrationId");
        }
    }

    /**
     * Unregiser a user
     *
     * @param registrationId C2DM registration ID of device
     */
    public static void unregister(String registrationId) {
        if (registrationId != null) {
            User user = User.find("registrationId", registrationId).first();
            if (user != null) {
                user = user.delete();
                Logger.info("Deleted user: %s", user.deviceId);
            } else {
                Logger.warn("User not found: %s", registrationId);
            }
        } else {
            Logger.info("Called without registrationId");
        }
    }
}
