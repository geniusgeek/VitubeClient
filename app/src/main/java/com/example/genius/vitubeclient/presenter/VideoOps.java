package com.example.genius.vitubeclient.presenter;

import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.genius.vitubeclient.common.ConfigurableOps;
import com.example.genius.vitubeclient.common.GenericAsyncTaskOps;
import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.view.ui.VideoAdapter;

import java.util.List;

/**
 * Created by Genius on 7/20/2015.
 */
public interface VideoOps extends GenericAsyncTaskOps<Void,Integer,List<Video>>,ConfigurableOps<VideoOps.ListViewOps<VideoAdapter>> {
     /**
     * called when the app is shutting down, thereby carrying out necessary cleanup
     * operations. such as stashing download for restoration etc
     */
    void onDestroy();

    /**
     * this interface abstracts the ListActivity function
     * it functions more like an adapter that converts atn activity to a list activity.
     * it therefore plays the role of   the adapter in the adapter pattern
     *
     * @param <ListViewAdapter>
     */
    interface ListViewOps<ListViewAdapter extends BaseAdapter> {
        void setAdapterData(List<?> dataList);

        ListAdapter getAdapter();//note the adpter that was added may not be thesame as the adapter that is retrieved, but they are all instances of contextWrapper

        void setAdapter(ListViewAdapter adapter);

        ListView getListView();//ensure that listview is not null

     }


    /**
     * The various means of uploading a Video.
     */
      enum Operations {

        DOWNLOAD("Download"),

        UPLOAD("Upload"),

        STREAM("Stream");

        private String value;


        Operations(String name) {
            this.value = name;
        }

        String getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return getValue();
        }
    }


}
