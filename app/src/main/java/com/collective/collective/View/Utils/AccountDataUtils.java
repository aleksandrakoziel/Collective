package com.collective.collective.View.Utils;


import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class AccountDataUtils {
    public static final String PROFILE_PICTURE_KEY = "profile_picture";
    public static final String PROFILE_USERNAME_KEY = "profile_username";
    public static final String SHARED_PREFERENCES = "shared_prefs";
    public static final String DEFAULT_USERNAME = "username";
    public static final String DEFAULT_PROFILE_PICTURE_FILENAME = "profile.jpg";

    public static void saveProfilePicture(Bitmap bitmapImage, Context context) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", MODE_PRIVATE);
        File mypath = new File(directory, DEFAULT_PROFILE_PICTURE_FILENAME);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String saveDirectory = directory.getAbsolutePath();
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PROFILE_PICTURE_KEY, saveDirectory);
        editor.apply();
    }

    public static Bitmap loadProfilePictureStorage(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String path = pref.getString(PROFILE_PICTURE_KEY, null);

        Bitmap b = null;
        try {
            File f = new File(path, DEFAULT_PROFILE_PICTURE_FILENAME);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void saveAccountUsername(Context context, String username) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PROFILE_USERNAME_KEY, username);
        editor.apply();
    }

    public static String getAccountUsername(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return pref.getString(PROFILE_USERNAME_KEY, DEFAULT_USERNAME);
    }
}
