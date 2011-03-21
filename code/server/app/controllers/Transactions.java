package controllers;

import com.google.gson.JsonArray;
import models.AggregatedTag;
import models.AverageConsumption;
import models.Transaction;
import play.db.jpa.JPA;
import play.mvc.Controller;
import utils.GsonUtil;
import utils.ModelHelper;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transactions extends Controller {

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

    public static void all() {
        final List<Transaction> transactions = Transaction.
                find("order by accountingDate desc, timestamp desc").fetch();
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    @SuppressWarnings("unchecked")
    public static void after(Long timestamp) {
        Query query = JPA.em().createQuery("select t from Transaction t " +
                "where timestamp > :timestamp " +
                "and internal = :internal " +
                "order by accountingDate desc, timestamp desc");
        query.setParameter("timestamp", timestamp);
        query.setParameter("internal", false);
        final List<Transaction> transactions = query.getResultList();
        renderJSON(GsonUtil.makeJSON(transactions));
    }

    public static void save(JsonArray body) {
        final List<Transaction> transactions = GsonUtil.parseTransactions(body);
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
                    t.save();
                    updated.add(t);
                }
            }
        }
        renderJSON(GsonUtil.makeJSON(updated));
    }
}