package no.kantega.android.afp.utils;

import android.content.Context;
import no.kantega.android.afp.MavenizedTestRunner;
import no.kantega.android.afp.OverviewActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Test case for ResourceHelper
 */
@RunWith(MavenizedTestRunner.class)
public class ResourceHelperTest {

    /**
     * Test getImage
     */
    @Test
    public void testGetImage() {
        final Context context = new OverviewActivity().getApplicationContext();
        assertNotNull(ResourceHelper.getImage(context, "tag"));
        assertNotNull(ResourceHelper.getImage(context, null));
    }
}
