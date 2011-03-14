package no.kantega.android.controllers;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import no.kantega.android.models.*;
import no.kantega.android.utils.DatabaseHelper;
import no.kantega.android.utils.FmtUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Transactions {

    private static final String TAG = Transactions.class.getSimpleName();
    private static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Dao<Transaction, Integer> transactionDao;
    private Dao<TransactionTag, Integer> transactionTagDao;
    private Dao<TransactionType, Integer> transactionTypeDao;

    public Transactions(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        transactionDao = helper.getTransactionDao();
        transactionTagDao = helper.getTransactionTagDao();
        transactionTypeDao = helper.getTransactionTypeDao();
    }

    private TransactionTag insertTag(TransactionTag tag) {
        try {

            QueryBuilder<TransactionTag, Integer> queryBuilder = transactionTagDao.queryBuilder();
            queryBuilder.where().eq("name", tag.getName());
            List<TransactionTag> tags = transactionTagDao.query(queryBuilder.prepare());
            if (tags.size() > 0) {
                return tags.get(0);
            } else {
                transactionTagDao.create(tag);
                tags = transactionTagDao.query(queryBuilder.prepare());
                return tags.get(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Failed to add transaction tag", e);
            return null;
        }
    }

    private TransactionType insertType(TransactionType type) {
        try {
            QueryBuilder<TransactionType, Integer> queryBuilder = transactionTypeDao.queryBuilder();
            queryBuilder.where().eq("name", type.getName());
            List<TransactionType> types = transactionTypeDao.query(queryBuilder.prepare());
            if (types.size() > 0) {
                return types.get(0);
            } else {
                transactionTypeDao.create(type);
                types = transactionTypeDao.query(queryBuilder.prepare());
                return types.get(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Failed to add transaction type", e);
            return null;
        }
    }

    public int insert(Transaction t) {
        try {
            t.setTag(insertTag(t.getTag()));
            t.setType(insertType(t.getType()));
            return transactionDao.create(t);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to add transaction", e);
            return -1;
        }
    }

    public List<Transaction> getOrderedByDateDesc(final int limit) {
        List<Transaction> transactions = Collections.emptyList();
        try {
            QueryBuilder<Transaction, Integer> queryBuilder = transactionDao.queryBuilder();
            queryBuilder.orderBy("accountingDate", false).limit(limit);
            transactions = transactionDao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve transactions", e);
        }
        return transactions;
    }

    public List<AggregatedTag> getTags(final int limit) {
        List<AggregatedTag> aggregatedTags = new ArrayList<AggregatedTag>();
        try {
            GenericRawResults<String[]> rawResults = transactionDao.queryRaw(
                    "SELECT transactiontags.name, SUM(amountOut) AS sum " +
                            "FROM transactions " +
                            "INNER JOIN transactiontags ON transactiontags.id = transactions.tag_id " +
                            "GROUP BY transactiontags.name " +
                            "ORDER BY sum DESC LIMIT ?", String.valueOf(limit));
            for (String[] row : rawResults) {
                AggregatedTag aggregatedTag = new AggregatedTag();
                aggregatedTag.setName(row[0]);
                aggregatedTag.setAmount(Double.parseDouble(row[1]));
                aggregatedTags.add(aggregatedTag);
            }
            rawResults.close();
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve aggregated tags", e);
        }
        return aggregatedTags;
    }

    public List<TransactionTag> getAllTags() {
        List<TransactionTag> transactionTags = new ArrayList<TransactionTag>();
        try {
            GenericRawResults<String[]> rawResults = transactionDao.queryRaw(
                    "SELECT name, COUNT(*) AS count FROM transactiontags GROUP BY name ORDER BY count DESC");
            for (String[] row : rawResults) {
                TransactionTag tag = new TransactionTag();
                tag.setName(row[0]);
                transactionTags.add(tag);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all tags", e);
        }
        return transactionTags;
    }

    public int getTransactionCount() {
        try {
            GenericRawResults<String[]> rawResults = transactionDao.queryRaw("SELECT COUNT(*) FROM transactions");
            return Integer.parseInt(rawResults.getResults().get(0)[0]);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve transaction count", e);
        }
        return 0;
    }

    private double getAvgDay() {
        try {
            GenericRawResults<String[]> rawResults = transactionDao.
                    queryRaw("SELECT accountingDate FROM transactions ORDER BY accountingDate ASC LIMIT 1");
            List<String[]> results = rawResults.getResults();
            if (results.size() > 0) {
                final Date start = FmtUtil.stringToDate(SQLITE_DATE_FORMAT, results.get(0)[0]);
                rawResults.close();
                rawResults = transactionDao.
                        queryRaw("SELECT accountingDate FROM transactions ORDER BY accountingDate DESC LIMIT 1");
                final Date stop = FmtUtil.stringToDate(SQLITE_DATE_FORMAT, rawResults.getResults().get(0)[0]);
                rawResults.close();
                final int days =
                        (int) ((stop.getTime() - start.getTime()) / 1000) / 86400;
                rawResults = transactionDao.
                        queryRaw("SELECT SUM(amountOut) FROM transactions LIMIT 1");
                final double avg = Double.parseDouble(rawResults.getResults().get(0)[0]) / days;
                rawResults.close();
                return avg;
            }

        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve average consumption");
        }
        return 0D;
    }

    public AverageConsumption getAvg() {
        final double avgPerDay = getAvgDay();
        final AverageConsumption avg = new AverageConsumption();
        avg.setDay(avgPerDay);
        avg.setWeek(avgPerDay * 7);
        avg.setMonth(avgPerDay * 30.4368499);
        return avg;
    }

    public void emptyTables() {
        try {
            transactionDao.queryRaw("DELETE FROM transactions");
            transactionDao.queryRaw("DELETE FROM transactiontags");
            transactionDao.queryRaw("DELETE FROM transactiontypes");
        } catch (SQLException e) {
            Log.e(TAG, "Could not empty tables", e);
        }
    }
}
