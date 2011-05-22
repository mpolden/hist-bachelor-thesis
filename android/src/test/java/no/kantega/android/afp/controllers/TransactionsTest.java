package no.kantega.android.afp.controllers;

import no.kantega.android.afp.MavenizedTestRunner;
import no.kantega.android.afp.OverviewActivity;
import no.kantega.android.afp.utils.DatabaseHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Test case for Transactions
 */
@RunWith(MavenizedTestRunner.class)
public class TransactionsTest {

    private Transactions transactions;

    /**
     * Set up database
     */
    @Before
    public void setUp() {
        OverviewActivity activity = new OverviewActivity();
        this.transactions = new Transactions(
                new DatabaseHelper(activity.getApplicationContext(), null, 1));
    }

    /**
     * Test add
     */
    @Test
    public void testAdd() {
        assertNotNull(transactions);
    }
}
