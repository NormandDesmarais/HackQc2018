package com.example.payne.simpletestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/*
Example of call:
DeviceStorage.saveInSharedPreferences("test", this);
 */

public class DeviceStorage {

    public static final String APPLICATION_ID = "com.example.payne.simpletestapp"; // pour le Folder_Path
    public static final int MAX_SIZE = 1024; // Pour ne pas détruire la mémoire
    public static final int DEFAULT_VALUE = 0; // en cas d'échec, valeur de retour


    /**
     * https://developer.android.com/training/data-storage/files#WriteInternalStorage
     * https://developer.android.com/training/data-storage/shared-preferences#java
     */
    /*
    Android stores Shared Preferences settings as XML file in shared_prefs folder under
    DATA/data/{application package} directory.
    The DATA folder can be obtained by calling Environment.getDataDirectory().
     */



    /**
     *
     * @param key the key
     * @param context = getActivity();
     * @return
     */
    public static SharedPreferences saveInSharedPreferences(String key, Context context) {
        /*
        When naming your shared preference files, you should use a name that's uniquely identifiable
        to your app. An easy way to do this is prefix the file name with your application ID.
        For example: "com.example.myapp.PREFERENCE_FILE_KEY"
        So: key = getString(R.string.preference_file_key)
         */


        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int highScoreTest = 1337;

        if(highScoreTest > sharedPreferences.getInt(key, DEFAULT_VALUE)) {
            editor.putInt(key, highScoreTest);
            editor.apply();
        }

        /*
        apply() changes the in-memory SharedPreferences object immediately but writes the updates
        to disk asynchronously. Alternatively, you can use commit() to write the data to disk
        synchronously. But because commit() is synchronous, you should avoid calling it from your
        main thread because it could pause your UI rendering.
         */

        Toast.makeText(context, "Value is: " + sharedPreferences.getInt(key, DEFAULT_VALUE), Toast.LENGTH_LONG).show();

        return sharedPreferences;
    }
}
