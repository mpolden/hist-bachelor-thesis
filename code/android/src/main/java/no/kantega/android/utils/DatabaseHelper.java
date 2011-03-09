package no.kantega.android.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import no.kantega.android.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static long insertType(final SQLiteDatabase db,
                                   final TransactionType t) {
        final ContentValues values = new ContentValues();
        values.put("name", t.getName());
        final long typeId = db.insertWithOnConflict("transactiontype", null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d(TAG, "Inserted transaction type with ID: " + typeId);
        return typeId;
    }

    private static long insertTag(final SQLiteDatabase db,
                                  final TransactionTag t) {
        final ContentValues values = new ContentValues();
        values.put("name", t.getName());
        final long tagId = db.insertWithOnConflict("transactiontag", null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d(TAG, "Inserted transaction tag with ID: " + tagId);
        return tagId;
    }

    public static void insert(final SQLiteDatabase db, final Transaction t) {
        final long typeId = insertType(db, t.getType());
        final long tagId = insertTag(db, t.getTag());
        final ContentValues values = new ContentValues();
        values.put("accountingdate", FmtUtil.date(SQLITE_DATE_FORMAT,
                t.getAccountingDate()));
        values.put("amountin", t.getAmountIn());
        values.put("amountout", t.getAmountOut());
        values.put("archiveref", t.getArchiveRef());
        values.put("fixeddate", FmtUtil.date(SQLITE_DATE_FORMAT,
                t.getFixedDate()));
        values.put("text", t.getText());
        values.put("type_id", typeId);
        values.put("tag_id", tagId);
        final long transactionId = db.insert("\"transaction\"", null, values);
        Log.d(TAG, "Inserted transaction with ID: " + transactionId);
    }

    private static String getValue(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static List<Transaction> getOrderedByDateDesc(SQLiteDatabase db,
                                                         int limit) {
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
        do {
            Transaction t = new Transaction();
            t.setAccountingDate(FmtUtil.stringToDate(SQLITE_DATE_FORMAT,
                    getValue(cursor, "accountingdate")));
            t.setAmountIn(Double.parseDouble(getValue(cursor, "amountin")));
            t.setAmountOut(Double.parseDouble(getValue(cursor, "amountout")));
            t.setArchiveRef(getValue(cursor, "archiveref"));
            t.setFixedDate(FmtUtil.stringToDate(SQLITE_DATE_FORMAT,
                    getValue(cursor, "fixeddate")));
            t.setText(getValue(cursor, "text"));
            TransactionTag tag = new TransactionTag();
            tag.setName(getValue(cursor, "tag"));
            t.setTag(tag);
            TransactionType type = new TransactionType();
            type.setName(getValue(cursor, "type"));
            t.setType(type);
            transactions.add(t);
        } while (cursor.moveToNext());
        cursor.close();
        return transactions;
    }

    public static List<AggregatedTag> getTags(final SQLiteDatabase db,
                                              final int limit) {
        final Cursor cursor = db.query("\"transaction\" " +
                "INNER JOIN transactiontag " +
                "ON transactiontag.id = \"transaction\".tag_id ",
                new String[]{"transactiontag.name",
                        "SUM(\"transaction\".amountout) AS sum"},
                null, null, "transactiontag.name", "sum DESC",
                String.valueOf(limit));
        final List<AggregatedTag> aggregatedTags =
                new ArrayList<AggregatedTag>();
        cursor.moveToFirst();
        do {
            AggregatedTag at = new AggregatedTag();
            at.setAmount(Double.parseDouble(getValue(cursor, "sum")));
            at.setName(getValue(cursor, "name"));
            aggregatedTags.add(at);
        } while (cursor.moveToNext());
        cursor.close();
        return null;
    }

    private static Double getAvgDay(final SQLiteDatabase db) {
        // Get start date
        Cursor cursor = db.query("\transaction\"",
                new String[]{"accountingdate"}, null, null, null,
                "accountingDate ASC", "1");
        cursor.moveToFirst();
        final Date start = FmtUtil.stringToDate(SQLITE_DATE_FORMAT, getValue(
                cursor, "accountingdate"));
        cursor.close();
        // Get stop date
        cursor = db.query("\transaction\"",
                new String[]{"accountingdate"}, null, null, null,
                "accountingDate DESC", "1");
        cursor.moveToFirst();
        final Date stop = FmtUtil.stringToDate(SQLITE_DATE_FORMAT, getValue(
                cursor, "accountingdate"));
        // Calculate number of days
        final int days =
                (int) ((stop.getTime() - start.getTime()) / 1000) / 86400;
        cursor.close();
        // Sum
        cursor = db.query("\transaction\"",
                new String[]{"SUM(amountout) AS sum"}, null, null, null,
                null, "1");
        cursor.moveToFirst();
        return Double.parseDouble(getValue(cursor, "sum")) / days;
    }

    public static AverageConsumption getAvg(final SQLiteDatabase db) {
        final double avgPerDay = getAvgDay(db);
        final AverageConsumption avg = new AverageConsumption();
        avg.setDay(avgPerDay);
        avg.setWeek(avgPerDay * 7);
        avg.setMonth(avgPerDay * 30.4368499);
        return avg;
    }
}
