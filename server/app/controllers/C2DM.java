package controllers;

import com.google.gson.JsonArray;
import models.Transaction;
import models.User;
import play.Logger;
import play.Play;
import play.libs.WS;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller handles C2DM communcation
 */
public class C2DM extends Controller {

    private static final String C2DM_PUSH_TOKEN = Play.configuration.
            getProperty("c2dm.push.token");
    private static final String C2DM_PUSH_URL = Play.configuration.
            getProperty("c2dm.push.url");

    /**
     * Notify device of new transactions
     *
     * @param registrationId Registration ID of the device
     * @param json           New transactions
     */
    public static void c2dm(String registrationId, JsonArray json) {
        if (C2DM_PUSH_TOKEN == null) {
            Logger.error("Missing c2dm.push.token i application.conf");
            return;
        }
        if (C2DM_PUSH_URL == null) {
            Logger.error("Missing c2dm.push.url i application.conf");
            return;
        }
        if (json == null) {
            Logger.error("Failed to bind JsonArray");
            return;
        }
        final List<Transaction> transactions = GsonUtil.parseTransactions(json);
        final List<Transaction> updated = new ArrayList<Transaction>();
        final User user = User.find("deviceId", registrationId).first();
        if (user == null) {
            Logger.error("No user found with deviceId: %s", registrationId);
            return;
        }
        for (Transaction t : transactions) {
            updated.add(ModelHelper.saveOrUpdate(t, user));
        }
        if (updated.isEmpty()) {
            Logger.info("No transactions were updated");
            return;
        }
        WS.WSRequest request = WS.url(C2DM_PUSH_URL);
        request.headers.put("Authorization", String.format(
                "GoogleLogin auth=%s", C2DM_PUSH_TOKEN));
        request.parameters.put("registration_id", registrationId);
        request.parameters.put("data.message", updated.size());
        request.parameters.put("collapse_key", ",");
        WS.HttpResponse response = request.post();
        if (response.getStatus() != 200) {
            Logger.error("Failed to send C2DM message: %s", response.getString());
        }
    }
}

