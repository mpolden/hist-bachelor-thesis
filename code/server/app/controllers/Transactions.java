package controllers;

import com.google.gson.JsonArray;
import models.Transaction;
import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import java.util.ArrayList;
import java.util.List;

public class Transactions extends Controller {

    private static Logger logger = Logger.getLogger(
            Transactions.class.getName());

    public static void all(String registrationId) {
        final List<Transaction> transactions = Transaction.
                find("user.deviceId = ? " +
                        "order by accountingDate desc, timestamp desc",
                        registrationId).fetch();
        if (transactions.isEmpty()) {
            logger.log(Level.WARN,
                    "Could not find any transactions for user with registrationId: " +
                            registrationId);
        }
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    public static void after(Long timestamp, String registrationId) {
        final List<Transaction> transactions = Transaction.find(
                "timestamp > ? " +
                        "and internal = ? " +
                        "and user.deviceId = ? " +
                        "order by accountingDate desc, timestamp desc",
                timestamp, false, registrationId).fetch();
        if (transactions.isEmpty()) {
            logger.log(Level.WARN,
                    "Could not find any transactions for user with registrationId: " +
                            registrationId);
        }
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    public static void save(String registrationId, JsonArray json) {
        final List<Transaction> transactions = GsonUtil.parseTransactions(json);
        final List<Transaction> updated = new ArrayList<Transaction>();
        final User user = User.find("deviceId", registrationId).first();
        if (user != null) {
            for (Transaction t : transactions) {
                updated.add(ModelHelper.saveOrUpdate(t, user));
            }
            renderJSON(GsonUtil.makeJSON(updated));
        } else {
            logger.log(Level.WARN, "Could not find user with registrationId: " +
                    registrationId);
        }
    }
}