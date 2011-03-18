package controllers;

import play.db.jpa.JPA;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.List;

public class TransactionTags extends Controller {

    private static String firstWord(String s) {
        if (s != null) {
            final String[] words = s.split(" ");
            if (words.length > 0) {
                return words[0];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void suggest(String body) {
        final String transactionText = firstWord(body);
        if (transactionText != null) {
            final Query query = JPA.em().createQuery(
                    "select tag.name, count(*) from Transaction t" +
                            " join t.tag as tag" +
                            " where t.text like :text" +
                            " group by tag.name order by count(*)"
            );
            query.setParameter("text", String.format("%%%s%%",
                    transactionText));
            final List<Object[]> result = query.getResultList();
            renderJSON(result);
        }
    }
}
