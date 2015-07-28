package com.example.genius.vitubeclient.presenter;

import android.content.Intent;

/**
 * Created by Genius on 7/20/2015.
 */
public interface ResultRecieved {
    void onResult(int requestCode, int resultCode, Intent data);
}
