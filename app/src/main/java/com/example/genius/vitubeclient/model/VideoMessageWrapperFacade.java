package com.example.genius.vitubeclient.model;

import android.os.Message;

import com.example.genius.vitubeclient.model.mediator.webendpoint.VideoServiceProxy;

/**
 * Created by Genius on 7/21/2015.
 * This defines a thin facade(From the wrapper/adapter facade pattern) around the message class.
 * So as to define a neater representation of a video Message sent to a service
 */
public class VideoMessageWrapperFacade {

    private Message mMessage;
    private VideoServiceProxy mApiObject;


    public VideoMessageWrapperFacade() {

    }

    public VideoMessageWrapperFacade(Message message){
        setMessage(message);
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message mMessage) {
        this.mMessage = mMessage;
    }


    public VideoServiceProxy getApiObject() {
        return mApiObject;
    }

    public void setApiObject(VideoServiceProxy mApiObject) {
        this.mApiObject = mApiObject;
    }

    public void setApiObjectForMessage(VideoServiceProxy mApiObject,Message message) {

        if(message.obj==null)
            throw new NullPointerException(" the object being sent with message is null");
        setApiObject(mApiObject);
        message.obj=this.getApiObject();
        setMessage(message);
    }

    public VideoServiceProxy getApiObjectForMessage(Message message){
        if(message ==null)
            throw new NullPointerException(" the   message is null");

        if(message.obj==null)
            throw new NullPointerException(" the object being sent with message is null");

        setMessage(message);
        return (VideoServiceProxy) getMessage().obj;

     }
}
