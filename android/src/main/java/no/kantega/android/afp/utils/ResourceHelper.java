package no.kantega.android.afp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import no.kantega.android.afp.R;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides helper methods for resources
 */
public class ResourceHelper {

    private static final Map<String, Integer> imageIds;

    static {
        imageIds = new HashMap<String, Integer>() {{
            put("Annet", R.drawable.tag_creditcard);
            put("Barn", R.drawable.tag_baby);
            put("Bolig", R.drawable.tag_house);
            put("Ferie", R.drawable.tag_sun);
            put("Fornøyelser", R.drawable.tag_controller);
            put("Helse", R.drawable.tag_medicalbag);
            put("Klær", R.drawable.tag_tshirt);
            put("Mat", R.drawable.tag_forkandknife);
            put("Transport", R.drawable.tag_airplane);
            put("Sparing", R.drawable.tag_piggybank);
            put("Total", R.drawable.tag_linechart);
        }};
    }

    /**
     * Get image for the given tag
     *
     * @param context Application context
     * @param tag     Tag
     * @return Image for tag
     */
    public static Drawable getImage(Context context, String tag) {
        final Integer imageId = imageIds.get(tag);
        if (imageId != null) {
            return context.getResources().getDrawable(imageId);
        } else {
            return context.getResources().getDrawable(R.drawable.tag_warning);
        }
    }
}
