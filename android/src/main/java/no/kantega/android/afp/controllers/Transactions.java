package no.kantega.android.afp.controllers;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MergeCursor;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import no.kantega.android.afp.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * This class acts as a wrapper for Transaction specific database operations
 */
public class Transactions {

    private static final String TAG = Transactions.class.getSimpleName();
    private final DatabaseHelper helper;
    private final Dao<Transaction, Integer> transactionDao;
    private final Dao<TransactionTag, Integer> transactionTagDao;

    /**
     * Create a new instance and initialize DAOs
     *
     * @param context Application context
     */
    public Transactions(Context context) {
        this.helper = new DatabaseHelper(context);
        this.transactionDao = helper.getTransactionDao();
        this.transactionTagDao = helper.getTransactionTagDao();
    }

    /**
     * Add a new transaction tag
     *
     * @param tag Transaction tag to save
     * @return The newly added tag or the existing one
     */
    private TransactionTag insertIgnore(TransactionTag tag) {
        if (tag == null || tag.getName() == null || tag.getName().length() == 0) {
            return null;
        }
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
        }
        return null;
    }

    /**
     * Add a new transaction
     *
     * @param t Transaction to save
     */
    public void add(Transaction t) {
        t.setTag(insertIgnore(t.getTag()));
        try {
            transactionDao.create(t);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to add transaction", e);
        }
    }

    /**
     * Add a new tag
     *
     * @param t Transaction tag to save
     */
    public void add(TransactionTag t) {
        insertIgnore(t);
    }

    /**
     * Update a transaction
     *
     * @param t Transaction tag to save
     */
    public void update(Transaction t) {
        t.setTag(insertIgnore(t.getTag()));
        try {
            transactionDao.update(t);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to update transaction", e);
        }
    }

    /**
     * Retrieve an ordered list of transactions using the given query builder
     *
     * @param queryBuilder Query builder
     * @return List of transactions
     * @throws java.sql.SQLException When error occurs
     */
    private List<Transaction> getAll(QueryBuilder<Transaction, Integer> queryBuilder) throws SQLException {
        queryBuilder.orderBy("date", false).orderBy("timestamp", false);
        return transactionDao.query(queryBuilder.prepare());
    }

    /**
     * Retrieve the latest transaction using the given query builder
     *
     * @param queryBuilder Query builder
     * @return Latest transaction
     * @throws java.sql.SQLException When error occurs
     */
    private Transaction get(QueryBuilder<Transaction, Integer> queryBuilder) throws SQLException {
        queryBuilder.orderBy("date", false).orderBy("timestamp", false).limit(1);
        return transactionDao.queryForFirst(queryBuilder.prepare());
    }

    /**
     * Get transaction by id
     *
     * @param id ID of transaction
     * @return The transaction or null if not found
     */
    public Transaction getById(final int id) {
        QueryBuilder<Transaction, Integer> queryBuilder = transactionDao.
                queryBuilder();
        try {
            queryBuilder.setWhere(queryBuilder.where().eq("_id", id));
            return get(queryBuilder);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to find transaction", e);
        }
        return null;
    }

    /**
     * Get transactions by text
     *
     * @param text      The text
     * @param excludeId Id to exclude
     * @param tagIsNull Wheter to include NULL tags
     * @return List of transactions
     */
    public List<Transaction> getByText(final String text, final int excludeId, final boolean tagIsNull) {
        QueryBuilder<Transaction, Integer> queryBuilder = transactionDao.
                queryBuilder();
        try {
            Where<Transaction, Integer> where = queryBuilder.where().eq("text", text).and().ne("_id", excludeId);
            if (tagIsNull) {
                where = where.and().isNull("tag_id");
            }
            queryBuilder.setWhere(where);
            return getAll(queryBuilder);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to find transactions by text", e);
        }
        return Collections.emptyList();
    }

    /**
     * Retrieve a list of dirty transactions
     *
     * @return List of transactions
     */
    public List<Transaction> getDirty() {
        QueryBuilder<Transaction, Integer> queryBuilder = transactionDao.
                queryBuilder();
        try {
            queryBuilder.setWhere(queryBuilder.where().eq("dirty", true));
            return getAll(queryBuilder);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to set where condition", e);
        }
        return Collections.emptyList();
    }

    /**
     * Get the latest external transaction
     *
     * @return Transaction
     */
    public Transaction getLatestExternal() {
        QueryBuilder<Transaction, Integer> queryBuilder = transactionDao.
                queryBuilder();
        try {
            queryBuilder.setWhere(queryBuilder.where().eq("internal", false));
            return get(queryBuilder);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to set where condition", e);
        }
        return null;
    }

    /**
     * Get a cursor for transactions
     *
     * @return Cursor
     */
    public Cursor getCursor() {
        return helper.getReadableDatabase().query(
                "transactions " +
                        "LEFT JOIN transactiontags " +
                        "ON transactiontags.id = transactions.tag_id",
                new String[]{"_id", "date", "text", "amount", "transactiontags.name AS tag"}, null,
                null, null, null,
                "date DESC, timestamp DESC", null);
    }

    /**
     * Get a cursor that only returns non-internal transactions after the given timestamp
     *
     * @param timestamp Timestamp
     * @return External transactions after timestamp
     */
    public Cursor getCursorAfterTimestamp(long timestamp) {
        String selection = "internal = ? AND timestamp > ?";
        String[] selectionArgs = new String[]{"0",
                String.valueOf(timestamp)};
        return helper.getReadableDatabase().query(
                "transactions " +
                        "LEFT JOIN transactiontags " +
                        "ON transactiontags.id = transactions.tag_id",
                new String[]{"_id", "date", "text", "amount", "transactiontags.name AS tag"}, selection,
                selectionArgs, null, null,
                "date DESC, timestamp DESC", null);
    }

    /**
     * Get a cursor that only returns tags within the given month and year
     *
     * @param month Month to filter on
     * @param year  Year to filter on
     * @return Tags within the given month and year
     */
    public Cursor getCursorTags(String month, String year) {
        final String dateQuery = String.format("%s-%s-%%", year, month);
        return helper.getReadableDatabase().query(
                "transactions " +
                        "LEFT JOIN transactiontags " +
                        "ON transactiontags.id = transactions.tag_id",
                new String[]{"_id", "transactiontags.name AS tag", "SUM(amount) AS sum"},
                "date LIKE ?", new String[]{dateQuery},
                "tag", null, "sum DESC, tag DESC", null);
    }

    /**
     * Get sum of transactions in the given month and year
     *
     * @param month Month to filter on
     * @param year  Year to filter on
     * @return Sum of transactions
     */
    private Cursor getCursorTagsTotal(String month, String year) {
        final String dateQuery = String.format("%s-%s-%%", year, month);
        return helper.getReadableDatabase().query(
                "transactions",
                new String[]{"_id", "SUM(amount) AS sum"},
                "date LIKE ?", new String[]{dateQuery},
                null, null, null, "1");
    }

    /**
     * Get a merged cursor for aggregated data for all tags and the sum of all transactions
     *
     * @param month Month to filter on
     * @param year  Year to filter on
     * @return Aggregated tags and sum of transactions
     */
    public MergeCursor getMergeCursorTags(String month, String year) {
        return new MergeCursor(new Cursor[]{getCursorTags(month, year), getCursorTagsTotal(month, year)});
    }

    /**
     * Get a cursor that returns transactions for a given tag within the given month and year
     *
     * @param tag   Tag to filter on
     * @param month Month to filter on
     * @param year  Year to filter on
     * @return Transactions
     */
    public Cursor getCursorTransactions(String tag, String month, String year) {
        final String dateQuery = String.format("%s-%s-%%", year, month);
        final String selection;
        final String[] selectionArgs;
        if (tag == null) {
            selection = "tag IS NULL AND date LIKE ?";
            selectionArgs = new String[]{dateQuery};
        } else {
            selection = "tag = ? AND date LIKE ?";
            selectionArgs = new String[]{tag, dateQuery};
        }
        return helper.getReadableDatabase().query(
                "transactions " +
                        "LEFT JOIN transactiontags " +
                        "ON transactiontags.id = transactions.tag_id",
                new String[]{"_id", "date", "text", "amount", "transactiontags.name AS tag"},
                selection, selectionArgs, null, null, "date DESC, timestamp DESC", null);
    }

    /**
     * Retrieve total transaction count
     *
     * @return Transaction count
     */
    public long getCount() {
        return DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), "transactions");
    }

    /**
     * Retrieve total transaction tag count
     *
     * @return Transaction tag count
     */
    public long getTagCount() {
        return DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), "transactiontags");
    }

    /**
     * Retrieve ntotal dirty transaction count
     *
     * @return Number of unsynced transactions
     */
    public int getDirtyCount() {
        try {
            final GenericRawResults<String[]> rawResults = transactionDao.
                    queryRaw("SELECT COUNT(*) FROM transactions WHERE dirty = 1 LIMIT 1");
            return Integer.parseInt(rawResults.getResults().get(0)[0]);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve transaction count", e);
        }
        return 0;
    }

    /**
     * Retrieve number of untagged transactions
     *
     * @return Untagged count
     */
    public int getUntaggedCount() {
        try {
            final GenericRawResults<String[]> rawResults = transactionDao.
                    queryRaw("SELECT COUNT(*) FROM transactions " +
                            "WHERE tag_id IS NULL LIMIT 1");
            return Integer.parseInt(rawResults.getResults().get(0)[0]);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve transaction count", e);
        }
        return 0;
    }

    /**
     * Get all transaction tags ordered by name
     *
     * @return List of transaction tags
     */
    public List<TransactionTag> getTags() {
        QueryBuilder<TransactionTag, Integer> queryBuilder = transactionTagDao.queryBuilder();
        queryBuilder.orderBy("name", true);
        try {
            return transactionTagDao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all tags", e);
        }
        return Collections.emptyList();
    }

    /**
     * Empty all tables
     */
    public void emptyTables() {
        try {
            transactionDao.queryRaw("DELETE FROM transactions");
            transactionDao.queryRaw("DELETE FROM transactiontags");
        } catch (SQLException e) {
            Log.e(TAG, "Could not empty tables", e);
        }
    }

    /**
     * Close open database connections
     */
    public void close() {
        if (helper != null) {
            helper.close();
            Log.d(TAG, "Closed database connection");
        }
    }
}
