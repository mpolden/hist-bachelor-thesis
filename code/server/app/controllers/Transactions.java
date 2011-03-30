package controllers;

import com.google.gson.JsonArray;
import models.Transaction;
import models.TransactionTag;
import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.QueryParser;
import play.db.jpa.JPA;
import play.modules.search.Search;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class Transactions extends Controller {

    private static Logger logger = Logger.getLogger(
            Transactions.class.getName());

    @SuppressWarnings("unchecked")
    private static TransactionTag getSuggestedTag(String text) {
        if (text == null) {
            return null;
        }
        play.modules.search.Query q = Search.search(String.format("text:(%s)", QueryParser.escape(text)),
                Transaction.class);
        List<Long> ids = q.fetchIds();
        /*TransactionTag suggestion = TransactionTag.find("select tag from Transaction t" +
                " join t.tag as tag" +
                " where t.id in (?)" +
                " group by tag.name order by count(*) desc", ids).first();*/
        Query query = JPA.em().createQuery(
                "select thetag.name from Transaction t" +
                        " join t.tag as thetag" +
                        " where t.id in (:ids)" +
                        " group by thetag.name order by count(*) desc"
        );
        query.setParameter("ids", ids);
        query.setMaxResults(1);
        List<String> result = query.getResultList();
        /*List<Map<String, String>> result = query.getResultList();
        return null;*/
        return !result.isEmpty() ? ModelHelper.saveOrUpdate(new TransactionTag(result.get(0))) : null;
        //return suggestion;
    }

    public static void all(String registrationId) {
        final List<Transaction> transactions = Transaction.
                find("user.deviceId = ? " +
                        "order by date desc, timestamp desc",
                        registrationId).fetch();
        if (!transactions.isEmpty()) {
            for (Transaction t : transactions) {
                if (t.tag != null) {
                    TransactionTag suggested = getSuggestedTag(t.text);
                    if (suggested != null) {
                        logger.log(Level.INFO, String.format("Set suggested tag to %s for transacion text: %s",
                                suggested.name, t.text));
                        t.tag = suggested;
                        t.save();
                    }
                }
            }
        } else {
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
                        "order by date desc, timestamp desc",
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