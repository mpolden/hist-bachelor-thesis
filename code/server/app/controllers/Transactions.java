package controllers;

import models.AggregatedTag;
import models.AverageConsumption;
import models.Transaction;
import play.db.jpa.JPA;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transactions extends Controller {

    @SuppressWarnings("unchecked")
    public static void topTags(int count) {
        Query query = JPA.em().createQuery(
                "select tag.name, sum(t.amountOut) from Transaction t" +
                        " join t.tags as tag" +
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

    public static void avg() {
        AverageConsumption ac = new AverageConsumption();
        ac.setDay(avgDay());
        ac.setWeek(avgDay() * 7);
        ac.setMonth(avgDay() * 30.4368499); // Google approves
        renderJSON(ac);
    }

    public static void transactions(int count) {
        List<Transaction> transactions = Transaction.
                find("order by accountingDate desc").fetch(count);
        renderJSON(transactions);
    }
}