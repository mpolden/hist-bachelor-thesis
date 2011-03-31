package controllers;

import models.Transaction;
import org.apache.lucene.queryParser.QueryParser;
import play.Logger;
import play.db.jpa.JPA;
import play.modules.search.Search;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TransactionTags extends Controller {

    private static String firstWord(String s) {
        if (s != null) {
            final String[] words = s.split(" ");
            if (words.length > 0) {
                return words[0];
            }
        }
        return "";
    }

    private static String queryBuilder(String field, String words) {
        final String firstWord = firstWord(words);
        final String remaining = words.replace(firstWord, "");
        return String.format("%s:(+\"%s\" \"%s\")", field, QueryParser.escape(firstWord),
                QueryParser.escape(remaining));
    }

    @SuppressWarnings("unchecked")
    public static void suggest(String body) {
        List<Map<String, String>> result = Collections.emptyList();
        if (body != null && body.length() > 0) {
            play.modules.search.Query q = Search.search(queryBuilder("text", body), Transaction.class);
            List<Long> ids = q.fetchIds();
            if (ids.isEmpty()) {
                Logger.warn("Did not find any results for: %s", body);
                return;
            }
            Query query = JPA.em().createQuery(
                    "select new map (thetag.name as tag, count(*) as count) from Transaction t" +
                            " join t.tag as thetag" +
                            " where t.id in (:ids)" +
                            " group by thetag.name order by count(*) desc"
            );
            query.setParameter("ids", ids);
            result = query.getResultList();
            if (result.isEmpty()) {
                Logger.warn("Could not find any suggestions for: %s", body);
            }
        }
        renderJSON(result);
    }
}
