package com.wubydax.hexconverter;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

/*      Created by Roberto Mariani and Anna Berkovitch, 28/06/2016
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
public class Utils {

    public static float getOrientation(Uri photoUri) {
        Cursor cursor = MyApp.getContext().getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        int degrees = -1;
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                degrees = cursor.getInt(0);
            }
            cursor.close();
        }

        return (float) degrees;
    }

    public static String convertToARGB(int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + alpha + red + green + blue;
    }

    public static int convertToColorInt(String argb) throws IllegalArgumentException {

        if (!argb.startsWith("#")) {
            argb = "#" + argb;
        }

        return Color.parseColor(argb);
    }

    public static String convertToRGB(int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + red + green + blue;
    }

    public static String getSmaliColor(int color) {
        String smaliString;
        if (Color.alpha(color) >= 128) {
            smaliString = "-0x" + Integer.toHexString((color * -1));
        } else {
            smaliString = "0x" + Integer.toHexString(color);
        }
        return smaliString;
    }
}
