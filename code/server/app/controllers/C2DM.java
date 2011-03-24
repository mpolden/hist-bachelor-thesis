package controllers;

import com.google.gson.JsonArray;
import models.Transaction;
import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import play.Play;
import play.libs.WS;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import java.util.ArrayList;
import java.util.List;

public class C2DM extends Controller {

    private static Logger logger = Logger.getLogger(
            C2DM.class.getName());
    private static final String C2DM_PUSH_TOKEN = Play.configuration.
            getProperty("c2dm.push.auth");
    private static final String C2DM_PUSH_URL = Play.configuration.
            getProperty("c2dm.push.url");

    public static void c2dm(String registrationId, JsonArray json) {
        if (C2DM_PUSH_TOKEN == null) {
            logger.log(Level.ERROR,
                    "Missing c2dm.push.auth i application.conf");
            return;
        }
        if (C2DM_PUSH_URL == null) {
            logger.log(Level.ERROR,
                    "Missing c2dm.push.url i application.conf");
            return;
        }
        final List<Transaction> transactions = GsonUtil.parseTransactions(json);
        final List<Transaction> updated = new ArrayList<Transaction>();
        final User user = User.find("deviceId", registrationId).first();
        if (user != null) {
            for (Transaction t : transactions) {
                updated.add(ModelHelper.saveTransaction(t, user));
            }
        }
        if (updated.isEmpty()) {
            logger.log(Level.ERROR,
                    "No transactions were saved");
            return;
        }
        // Build a comma separated list of IDs that have been updated
        final StringBuilder ids = new StringBuilder();
        for (Transaction t : updated) {
            ids.append(t.id);
            ids.append(",");
        }
        ids.deleteCharAt(ids.length() - 1);
        WS.WSRequest request = WS.url(C2DM_PUSH_URL);
        request.headers.put("Authorization", String.format(
                "GoogleLogin auth=%s", C2DM_PUSH_TOKEN));
        request.parameters.put("registration_id", registrationId);
        request.parameters.put("data.message", ids.toString());
        request.parameters.put("collapse_key", ",");
        WS.HttpResponse response = request.post();
        if (response.getStatus() != 200) {
            logger.log(Level.ERROR, "Failed to send C2DM message: " +
                    response.getString());
        }
        renderJSON(GsonUtil.makeJSON(updated));
    }
}

