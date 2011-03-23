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
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    public static void after(Long timestamp, String registrationId) {
        final List<Transaction> transactions = Transaction.find(
                "timestamp > ? " +
                        "and internal = ? " +
                        "and user.deviceId = ? " +
                        "order by accountingDate desc, timestamp desc",
                timestamp, false, registrationId).fetch();
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    public static void save(String registrationId, JsonArray json) {
        final List<Transaction> transactions = GsonUtil.parseTransactions(json);
        final User user = User.find("deviceId", registrationId).first();
        if (user != null) {
            List<Transaction> updated = saveTransactions(transactions, user);
            renderJSON(GsonUtil.makeJSON(updated));
        } else {
            logger.log(Level.WARN, "Could not find user with registrationId: " +
                    registrationId);
        }
    }

    private static List<Transaction> saveTransactions(
            List<Transaction> transactions, User user) {
        final List<Transaction> updated = new ArrayList<Transaction>();
        for (Transaction t : transactions) {
            if (t.dirty) {
                Transaction existing = Transaction.findById(t.id);
                if (existing != null) {
                    existing._id = t._id;
                    existing.accountingDate = t.accountingDate;
                    existing.amountIn = t.amountIn;
                    existing.amountOut = t.amountOut;
                    existing.text = t.text;
                    existing.internal = t.internal;
                    existing.timestamp = t.timestamp;
                    existing.dirty = false;
                    existing.tag = ModelHelper.getOrSaveTag(t.tag.name);
                    existing.type = ModelHelper.getOrAddType(t.type.name);
                    existing.save();
                    updated.add(existing);
                } else {
                    t.id = null;
                    t.tag = ModelHelper.getOrSaveTag(t.tag.name);
                    t.type = ModelHelper.getOrAddType(t.type.name);
                    t.dirty = false;
                    t.user = user;
                    t.save();
                    updated.add(t);
                }
            } else {
                logger.log(Level.WARN,
                        "Not saving non-dirty transaction with _id: " + t._id);
            }
        }
        return updated;
    }
}