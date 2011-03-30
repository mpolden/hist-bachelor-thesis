package no.kantega.android.afp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import no.kantega.android.afp.R;

import java.util.HashMap;
import java.util.Map;

public class ResourceHelper {

    private static final Map<String, Integer> imageIds;

    static {
        imageIds = new HashMap<String, Integer>() {{
            put("Annet", R.drawable.tag_creditcard);
            put("Barn", R.drawable.tag_baby);
            put("Ferie", R.drawable.tag_sun);
            put("Fornøyelser", R.drawable.tag_controller);
            put("Helse", R.drawable.tag_medicalbag);
            put("Husholdning", R.drawable.tag_house);
            put("Klær", R.drawable.tag_tshirt);
            put("Mat", R.drawable.tag_forkandknife);
            put("Transport", R.drawable.tag_airplane);
            put("Sparing", R.drawable.tag_linechart);

        }};
    }

    public static Drawable getImage(Context context, String tag) {
        final Integer imageId = imageIds.get(tag);
        if (imageId != null) {
            return context.getResources().getDrawable(imageId);
        } else {
            return context.getResources().getDrawable(R.drawable.tag_warning);
        }
    }

}
