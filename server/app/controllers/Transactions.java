package controllers;

import com.google.gson.JsonArray;
import models.Transaction;
import models.User;
import play.Logger;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This controller provides methods for retrieving and adding/updated transactions
 */
public class Transactions extends Controller {

    /**
     * Retrieve all transactions for a user
     *
     * @param username Username of user
     */
    public static void all(String username) {
        final List<Transaction> transactions = Transaction.
                find("user.username = ? " +
                        "order by date desc, timestamp desc", username).fetch();
        if (transactions.isEmpty()) {
            Logger.warn("Could not find any transactions for user: %s", username);
        }
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    /**
     * Retrieve all transactions that were created after the given timestamp
     *
     * @param timestamp Only retrieve transactions after this timestamp
     * @param username  Username of user
     */
    public static void after(Long timestamp, String username) {
        final List<Transaction> transactions = Transaction.find(
                "timestamp > ? " +
                        "and internal = ? " +
                        "and user.username = ? " +
                        "order by date desc, timestamp desc",
                timestamp, false, username).fetch();
        if (transactions.isEmpty()) {
            Logger.info("Could not find any new transactions for user: %s", username);
        }
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    /**
     * Save or update the given transactions for a device
     *
     * @param username Username of user
     * @param json     Transactions
     */
    public static void save(String username, JsonArray json) {
        final List<Transaction> transactions = GsonUtil.parseTransactions(json);
        final List<Transaction> updated = new ArrayList<Transaction>();
        final User user = User.find("username", username).first();
        if (user != null) {
            for (Transaction t : transactions) {
                updated.add(ModelHelper.saveOrUpdate(t, user));
            }
            renderJSON(GsonUtil.makeJSON(updated));
        } else {
            Logger.warn("Could not find user with username: %s", username);
            renderJSON(Collections.<Transaction>emptyList());
        }
    }
}