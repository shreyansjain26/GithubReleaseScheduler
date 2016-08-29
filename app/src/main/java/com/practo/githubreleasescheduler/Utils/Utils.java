package com.practo.githubreleasescheduler.Utils;

import android.database.Cursor;


public class Utils {
    public static boolean isCursorEmpty(Cursor cursor) {
        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0) {
            return false;
        }
        return true;
    }
}
