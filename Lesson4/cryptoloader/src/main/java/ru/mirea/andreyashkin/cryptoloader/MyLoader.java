package ru.mirea.andreyashkin.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {
    private String firstName;
    private byte[] cryptText;
    private byte[] key;
    private SecretKey originalKey;
    public static final String ARG_WORD = "word";

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null) {
            cryptText = args.getByteArray(ARG_WORD);
            key = args.getByteArray("key");

            originalKey = new SecretKeySpec(key, 0, key.length, "AES");
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {

        SystemClock.sleep(5000);
        return firstName;
    }
}
