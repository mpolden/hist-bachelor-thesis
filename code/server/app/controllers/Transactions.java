package controllers;

import com.google.gson.JsonArray;
import models.AggregatedTag;
import models.AverageConsumption;
import models.Transaction;
import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import play.db.jpa.JPA;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transactions extends Controller {

    private static Logger logger = Logger.getLogger(
            Transactions.class.getName());

    @SuppressWarnings("unchecked")
    @Deprecated
    public static void topTags(int count) {
        Query query = JPA.em().createQuery(
                "select tag.name, sum(t.amountOut) from Transaction t" +
                        " join t.tag as tag" +
                        " group by tag.name order by sum(t.amountOut) desc").
                setMaxResults(count);
        List<Object[]> result = query.getResultList();
        List<AggregatedTag> tags = new ArrayList<AggregatedTag>();
        for (Object[] o : result) {
            if (o.length != 2) {
                throw new IllegalArgumentException(
                        "Incorrect number of fields fetched");
            }
            AggregatedTag tag = new AggregatedTag();
            tag.setName(o[0].toString());
            tag.setAmount(Double.parseDouble(o[1].toString()));
            tags.add(tag);
        }
        renderJSON(tags);
    }

    @Deprecated
    private static Double avgDay() {
        Transaction t;
        t = Transaction.find("order by accountingDate asc").first();
        Date start = t.accountingDate;
        t = Transaction.find("order by accountingDate desc").first();
        Date stop = t.accountingDate;
        final int days =
                (int) ((stop.getTime() - start.getTime()) / 1000) / 86400;
        Query query = JPA.em().createQuery(
                "select sum(t.amountOut) from Transaction t");
        Object o = query.getSingleResult();
        return Double.parseDouble(o.toString()) / days;
    }

    @Deprecated
    public static void avg() {
        AverageConsumption ac = new AverageConsumption();
        ac.setDay(avgDay());
        ac.setWeek(avgDay() * 7);
        ac.setMonth(avgDay() * 30.4368499); // Google approves
        renderJSON(ac);
    }

    public static void all(String registrationId) {
        final List<Transaction> transactions = Transaction.
                find("user.deviceId = ? " +
                        "order by accountingDate desc, timestamp desc",
                        registrationId).fetch();
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    @SuppressWarnings("unchecked")
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
            }
        }
        return updated;
    }
}