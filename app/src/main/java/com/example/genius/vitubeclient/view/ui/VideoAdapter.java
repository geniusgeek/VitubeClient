package com.example.genius.vitubeclient.view.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.genius.vitubeclient.R;
import com.example.genius.vitubeclient.model.Video;
import com.example.genius.vitubeclient.view.VideoListActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Show the view for each Video's meta-data in a ListView.
 */
public class VideoAdapter extends BaseAdapter {
    /**
     * Allows access to application-specific resources and classes.
     */
    private final Context mContext;

    /**
     * ArrayList to hold list of Videos that is shown in ListView.
     */
    private List<Video> videoList = new ArrayList<>();

    /**
     * Construtor that stores the Application Context.
     *
     * @param context
     */
    public VideoAdapter(Context context) {
        super();
        mContext = context;
    }

    /**
     * Method used by the ListView to "get" the "view" for each row of
     * data in the ListView.
     *
     * @param position The position of the item within the adapter's data
     *                 set of the item whose view we want. convertView The
     *                 old view to reuse, if possible. Note: You should
     *                 check that this view is non-null and of an
     *                 appropriate type before using. If it is not possible
     *                 to convert this view to display the correct data,
     *                 this method can create a new view. Heterogeneous
     *                 lists can specify their number of view types, so
     *                 that this View is always of the right type (see
     *                 getViewTypeCount() and getItemViewType(int)).
     * @param parent   The parent that this view will eventually be
     *                 attached to
     * @return A View corresponding to the data at the specified
     * position.
     */
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Video video = videoList.get(position);
         if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.video_adapter_item, null);
            final ViewHolder  viewHolder= new ViewHolder();
            convertView.setTag(viewHolder);

            viewHolder.videoImageView=(ImageView) convertView.findViewById(R.id.video_thumbnail_imageview);

                            Picasso.with(mContext)
                                    .load(video.getDataUrl())
                                    .placeholder(R.drawable.ic_thumbnail_placeholder)
                                    .error(R.drawable.ic_thumbnail_placeholder)
                                    .resize(120,120)//resize the thumbnail
                                    .into( viewHolder.videoImageView);


            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.video_title_textview);
             viewHolder.ratingTextView = (TextView) convertView.findViewById(R.id.video_rating_textview);
             viewHolder.durationTextView  = (TextView) convertView.findViewById(R.id.video_duration_textview);

             viewHolder.downloadButton=(Button)convertView.findViewById(R.id.video_download_button);
             viewHolder.watchButton=(Button)convertView.findViewById(R.id.video_watch_button);
        }
         ViewHolder viewHolder = (ViewHolder) convertView.getTag();
         viewHolder.position=position;

        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VideoListActivity)mContext).downloadVideo(getItem(position));
            }
        });

        viewHolder.watchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VideoListActivity) mContext).watchVideo(getItem(position));
            }
        });
        viewHolder.titleTextView.setText(video.getTitle());
        viewHolder.ratingTextView.setText("Ratings:"+ String.valueOf(video.getRatings()));
        viewHolder.durationTextView.setText(String.valueOf(video.getDuration())+" min");

        return convertView;
    }

    /**
     * use the viewholder pattern to cache the view items thereby increasing view scroll performance
     */
    public static class ViewHolder {
        public TextView titleTextView, ratingTextView,durationTextView;
        public ImageView videoImageView;
        public Button downloadButton,watchButton;
        public int position;
    }
    /**
     * Adds a Video to the Adapter and notify the change.
     */
    public void add(Video video) {
        videoList.add(video);
        notifyDataSetChanged();
    }

    /**
     * Removes a Video from the Adapter and notify the change.
     */
    public void remove(Video video) {
        videoList.remove(video);
        notifyDataSetChanged();
    }

    /**
     * Get the List of Videos from Adapter.
     */
    public List<Video> getVideos() {
        return videoList;
    }

    /**
     * Set the Adapter to list of Videos.
     */
    public void setVideos(List<Video> videos) {
        this.videoList = videos;
        notifyDataSetChanged();
    }

    /**
     * Get the no of videos in adapter.
     */
    @Override
    public int getCount() {
        return videoList.size();
    }

    /**
     * Get video from a given position.
     */
    public Video getItem(int position) {
        return videoList.get(position);
    }

    /**
     * Get Id of video from a given position.
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get Id of video from a given position.
     */
    public int getItemId(long position) {
        return (int) position;
    }


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
