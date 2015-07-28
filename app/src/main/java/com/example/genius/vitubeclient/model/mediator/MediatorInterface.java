package com.example.genius.vitubeclient.model.mediator;

import android.content.Context;
import android.net.Uri;

import com.example.genius.vitubeclient.model.Video;

import java.util.List;

/**
 * Created by Genius on 7/19/2015.
 * This plays the role of the mediator in the mediator pattern. THis mediates between the client and server
 * This also uses a variant of the asynchronous completion token pattern. TO request for a service and passing the
 * token(request code) and passes the request code(token)  as the result of the operation ;
 */

public interface MediatorInterface {
    int REQUEST_UPLOAD = 0;
    int REQUEST_DOWNLOAD = 1;
    int REQUEST_RATE = 2;

    void uploadVideo(int requestCode,Context context, Uri requestUri);

    void downloadVideo(int requestCode, Context context, Video video);

    double rateVideo(int requestCode, Video video, double rating);

    List<Video> getVideoList();
}
