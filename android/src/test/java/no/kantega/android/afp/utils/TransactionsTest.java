package no.kantega.android.afp.utils;

import no.kantega.android.afp.OverviewActivity;
import no.kantega.android.afp.controllers.Transactions;
import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertTrue;

@RunWith(MavenizedTestRunner.class)
public class TransactionsTest {

    private Transactions transactions;

    @Before
    public void setUp() {
        OverviewActivity activity = new OverviewActivity();
        this.transactions = new Transactions(activity.getApplicationContext(),
                new DatabaseHelper(activity.getApplicationContext(), "test", 1));
    }

    @Test
    public void testAdd() {
        final Transaction t = new Transaction();
        t.setDate(new Date());
        t.setAmount(133.7);
        t.setText("Test");
        t.setId(1);
        final TransactionTag tag = new TransactionTag();
        tag.setId(1);
        tag.setName("Test");
        t.setTag(tag);
        t.setTimestamp(t.getDate().getTime());
        t.setInternal(false);
        t.setDirty(true);
        transactions.add(t);

        assertTrue(transactions.getCount() == 1);
    }

}
