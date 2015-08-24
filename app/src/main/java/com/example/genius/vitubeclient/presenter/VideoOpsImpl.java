package com.example.genius.vitubeclient.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.genius.vitubeclient.common.GenericAsyncTask;
import com.example.genius.vitubeclient.common.RetainedFragmentManager;
import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.model.mediator.MediatorInterface;
import com.example.genius.vitubeclient.model.mediator.VideoServiceMediator;
import com.example.genius.vitubeclient.utils.Constants;
import com.example.genius.vitubeclient.utils.Utils;
import com.example.genius.vitubeclient.utils.VideoFileUtils;
import com.example.genius.vitubeclient.view.VideoListActivity;
import com.example.genius.vitubeclient.view.ui.VideoAdapter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Genius on 7/20/2015.
 */
public class VideoOpsImpl implements ListView.OnItemClickListener , VideoOps, ResultRecieved {

    private String TAG=getClass().getSimpleName();

    private RetainedFragmentManager mRetainedFragmentManager;

    private ExecutorService mExecutorService= Executors.newFixedThreadPool(5);
    private List<Video> mVideoList;

    private GenericAsyncTask<Void,Integer,List<Video>,VideoOps> mAsyncTask;

    private volatile boolean mConnectionStatus=false;

    WeakReference<ListViewOps> mActivityView;
    /**
     * The mediator between the Coleagues{client and server}
     */
    MediatorInterface mVideoServiceMediator;

    /**
     * request tokens for picking  video
     */
    public static int REQUEST_PICK_VIDEO = 3;
    /**
     * request tokens for   recording video
     */
    public static int REQUEST_RECORD_VIDEO = 4;

    /**
     * No ops OpsImpl for Ops.newInstance()
     */
    public VideoOpsImpl() {
             mVideoServiceMediator = new VideoServiceMediator();//create an instance of the videoservice mediator
    }



    /**
     * Use this method only when we are trying to fail safe, to ensure that no data was lost when the asynctask was executing
     * as the phone was rotating and the operation was done and just close  to updating the ui
     * @param asyncTask
     */
    private void updateUiWithAsync(GenericAsyncTask<Void,Integer,List<Video>, VideoOps> asyncTask) {
        Log.d(TAG,"updating ui with previous async task");
        getActivityView().setAdapterData(asyncTask.getStashedResult());//get the stashed result if any
        asyncTask.cancel(true);
        //asyncTask=null;
    }


    /**
     * get video list from the server,
     * please note that this synchronous operation must be called in a background thread, thereby improving the responsiveness of the app
     * @return
     */
     public List<Video> getVideoList() {
         if(mVideoList==null)
            return mVideoServiceMediator.getVideoList();
         else
             return mVideoList;
     }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //show the nextpage of the video app for the user to watch or download
        Utils.showToast((Context) getActivityView(),"rating not yet added");
    }


    @Override
    public void onConfiguration(ListViewOps listViewOps, boolean firstTimeIn) {
        mActivityView = new WeakReference<>(listViewOps); //update the reference to the view(ie the activity)
        mRetainedFragmentManager= ((VideoListActivity)getActivityView()).getRetainedFragmentManager();//gethe fragment manager for the activity
        ((VideoListActivity)getActivityView()).innitializeViewsAndSetListeners();
        if(firstTimeIn){
             VideoAdapter videoAdapter= new VideoAdapter((Context) getActivityView());
            listViewOps.setAdapter(videoAdapter);

            updateUi();//update the ui for the listview
        }

        if(!firstTimeIn && mRetainedFragmentManager.containsKey(GenericAsyncTask.TAG)){
            attemptDownloadRecovery();
        }

    }

    /**
     * if there was an uncompleted download then try to complete it.
     */
    private void attemptDownloadRecovery() {
        mAsyncTask=mRetainedFragmentManager.get(GenericAsyncTask.TAG);

        if(mAsyncTask!=null&&!mAsyncTask.isCancelled()){
            if(mAsyncTask.getStashedResult()!=null)
                updateUiWithAsync(mAsyncTask);//update the ui with the result of the uncompleted operation and complete it
            else
                updateUi();//if the ui did not finish updating then redo
        }
       // mRetainedFragmentManager.remove(GenericAsyncTask.TAG);//TODo please enable this when you have a contentProvider
    }

    ListViewOps getActivityView() {
        return mActivityView.get();
    }


    /**
     * Recieve the result of the operation from the activity
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {

        //check for selecting video operations
        if(requestCode== REQUEST_PICK_VIDEO ||requestCode== REQUEST_PICK_VIDEO){
            if(resultCode!= Activity.RESULT_OK){
                //result was cancelled
                Utils.showToast((Context) getActivityView(),"operation was not successful");
                return;
            }
            Uri videoUri=data.getData();

            String realPathFromURI= VideoFileUtils.getRealPathFromURI((Context) getActivityView(), videoUri);

            mVideoServiceMediator.uploadVideo(MediatorInterface.REQUEST_UPLOAD,((Context) getActivityView()).getApplicationContext(),Uri.parse(realPathFromURI) );
        }
        //check for uploading video

        //check for downloading video
    }

    /**
     * download the video using asynchnous method by the service
     * @param video
     */
    public void downloadVideo(Video video){
        mVideoServiceMediator.downloadVideo(MediatorInterface.REQUEST_DOWNLOAD, (Context) getActivityView(), video);
    }

    /**
     * streem the video using synchnous method by the service
     * @param video
     */
    public void watchVideo(Video video){
        Utils.showToast((Context) getActivityView(), "streeming not yet supported");
    }

    /**
     * this long operation runs in background in a different thread from the main thread,
     * this uses active object pattern
     * @param params
     * @return
     */
    @Override
    public List<Video> doInBackground(Void... params) {
          try {
            mConnectionStatus= (boolean) mExecutorService.submit(mCallable).get();
        } catch (Exception e) {
            e.printStackTrace();
              mConnectionStatus=false;
        }
         if(!mConnectionStatus)
            return null;
         return  getVideoList();
    }

    Callable mCallable= new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return Utils.checkConnectionStatus(Constants.VIDEO_POINT);
        }
    };

    @Override
    public void onPostExecute(List<Video> videos) {
         if(!mConnectionStatus && videos==null){
            Utils.showToast(((Context) getActivityView()),"please connect to the server");

            ((VideoListActivity) getActivityView()).finish();
        }else{
             mVideoList=videos;
             getActivityView().setAdapterData(videos);
             mAsyncTask.cancel(true);//cancel the asynctasktrue;
             // mAsyncTask=null;
         }
        Log.d(TAG, "connection status is"+mConnectionStatus+"result gotten"+videos);


    }



    public void updateUi(){
        Log.d(TAG,"updating ui/redoing async");
        mAsyncTask= new GenericAsyncTask(this);
        mAsyncTask.executeOnExecutor(GenericAsyncTask.THREAD_POOL_EXECUTOR);
     }

    @Override
    public void onDestroy() {
        //stash the asynctask to continue with operation if not completed
        if(mAsyncTask!=null && !mAsyncTask.isCancelled()){
            mRetainedFragmentManager.put(GenericAsyncTask.TAG,mAsyncTask);//put the asynctask to continue the execution
        }
    }


}
