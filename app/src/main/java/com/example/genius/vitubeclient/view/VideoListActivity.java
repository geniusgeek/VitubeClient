package com.example.genius.vitubeclient.view;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.genius.vitubeclient.R;
import com.example.genius.vitubeclient.common.GenericActivity;
import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.presenter.VideoOps;
import com.example.genius.vitubeclient.presenter.VideoOpsImpl;
import com.example.genius.vitubeclient.presenter.services.DownloadIntentService;
import com.example.genius.vitubeclient.presenter.services.GenericDownloadUploadService;
import com.example.genius.vitubeclient.presenter.services.UploadIntentService;
import com.example.genius.vitubeclient.utils.Utils;
import com.example.genius.vitubeclient.view.ui.UploadVideoDialogFragment;
import com.example.genius.vitubeclient.view.ui.VideoAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * THis class plays the role of the adaptee to the ListActivity using VideoOps.ListViewOps<VideoAdapter>
 *     and also an adaptee to the GenericAsyncTaskOps(which publishes the calls from the asyncTask to the activity thereby functioning as
 *     the asynctask).
 * {this also plays the role of the Active object in the active object pattern}. and CommandExecutor or reciever in the commandPattern
 * /commandProcessor pattern . It also plays the role of the concrete Template Object/class in the template method for the GenericActivity
 * and the GenericAsyncTaskOps classes respectively
 *
 */
public class VideoListActivity extends GenericActivity<VideoOps.ListViewOps<VideoAdapter>,VideoOpsImpl>
                               implements  VideoOpsImpl.ListViewOps<VideoAdapter>, View.OnClickListener,//make the activity a listViewActivity and handle click events
                                                            UploadVideoDialogFragment.OnVideoSelectedListener{//implement listener for the dialog to select video methods


    private WeakReference<ListView> mListView;

    private WeakReference<FloatingActionButton> mFab;

    // innitaialize the ops class, register the reciever,
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, VideoOpsImpl.class, this);



        IntentFilter filter= new IntentFilter(DownloadIntentService.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(UploadIntentService.ACTION_UPLOAD_COMPLETE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadCastReciever, filter);

    }



    public void innitializeViewsAndSetListeners() {
         // Get weak reference to the ListView for displaying the results  entered.
        mListView=new WeakReference<>((ListView) findViewById(R.id.video_listview));
        mFab=new WeakReference<>((FloatingActionButton) findViewById(R.id.fabButton));
        //set the click listener for the listview
        getListView().setOnItemClickListener(getOps());
        getFab().setOnClickListener(this);

    }


    //cancel the reciever
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadCastReciever);//unregister the broadcast reciever
        if(getOps()!=null){
              getOps().onDestroy();
        }
        super.onDestroy();
    }




    /**
     * TemplateMethod of the template method pattern,
     * @return s the menuView
     * */

    @Override
    protected int getMenuView() {
        return R.menu.menu_video_list;
    }

    /**
     * get the fab button reference
     * @return
     */
    FloatingActionButton getFab(){
        return mFab.get();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapterData(List<?> dataList) {
          VideoAdapter mVideoAdapter= (VideoAdapter) getAdapter();
        if(mVideoAdapter==null)
            mVideoAdapter= new VideoAdapter(this.getApplicationContext());
         mVideoAdapter.setVideos((List<Video>) dataList);
         setAdapter(mVideoAdapter);
    }

    @Override
    public void setAdapter(VideoAdapter adapter) {
        if(mListView!=null){
            synchronized (this){
                if(getListView()==null)
                    Utils.showToast(this,"listvie is nul");
                else
                //get exclusive access to set the adapter
                getListView().setAdapter(adapter);
            }
        }

    }

    /**
     * get the adapter currently used for the listview, this adapter maybe different which the one that was inserted
     * @return
     */
    @Override
    public ListAdapter getAdapter() {
        return getListView().getAdapter();
    }

    @Override
    public ListView getListView() {
        return mListView.get();
    }


    @Override
    public void onClick(View v) {
          showDialogFragment(true);
    }

    /**
     * download the video using the service and mediator in the VideoOpsImpl class
     * @param video
     */
    public void downloadVideo(final Video video){
        getOps().downloadVideo(video);
    }

    /**
     * streeming of video, not yet supported.
     * TODO support this
     * @param video
     */

    public void watchVideo(final Video video){
        getOps().watchVideo(video);
    }

    void showDialogFragment(boolean show){
        FragmentManager fm=getFragmentManager();
        if(show){
            UploadVideoDialogFragment mUploadVideoDialogFragment= new UploadVideoDialogFragment();
            //fm.beginTransaction().add(mUploadVideoDialogFragment,UploadVideoDialogFragment.TAG).commit();
            mUploadVideoDialogFragment.show(fm, UploadVideoDialogFragment.TAG);
        }else{
            Fragment fragment=fm.findFragmentByTag(UploadVideoDialogFragment.TAG);
            fm.beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onVideoSelected(UploadVideoDialogFragment.OperationType which) {
        showDialogFragment(false);//remove the dialogFragment
        Intent recordPickIntent=null;
        int requestCode=-1;
        switch (which){
            case  RECORD_VIDEO:
                //record the video
                  recordPickIntent=makeRecordVideoIntent();
                requestCode=getOps().REQUEST_RECORD_VIDEO;
                break;
            case  VIDEO_GALLERY:
                //pick from the video gallery
                recordPickIntent=makePickVideoIntent();
                requestCode=getOps().REQUEST_PICK_VIDEO;

                break;
        }
        if(recordPickIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(recordPickIntent,requestCode);
        }
    }

    /**
     * factory method for making intent to record video from the phone to upload to the video server,
     * This plays the role of the factory method in the factory method pattern
     * @return
     */
    private Intent makeRecordVideoIntent(){
        Intent intent= new Intent();
        intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        return intent;
    }

    /**
     * factory method for making intent to pick  video from the phone gallery to upload to the video server
     *  This plays the role of the factory method in the factory method pattern
     * @return
     */
    private Intent makePickVideoIntent(){
       Intent intent=new Intent();
       intent.setAction(Intent.ACTION_GET_CONTENT);//get the content for instant access,
       //intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("video/*");
       intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"video/*");//set the type of the resource to retrieve
       return intent;
    }

    /**
     * This ses Reactor pattern since the operation is synchronous and the app waits for the result before continuing
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getOps().onResult( requestCode,  resultCode,  data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    BroadcastReceiver mBroadCastReciever= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();
            long videoId=intent.getLongExtra(GenericDownloadUploadService.VIDEO_ID, 0l);
            VideoAdapter videoAdapter=(VideoAdapter)getAdapter();
            if(action.equals(DownloadIntentService.ACTION_DOWNLOAD_COMPLETE)){
                //hide , or disable the download button
                disableDownloadButton((int) videoId, videoAdapter);

             }else{
                // refresh only 1 item, this is good for ui performance
                videoAdapter.add(getOps().getVideoList().get(videoAdapter.getCount()));
                videoAdapter.notifyDataSetChanged();
            }

        }

        private void disableDownloadButton(int videoId, VideoAdapter videoAdapter) {
            View view= videoAdapter.getViewByPosition(videoId,getListView());
            VideoAdapter.ViewHolder viewHolder= (VideoAdapter.ViewHolder) view.getTag();
            viewHolder.downloadButton.setEnabled(false);
        }
    };

}
