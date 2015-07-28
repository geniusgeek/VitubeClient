package com.example.genius.vitubeclient.common;

import android.os.AsyncTask;

/**
 * Defines a generic framework for running an AsyncTask that delegates
 * its operations to the @a Ops parameter.
 */
public class GenericAsyncTask<Params,
        Progress,
        Result,
        Ops extends GenericAsyncTaskOps<Params, Progress, Result>>
        extends AsyncTask<Params, Progress, Result> {
    /**
     * Debugging tag used by the Android logger.
     */
    public static final String TAG =GenericAsyncTask.class.getSimpleName();

    /**
     * Reference to the enclosing Ops object.
     */
    protected Ops mOps;

    /**
     * stashed result after completion of task
     */
    private Result mStashedResult;

    /**
     * Constructor initializes the field.
     */
    public GenericAsyncTask(Ops ops) {
        mOps = ops;
    }

    /**
     * Called in the UI thread prior to running doInBackground() in a
     * background thread.
     */
    // @@ Omit until Android supports default methods in interfaces..
    // @Override
    // protected void onPreExecute() {
    //     mOps.onPreExecute();
    // }

    /**
     * Called in a background thread to process the @a params.
     */
    @SuppressWarnings("unchecked")
    protected Result doInBackground(Params... params) {
        return mOps.doInBackground(params);
    }

    /**
     * Called in the UI thread to process the @a result.
     */
    protected void onPostExecute(Result result) {
        setStashedResult(result);
        mOps.onPostExecute(result);
    }

    /**
     * result data to stash
     */
    public Result getStashedResult() {
        return mStashedResult;
    }

    public void setStashedResult(Result stashedResult) {
        this.mStashedResult = stashedResult;
    }
}
