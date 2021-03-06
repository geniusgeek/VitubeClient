package com.example.genius.vitubeclient.utils;

/**
 * Created by Genius on 7/20/2015.
 */
public class Constants {
    private static final String IP_ADDRESS = "192.168.173.1";
    private static final String PORT=":8080";
    public static final String WEB_ENDPOINT = "http://" + IP_ADDRESS+PORT;
    public static final String VIDEO_POINT = WEB_ENDPOINT+"/video";

    /**
     * Define a constant for 1 MB.
     */
    public static final long MEGA_BYTE = 1024 * 1024;

    /**
     * Maximum size of Video to be uploaded in MB.
     */
    public static final long MAX_SIZE_MEGA_BYTE = 50 * MEGA_BYTE;

}
