package com.example.genius.vitubeclient.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit.client.Response;

/**
 * Created by Genius on 7/22/2015.
 */
public class VideoStorageUtils {


   public static File getFileToStoreVideo(String videoName, Context context){
       return new File(getExternalStorageUriForFile(context,videoName).getPath());

   }

    /**
     * Notify the mediascannerConnection that a new file is inserted
     */
    public static void notifyMediaScannerConnection(final Context context,String filepathName){
        String[] paths= new String[]{filepathName};
        String[] mimeTypes={"video/*"};
        MediaScannerConnection.scanFile(context,paths,mimeTypes,
                new MediaScannerConnection.OnScanCompletedListener(){
                    //we can maje this null
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Utils.showToast(context,"file name with path"+path +" and uri "+uri+ " was inserted");
                    }
                }
                );

    }

    /**
     * get the picture and video directory instead of just placing  in the download direactory
     *
     * @param videoNameToAppend the video name to append to the uri
     *   @return Uri the return value where the resource is to be placed
     */
   public static Uri getExternalStorageUriForFile(Context context,String videoNameToAppend){
       if(!canWriteExternalDiractoryState())
           return null;//cannot write to external storage
        File file= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);//get the picture and video directory instead of just placing

       if(!file.exists()) //just a failsafe
           file.mkdir();
       //in the download direactory
       SimpleDateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String timeExtra=dateFormat.format(Calendar.getInstance(context.getResources().getConfiguration().locale));
        String path=file.getPath().concat(videoNameToAppend).concat(timeExtra);
        File fileToDelete=new File(path);
       if(fileToDelete.exists())
           fileToDelete.delete();//delete the file if exist
         return Uri.parse(fileToDelete.getPath());

   }

     public static boolean canWriteExternalDiractoryState(){
       if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
           return true;
       return false;
    }

    @SuppressLint("NewApi")
    public static boolean storeVideo(Response response,Context context,String videoName){
       File file=getFileToStoreVideo(videoName,context);

       try(    InputStream inputStream=response.getBody().in();
               OutputStream outputStream=new FileOutputStream(file) )
       {

           byte[] bytes= new byte[4096];//copy large data, note this was employed from IOUtils.copy(in,out)

           while (inputStream.read(bytes)!=-1)
               outputStream.write(bytes);

            return true;
        }catch (IOException ex){
           ex.printStackTrace();
           return false;
       }finally {
           notifyMediaScannerConnection(context,file.getPath());
       }
   }


    /**
     * Make VideoStorageUtils a utility class by preventing instantiation.
     */
    private VideoStorageUtils() {
        throw new AssertionError();
    }
}
