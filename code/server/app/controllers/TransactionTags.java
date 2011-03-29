package controllers;

import models.Transaction;
import org.apache.log4j.Logger;
import play.db.jpa.JPA;
import play.modules.search.Search;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.List;

public class TransactionTags extends Controller {

    private static final Logger logger = Logger.getLogger(
            TransactionTags.class.getName());

    private static String firstWord(String s) {
        if (s != null) {
            final String[] words = s.split(" ");
            if (words.length > 0) {
                return words[0];
            }
        }
        return null;
    }

    private static boolean renderTag(List<Object> result) {
        if (!result.isEmpty()) {
            renderText(result.get(0) + " " + result.get(1));
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static void suggest(String body) {
        if (body != null) {
            play.modules.search.Query q = Search.search(String.format("text:(%s)", body), Transaction.class);
            List<Long> ids = q.fetchIds();
            Query query = JPA.em().createQuery(
                    "select new map (thetag.name as tag, count(*) as count) from Transaction t" +
                            " join t.tag as thetag" +
                            " where t.id in (:ids)" +
                            " group by thetag.name order by count(*) desc"
            );
            query.setParameter("ids", ids);
            renderJSON(query.getResultList());
        }
    }
}
