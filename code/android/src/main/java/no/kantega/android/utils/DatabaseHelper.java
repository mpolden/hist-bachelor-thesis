package no.kantega.android.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import no.kantega.android.models.AggregatedTag;
import no.kantega.android.models.AverageConsumption;
import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;
    private Dao<Transaction, Long> transactionDao;
    private Dao<TransactionTag, Long> transactionTagDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        Log.d(TAG, "Creating database");
        try {
            TableUtils.createTable(connectionSource, Transaction.class);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        Log.d(TAG, "Upgrading database");
        try {
            TableUtils.dropTable(connectionSource, Transaction.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    private Dao<Transaction, Long> getTransactionDao() throws SQLException {
        if (transactionDao == null) {
            transactionDao = BaseDaoImpl.createDao(getConnectionSource(), Transaction.class);
        }
        return transactionDao;
    }

    private Dao<TransactionTag, Long> getTransactionTagDao() throws SQLException {
        if (transactionTagDao == null) {
            transactionTagDao = BaseDaoImpl.createDao(getConnectionSource(), TransactionTag.class);
        }
        return transactionTagDao;
    }

    public int insert(Transaction t) {
        try {
            transactionDao = getTransactionDao();
            return transactionDao.create(t);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to add transaction", e);
            return -1;
        }
    }

    public List<Transaction> getOrderedByDateDesc(final int limit) {
        List<Transaction> transactions = Collections.emptyList();
        try {
            transactionDao = getTransactionDao();
            QueryBuilder<Transaction, Long> queryBuilder = transactionDao.queryBuilder();
            queryBuilder.orderBy("accountingDate", false).limit(limit);
            transactions = transactionDao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve transactions", e);
        }
        return transactions;
    }

    public List<AggregatedTag> getTags(final int limit) {
        List<AggregatedTag> aggregatedTags = Collections.emptyList();
        try {
            transactionDao = getTransactionDao();
            GenericRawResults<String[]> rawResults = transactionDao.queryRaw(
                    "SELECT transactiontags.name, SUM(amountOut) AS sum " +
                            "FROM transactions " +
                            "INNER JOIN transactiontags ON transactiontags.id = transactions.tag_id " +
                            "GROUP BY tag.name " +
                            "ORDER BY sum DESC");
            for (String[] row : rawResults.getResults()) {
                AggregatedTag aggregatedTag = new AggregatedTag();
                aggregatedTag.setName(row[0]);
                aggregatedTag.setAmount(Double.parseDouble(row[1]));
                aggregatedTags.add(aggregatedTag);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve aggregated tags", e);
        }
        return aggregatedTags;
    }

    public List<TransactionTag> getAllTags() {
        List<TransactionTag> transactionTags = Collections.emptyList();
        try {
            transactionTagDao = getTransactionTagDao();
            QueryBuilder<TransactionTag, Long> queryBuilder = transactionTagDao.queryBuilder();
            queryBuilder.selectColumns("*", "COUNT(*) AS count").
                    groupBy("transactiontags.name").
                    orderBy("count", false);
            transactionTags = transactionTagDao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve all tags", e);
        }
        return transactionTags;
    }

    public Long getTransactionCount() {
        Long count = 0L;
        try {
            transactionDao = getTransactionDao();
            GenericRawResults<String[]> rawResults = transactionDao.queryRaw("SELECT COUNT(*) FROM transactions");
            return Long.parseLong(rawResults.getResults().get(0)[0]);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to retrieve transaction count", e);
        }
        return count;
    }

    private Double getAvgDay() {
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

    }

    /*public List<TransactionTag> getAllTags() {
        final Cursor cursor = db.query("\"transaction\" " +
                "INNER JOIN transactiontag ON " +
                "transactiontag.id = \"transaction\".tag_id",
                new String[]{"transactiontag.name AS tag", "COUNT(*) AS count"},
                null, null, "tag", null, "count DESC", null);
        final List<TransactionTag> tags = new ArrayList<TransactionTag>();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                TransactionTag tag = new TransactionTag();
                tag.setName(getStringValue(cursor, "tag"));
                tags.add(tag);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }*/

    /*public List<AggregatedTag> getTags(final int limit) {
        final Cursor cursor = db.query("\"transaction\" " +
                "INNER JOIN transactiontag " +
                "ON transactiontag.id = \"transaction\".tag_id ",
                new String[]{"transactiontag.name",
                        "SUM(\"transaction\".amountout) AS sum"},
                null, null, "transactiontag.name", null, "sum DESC",
                String.valueOf(limit));
        final List<AggregatedTag> aggregatedTags =
                new ArrayList<AggregatedTag>();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                AggregatedTag at = new AggregatedTag();
                at.setAmount(Double.parseDouble(getStringValue(cursor, "sum")));
                at.setName(getStringValue(cursor, "name"));
                aggregatedTags.add(at);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return aggregatedTags;
    }*/


    /**

     private boolean rowExists(String table, String columName, String param) {
     final Cursor cursor = db.query(table,
     new String[]{"1"}, columName + " = ?", new String[]{param},
     null, null, null, "1");
     cursor.moveToFirst();
     return cursor.getCount() > 0;
     }


     private long insertType(final TransactionType t) {
     if (!rowExists("transactiontype", "name", t.getName())) {
     final ContentValues values = new ContentValues();
     values.put("name", t.getName());
     db.insert("transactiontype", null, values);
     }
     Cursor cursor = db.query("transactiontype", new String[]{"id"},
     " name = ?", new String[]{t.getName()}, null, null, null, "1");
     cursor.moveToFirst();
     long typeId;
     if (cursor.getCount() > 0) {
     typeId = Long.parseLong(getStringValue(cursor, "id"));
     } else {
     typeId = -1;
     }
     cursor.close();
     return typeId;
     }

     private long insertTag(final TransactionTag t) {
     if (!rowExists("transactiontag", "name", t.getName())) {
     final ContentValues values = new ContentValues();
     values.put("name", t.getName());
     db.insert("transactiontag", null, values);
     }
     Cursor cursor = db.query("transactiontag", new String[]{"id"},
     " name = ?", new String[]{t.getName()}, null, null, null, "1");
     cursor.moveToFirst();
     long tagId;
     if (cursor.getCount() > 0) {
     tagId = Long.parseLong(getStringValue(cursor, "id"));
     } else {
     tagId = -1;
     }
     cursor.close();
     return tagId;
     }

     public void insert(final Transaction t) {
     final long typeId = insertType(t.getType());
     final long tagId = insertTag(t.getTag());
     final ContentValues values = new ContentValues();
     values.put("accountingdate", FmtUtil.dateToString(SQLITE_DATE_FORMAT,
     t.getAccountingDate()));
     values.put("amountin", t.getAmountIn());
     values.put("amountout", t.getAmountOut());
     values.put("archiveref", t.getArchiveRef());
     values.put("fixeddate", FmtUtil.dateToString(SQLITE_DATE_FORMAT,
     t.getFixedDate()));
     values.put("text", t.getText());
     values.put("type_id", typeId);
     values.put("tag_id", tagId);
     values.put("timestamp", t.getTimestamp());
     values.put("internal", t.getInternal() ? 1 : 0);
     final long transactionId = db.insert("\"transaction\"", null, values);
     Log.d(TAG, "Inserted transaction with ID: " + transactionId);
     }

     private String getStringValue(Cursor cursor, String columnName) {
     return cursor.getString(cursor.getColumnIndex(columnName));
     }

     private Integer getIntValue(Cursor cursor, String columnName) {
     return cursor.getInt(cursor.getColumnIndex(columnName));
     }

     public void emptyTables() {
     db.execSQL("DELETE FROM \"transaction\"");
     db.execSQL("DELETE FROM \"transactiontag\"");
     db.execSQL("DELETE FROM \"transactiontype\"");
     }

     public long getTransactionCount() {
     return DatabaseUtils.queryNumEntries(db, "\"transaction\"");
     }


     public List<Transaction> getOrderedByDateDesc(int limit) {
     final Cursor cursor = db.query(
     "\"transaction\" " +
     "INNER JOIN \"transactiontype\" " +
     "ON transactiontype.id = \"transaction\".type_id " +
     "INNER JOIN transactiontag " +
     "ON transactiontag.id = \"transaction\".tag_id"
     , new String[]{"*", "transactiontype.name as type",
     "transactiontag.name as tag"}, null, null,
     null, null, "accountingdate DESC", String.valueOf(limit));
     final List<Transaction> transactions = new ArrayList<Transaction>();
     cursor.moveToFirst();
     if (cursor.getCount() > 0) {
     do {
     Transaction t = new Transaction();
     t.setAccountingDate(FmtUtil.stringToDate(SQLITE_DATE_FORMAT,
     getStringValue(cursor, "accountingdate")));
     t.setAmountIn(Double.parseDouble(getStringValue(cursor,
     "amountin")));
     t.setAmountOut(Double.parseDouble(getStringValue(cursor,
     "amountout")));
     t.setArchiveRef(getStringValue(cursor, "archiveref"));
     t.setFixedDate(FmtUtil.stringToDate(SQLITE_DATE_FORMAT,
     getStringValue(cursor, "fixeddate")));
     t.setText(getStringValue(cursor, "text"));
     TransactionTag tag = new TransactionTag();
     tag.setName(getStringValue(cursor, "tag"));
     t.setTag(tag);
     TransactionType type = new TransactionType();
     type.setName(getStringValue(cursor, "type"));
     t.setType(type);
     t.setInternal(
     getIntValue(cursor, "internal") == 1 ? true : false);
     t.setTimestamp(Long.parseLong(getStringValue(cursor,
     "timestamp")));
     transactions.add(t);
     } while (cursor.moveToNext());
     }
     cursor.close();
     return transactions;
     }

     public List<AggregatedTag> getTags(final int limit) {
     final Cursor cursor = db.query("\"transaction\" " +
     "INNER JOIN transactiontag " +
     "ON transactiontag.id = \"transaction\".tag_id ",
     new String[]{"transactiontag.name",
     "SUM(\"transaction\".amountout) AS sum"},
     null, null, "transactiontag.name", null, "sum DESC",
     String.valueOf(limit));
     final List<AggregatedTag> aggregatedTags =
     new ArrayList<AggregatedTag>();
     cursor.moveToFirst();
     if (cursor.getCount() > 0) {
     do {
     AggregatedTag at = new AggregatedTag();
     at.setAmount(Double.parseDouble(getStringValue(cursor, "sum")));
     at.setName(getStringValue(cursor, "name"));
     aggregatedTags.add(at);
     } while (cursor.moveToNext());
     }
     cursor.close();
     return aggregatedTags;
     }



     public List<TransactionTag> getAllTags() {
     final Cursor cursor = db.query("\"transaction\" " +
     "INNER JOIN transactiontag ON " +
     "transactiontag.id = \"transaction\".tag_id",
     new String[]{"transactiontag.name AS tag", "COUNT(*) AS count"},
     null, null, "tag", null, "count DESC", null);
     final List<TransactionTag> tags = new ArrayList<TransactionTag>();
     cursor.moveToFirst();
     if (cursor.getCount() > 0) {
     do {
     TransactionTag tag = new TransactionTag();
     tag.setName(getStringValue(cursor, "tag"));
     tags.add(tag);
     } while (cursor.moveToNext());
     }
     cursor.close();
     return tags;
     }

     private Double getAvgDay() {
     if (DatabaseUtils.queryNumEntries(db, "\"transaction\"") == 0) {
     return 0D;
     }
     // Get start date
     Cursor cursor = db.query("\"transaction\"",
     new String[]{"accountingdate"}, null, null, null, null,
     "accountingDate ASC", "1");
     cursor.moveToFirst();
     final Date start = FmtUtil.stringToDate(SQLITE_DATE_FORMAT, getStringValue(
     cursor, "accountingdate"));
     cursor.close();
     // Get stop date
     cursor = db.query("\"transaction\"",
     new String[]{"accountingdate"}, null, null, null, null,
     "accountingDate DESC", "1");
     cursor.moveToFirst();
     final Date stop = FmtUtil.stringToDate(SQLITE_DATE_FORMAT, getStringValue(
     cursor, "accountingdate"));
     // Calculate number of days
     final int days =
     (int) ((stop.getTime() - start.getTime()) / 1000) / 86400;
     cursor.close();
     // Sum
     cursor = db.query("\"transaction\"",
     new String[]{"SUM(amountout) AS sum"}, null, null, null, null,
     null, "1");
     cursor.moveToFirst();
     Double avg = Double.parseDouble(getStringValue(cursor, "sum")) / days;
     cursor.close();
     return avg;
     }

     /**
     * Calculate average consumpetion per day, week and month
     *
     * @return Average consumption
     */
    /*public AverageConsumption getAvg() {
        final double avgPerDay = getAvgDay();
        final AverageConsumption avg = new AverageConsumption();
        avg.setDay(avgPerDay);
        avg.setWeek(avgPerDay * 7);
        avg.setMonth(avgPerDay * 30.4368499);
        return avg;
    } */


}
