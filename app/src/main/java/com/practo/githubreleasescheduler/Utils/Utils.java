package com.practo.githubreleasescheduler.Utils;

import android.database.Cursor;

/**
 * Created by shreyans on 26/08/16.
 */
public class Utils {
    public static boolean isCursorEmpty(Cursor cursor) {
        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0) {
            return false;
        }
        return true;
    }
}
