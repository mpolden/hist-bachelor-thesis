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
            Logger.warn("Could not find any transactions for user: %s", username);
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