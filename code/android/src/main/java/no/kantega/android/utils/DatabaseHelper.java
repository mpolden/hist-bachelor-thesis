package no.kantega.android.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;
import no.kantega.android.models.TransactionType;

import java.util.ArrayList;
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
        final List<Transaction> transactionList = new ArrayList<Transaction>();
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
            transactionList.add(t);
        } while (cursor.moveToNext());
        cursor.close();
        return transactionList;
    }
}
