package controllers;

import com.google.gson.JsonArray;
import models.Transaction;
import models.TransactionTag;
import models.User;
import org.apache.lucene.queryParser.QueryParser;
import play.Logger;
import play.db.jpa.JPA;
import play.modules.search.Search;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This controller provides methods for retrieving and adding/updated transactions
 */
public class Transactions extends Controller {

    /**
     * Get a suggested tag for the given text
     *
     * @param text Text to suggest by
     * @return Suggested tag
     */
    @SuppressWarnings("unchecked")
    private static TransactionTag getSuggestedTag(String text) {
        if (text == null) {
            return null;
        }
        play.modules.search.Query q = Search.search(String.format("text:(%s)", QueryParser.escape(text)),
                Transaction.class);
        List<Long> ids = q.fetchIds();
        Query query = JPA.em().createQuery(
                "select thetag.name from Transaction t" +
                        " join t.tag as thetag" +
                        " where t.id in (:ids)" +
                        " group by thetag.name order by count(*) desc"
        );
        query.setParameter("ids", ids);
        query.setMaxResults(1);
        List<String> result = query.getResultList();
        return !result.isEmpty() ? ModelHelper.saveOrUpdate(new TransactionTag(result.get(0))) : null;
    }

    /**
     * Retrieve all transactions for a device
     *
     * @param registrationId C2DM registration ID of the device
     */
    public static void all(String registrationId) {
        final List<Transaction> transactions = Transaction.
                find("user.deviceId = ? " +
                        "order by date desc, timestamp desc",
                        registrationId).fetch();
        /*if (!transactions.isEmpty()) {
            for (Transaction t : transactions) {
                if (t.tag == null) {
                    TransactionTag suggested = getSuggestedTag(t.text);
                    if (suggested != null) {
                        Logger.info("Set suggested tag to %s for transaction text: %s", suggested.name, t.text);
                        t.tag = suggested;
                        t.save();
                    }
                }
            }
        } else {
            Logger.warn("Could not find any transactions for user with registrationId: %s", registrationId);
        }*/
        if (transactions.isEmpty()) {
            Logger.warn("Could not find any transactions for user with registrationId: %s", registrationId);
        }
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    /**
     * Retrieve all transactions that were created after the given timestamp
     *
     * @param timestamp      Only retrieve transactions after this timestamp
     * @param registrationId C2DM registration ID of the device
     */
    public static void after(Long timestamp, String registrationId) {
        final List<Transaction> transactions = Transaction.find(
                "timestamp > ? " +
                        "and internal = ? " +
                        "and user.deviceId = ? " +
                        "order by date desc, timestamp desc",
                timestamp, false, registrationId).fetch();
        if (transactions.isEmpty()) {
            Logger.warn("Could not find any transactions for user with registrationId: %s", registrationId);
        }
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    /**
     * Save or update the given transactions for a device
     *
     * @param registrationId C2DM registration ID of the device
     * @param json           Transactions
     */
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
            Logger.warn("Could not find user with registrationId: %s", registrationId);
            renderJSON(Collections.<Transaction>emptyList());
        }
    }
}