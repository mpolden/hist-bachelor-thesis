package no.kantega.android.afp.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import no.kantega.android.afp.models.TransactionType;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 18;
    private Dao<Transaction, Integer> transactionDao;
    private Dao<TransactionTag, Integer> transactionTagDao;
    private Dao<TransactionType, Integer> transactionTypeDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String database, int databaseVersion) {
        super(context, database, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        Log.d(TAG, "Creating database");
        try {
            TableUtils.createTable(connectionSource, Transaction.class);
            TableUtils.createTable(connectionSource, TransactionTag.class);
            TableUtils.createTable(connectionSource, TransactionType.class);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        Log.d(TAG, "Upgrading database");
        try {
            TableUtils.dropTable(connectionSource, Transaction.class, true);
            TableUtils.dropTable(connectionSource, TransactionTag.class, true);
            TableUtils.dropTable(connectionSource, TransactionType.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    /**
     * Retrieve a DAO for Transaction
     *
     * @return DAO
     */
    public Dao<Transaction, Integer> getTransactionDao() {
        if (transactionDao == null) {
            try {
                transactionDao = BaseDaoImpl.createDao(getConnectionSource(), Transaction.class);
            } catch (SQLException e) {
                Log.e(TAG, "Could not create DAO for Transaction");
            }
        }
        return transactionDao;
    }

    /**
     * Retrieve a DAO for TransactionTag
     *
     * @return DAO
     */
    public Dao<TransactionTag, Integer> getTransactionTagDao() {
        if (transactionTagDao == null) {
            try {
                transactionTagDao = BaseDaoImpl.createDao(getConnectionSource(), TransactionTag.class);
            } catch (SQLException e) {
                Log.e(TAG, "Could not create DAO for TransactionTAg");
            }
        }
        return transactionTagDao;
    }

    /**
     * Retrieve a DAO for TransactionType
     *
     * @return DAO
     */
    public Dao<TransactionType, Integer> getTransactionTypeDao() {
        if (transactionTypeDao == null) {
            try {
                transactionTypeDao = BaseDaoImpl.createDao(getConnectionSource(), TransactionType.class);
            } catch (SQLException e) {
                Log.e(TAG, "Could not create DAO for TransactionType");
            }
        }
        return transactionTypeDao;
    }
}
