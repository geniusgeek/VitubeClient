package com.example.genius.vitubeclient.view.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.genius.vitubeclient.R;
import com.example.genius.vitubeclient.utils.Utils;

/**
 * UploadVideoDialog Fragment shows user a Dialog that lists various
 * means of uploading a Video.
 */
public class UploadVideoDialogFragment extends DialogFragment {
    public static final String TAG = UploadVideoDialogFragment.class.getSimpleName();
    /**
     * Callback that will send the result to Activity that implements
     * it, when the Option is selected.
     */
    private OnVideoSelectedListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Hook method called when a fragment is first attached to its
     * activity. onCreate(Bundle) will be called after this.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {

        //retains the instance of te activity across screeen rotation
        super.onAttach(activity);
        try {
            mListener =
                    (OnVideoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException
                    (activity.toString()
                            + " must implement OnVideoSelectedListener");
        }

    }

    /**
     * This method will be called after onCreate(Bundle) and before
     * onCreateView(LayoutInflater, ViewGroup, Bundle).  The default
     * implementation simply instantiates and returns a Dialog class.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Builder for creating a new Dialog.
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_upload_video)
                .setItems(Utils.getEnumNames(OperationType.class),
                        //Set OnClick listener for the Dialog.
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                OperationType type =
                                        OperationType
                                                .values()[which];
                                // Select the means of uploading a video.
                                mListener.onVideoSelected(type);
                            }
                        });

        // Use the Builder pattern to create the Dialog.
        return builder.create();
    }

    /**
     * The various means of uploading a Video.
     */
    public enum OperationType {
        /**
         * Position of Video Gallery Option in List.
         */
        VIDEO_GALLERY("Upload Using Gallery"),

        /**
         * Position of Record Video Option in List.
         */
        RECORD_VIDEO("Record a video To Upload");

        private String value;


        OperationType(String name) {
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


    /**
     * Container Activity must implement this interface
     */
    public interface OnVideoSelectedListener {
        void onVideoSelected(OperationType which);
    }

    @Override
    public void onDestroyView() {
        if(getDialog() !=null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
