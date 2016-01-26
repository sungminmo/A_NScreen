/*
 * (c)Copyright 2011 Widevine Technologies, Inc
 */

package com.widevine.sampleplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmErrorEvent;
import android.drm.DrmEvent;
import android.drm.DrmManagerClient;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;
import com.stvn.nscreen.util.CMLog;
import com.stvn.nscreen.util.CMUtil;

public class VideoPlayerView extends Activity {
    private final static String TAG = "VideoPlayerView";

    private final static float BUTTON_FONT_SIZE = 10;
    private final static String EXIT_FULLSCREEN = "Exit Full Screen";
    private final static String FULLSCREEN = "Enter Full Screen";
    private final static String PLAY = "Play";
    private final static int REFRESH = 1;

    private WidevineDrm drm;
    private FullScreenVideoView videoView;
    private MediaCodecView mediaCodecView;
    private String assetUri;
    private TextView logs;
    private ScrollView scrollView;
    private Context context;
    private ClipImageView bgImage;
    private Button mediaCodecModeButton;
    private Button playButton;
    private Button fullScreen;
    private Handler hRefresh;
    private View contentView;
    private LinearLayout main;
    private LinearLayout sidePanel;
    private boolean enteringFullScreen;
    private boolean useMediaCodec;
    private int width, height;

    private int currentSeek;

    private CMUtil.CMNetworkType isNetworkType;
    private BroadcastReceiver mWifiStateReceiver;
    @Override
    protected void onResume() {
        super.onResume();

        Log.d("VideoPlayerView", "onResume()");
        if (currentSeek > 0) {
            if (videoView != null) {
                if (videoView.isPlaying() == false) {
                    videoView.seekTo(currentSeek);
                    videoView.start();
                }
            }
            if (mediaCodecView != null) {
                if (mediaCodecView.isPlaying() == false) {
                    mediaCodecView.seekTo(currentSeek);
                    videoView.start();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        Log.v("VideoPlayerView", "------------------- onPause ----------------");
        super.onPause(); // swlim

        if (videoView != null) {
            if (videoView.isPlaying()) {
                videoView.pause();
                currentSeek = videoView.getCurrentPosition();
            }
        }
        if (mediaCodecView != null) {
            if (mediaCodecView.isPlaying()) {
                mediaCodecView.pause();
                currentSeek = mediaCodecView.getCurrentPosition();
            }
        }

    }

    @Override
    public void finish() {
        CMLog.d("wd", "finish");
        Intent intent = new Intent();
        intent.putExtra("currentpage",getIntent().getIntExtra("currentpage",0));
        setResult(Activity.RESULT_OK, intent);

        if (this.mWifiStateReceiver != null) {
            unregisterReceiver(this.mWifiStateReceiver);
        }
        super.finish();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        currentSeek = -1;
        height = display.getHeight();
        width = display.getWidth();
        context = this;
        useMediaCodec = false;
        contentView = createView();
        if (drm.isProvisionedDevice()) {
            setContentView(contentView);
        } else {
            setContentView(R.layout.widevine_sampleplayer_notprovisioned);
        }

        //drm.printPluginVersion();
        // swlim aaa
        //drm.acquireRights(assetUri);

        this.isNetworkType = CMUtil.isNetworkConnectedType(this);

        if (this.isNetworkType.compareTo(CMUtil.CMNetworkType.NotConnected) == 0) {
            CMAlertUtil.Alert(VideoPlayerView.this, "알림", "연결된 네트워크가 없습니다.", "", false, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }, true);
        }

        this.mWifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                CMUtil.CMNetworkType type = CMUtil.isNetworkConnectedType(VideoPlayerView.this);
                if (type.compareTo(CMUtil.CMNetworkType.NotConnected) == 0) {
                    unregisterReceiver(mWifiStateReceiver);
                    mWifiStateReceiver = null;
                    CMAlertUtil.Alert(VideoPlayerView.this, "알림", "연결된 네트워크가 없습니다.", "", false, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }, true);
                } else if (isNetworkType.compareTo(CMUtil.CMNetworkType.WifiConnected) == 0 && type.compareTo(CMUtil.CMNetworkType.AnotherConnected) == 0) {
                    unregisterReceiver(mWifiStateReceiver);
                    mWifiStateReceiver = null;
                    if (videoView != null) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        }
                    } else if (mediaCodecView != null) {
                        if (mediaCodecView.isPlaying()) {
                            mediaCodecView.pause();
                        }
                    }

                    CMAlertUtil.Alert(VideoPlayerView.this, "알림", "모바일 데이터(LTE, 3G)로 연결되었습니다.", "", false, false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }, true);
                } else {
                    isNetworkType = CMUtil.isNetworkConnectedType(VideoPlayerView.this);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.mWifiStateReceiver, intentFilter);

        try {
            Thread.sleep(100);
            startPlayback();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop.");
    }

    private View createView() {
        enteringFullScreen = false;
        //assetUri = this.getIntent().getStringExtra("com.widevine.demo.Path").replaceAll("wvplay", "http");


        String assetId = this.getIntent().getStringExtra("assetId");
        String contentUri = this.getIntent().getStringExtra("contentUri");
        String drmServerUri = this.getIntent().getStringExtra("drmServerUri");
        String drmProtection = this.getIntent().getStringExtra("drmProtection");
        String terminalKey = this.getIntent().getStringExtra("terminalKey");
        assetUri = contentUri;
        assetUri = contentUri.replaceAll("wvplay", "http");
        //assetUri = contentUri.replaceAll("widevine", "http");
        // widevine://cnm.video.toast.com/aaaaaa/b99fd60d-e0a1-465f-8641-b8276b3f1b8a.wvm

        drm = new WidevineDrm(this);

        // public static String USER_DATA = ",user_id:myjulyyi,content_id:M0431531LFO259395100|www.hchoice.co.kr,device_key:648a16b50911464aaf92801c4ea88b31,so_idx:10";
        WidevineDrm.Settings.DEVICE_ID = terminalKey;
        WidevineDrm.Settings.DRM_SERVER_URI = drmServerUri;
        //WidevineDrm.Settings.DEVICE_ID = terminalKey;
        WidevineDrm.Settings.USER_DATA = ",user_id:"+terminalKey+",content_id:"+assetId+",device_key:"+terminalKey+",so_idx:10";



        logMessage("title: " + this.getIntent().getStringExtra("title") + "\n");
        logMessage("Asset Uri: " + assetUri + "\n");
        logMessage("Drm Server: " + WidevineDrm.Settings.DRM_SERVER_URI + "\n");
        logMessage("Device Id: " + WidevineDrm.Settings.DEVICE_ID + "\n");
        logMessage("Portal Name: " + WidevineDrm.Settings.PORTAL_NAME + "\n");



        drm.setOnEventListener(new DrmManagerClient.OnEventListener() {
            public void onEvent(DrmManagerClient client, DrmEvent event) {
                logMessage("Drm Event : " + event);
                switch (event.getType()) {
                    case DrmEvent.TYPE_DRM_INFO_PROCESSED:
                        logMessage("Info Processed\n");
                        // 영상을 실행하는 코드를 넣습니다.
//                        startPlayback();
                        break;
                    case DrmEvent.TYPE_ALL_RIGHTS_REMOVED:
                        logMessage("All rights removed\n");
                        break;
                }
            }
        });
        drm.setOnErrorListener(new DrmManagerClient.OnErrorListener() {
            public void onError(DrmManagerClient client, DrmErrorEvent event) {
                logMessage("Drm Error : " + event.getType() + " / " + event.getMessage());
                switch (event.getType()) {
                    case DrmErrorEvent.TYPE_NO_INTERNET_CONNECTION:
                        logMessage("No Internet Connection\n");
                        break;
                    case DrmErrorEvent.TYPE_NOT_SUPPORTED:
                        logMessage("Not Supported\n");
                        break;
                    case DrmErrorEvent.TYPE_OUT_OF_MEMORY:
                        logMessage("Out of Memory\n");
                        break;
                    case DrmErrorEvent.TYPE_PROCESS_DRM_INFO_FAILED:
                        logMessage("Process DRM Info failed\n");
                        break;
                    case DrmErrorEvent.TYPE_REMOVE_ALL_RIGHTS_FAILED:
                        logMessage("Remove All Rights failed\n");
                        break;
                    case DrmErrorEvent.TYPE_RIGHTS_NOT_INSTALLED:
                        logMessage("Rights not installed\n");
                        break;
                    case DrmErrorEvent.TYPE_RIGHTS_RENEWAL_NOT_ALLOWED:
                        logMessage("Rights renewal not allowed\n");
                        break;
                }
                // 에러처리를 합니다.
            }
        });
        drm.acquireRights(assetUri);

        // Set log update listener
        WidevineDrm.WidevineDrmLogEventListener drmLogListener = new WidevineDrm.WidevineDrmLogEventListener() {
            public void logUpdated() {

                /*
                private final static long DEVICE_IS_PROVISIONED = 0;
                private final static long DEVICE_IS_NOT_PROVISIONED = 1;
                private final static long DEVICE_IS_PROVISIONED_SD_ONLY = 2;
                */
//                long lWVDrmInfoRequestStatusKey = drm.getWVDrmInfoRequestStatusKey();
//                if ( lWVDrmInfoRequestStatusKey == 1 ) {
//                    startPlayback();
//                }
                updateLogs();
            }
        };

        logs = new TextView(this);
        drm.setLogListener(drmLogListener);
        drm.registerPortal(WidevineDrm.Settings.PORTAL_NAME);

        scrollView = new ScrollView(this);
        scrollView.addView(logs);

        // Set message handler for log events
        hRefresh = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case REFRESH:
                    /* Refresh UI */
                    logs.setText(drm.logBuffer.toString());
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    //mStringBuffer.append(drm.logBuffer.toString());
                    //Log.d("player", mStringBuffer.toString());
                    //Log.d("player", drm.logBuffer.toString());

                    //drm.checkRightsStatus(contentUri)
                    //drm.acquireRights(assetUri);
                    //int drmRightsStatus = drm.checkRightsStatus(assetUri);
                    //Log.d("player", "updateLogs() drmRightsStatus:"+drmRightsStatus);


                    break;
                }
            }
        };

        updateLogs();

        sidePanel = new LinearLayout(this);
        sidePanel.setOrientation(LinearLayout.VERTICAL);

        sidePanel.addView(scrollView, new LinearLayout.LayoutParams((int)(width * 0.35), (int)(height * 0.5)));

        LinearLayout.LayoutParams paramsSidePanel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsSidePanel.gravity = Gravity.CENTER;
        sidePanel.addView(createButtons(), paramsSidePanel);

        FrameLayout playerFrame = new FrameLayout(this);

        View view;
        if (useMediaCodec) {
            mediaCodecView = new MediaCodecView(this);
            view = mediaCodecView;
        } else {
            videoView = new FullScreenVideoView(this);
            view = videoView;
        }

        playerFrame.addView(view, new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT));

        bgImage = new ClipImageView(this);
//        bgImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.widevine_sampleplayer_play_shield));

        bgImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click play (start playback).");
                startPlayback();

            }
        });

        fullScreen = new Button(this);
        fullScreen.setText(FULLSCREEN);

        fullScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click full screen");
                int currentPosition = videoView.getCurrentPosition();
                videoView.setVisibility(View.INVISIBLE);
                if (fullScreen.getText().equals(FULLSCREEN)) {

                    videoView.setFullScreen(true);
                    fullScreen.setText(EXIT_FULLSCREEN);
                    enteringFullScreen = true;
                } else {
                    videoView.setFullScreen(false);
                    fullScreen.setText(FULLSCREEN);
                }
                videoView.setVisibility(View.VISIBLE);

                stopPlayback();
                startPlayback();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                videoView.seekTo(currentPosition);
            }
        });
        //playerFrame.addView(fullScreen, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        //fullScreen.setVisibility(View.INVISIBLE);
        //playerFrame.addView(bgImage, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        main = new LinearLayout(this);
//        main.addView(playerFrame, new LinearLayout.LayoutParams((int) (width * 0.65), LinearLayout.LayoutParams.FILL_PARENT, 1));
        main.addView(playerFrame, new LinearLayout.LayoutParams((int) (width), LinearLayout.LayoutParams.FILL_PARENT, 1));
//        main.addView(sidePanel, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT, 3));

//        sidePanel.setVisibility(View.GONE);


        // swlim aaa
        //drm.acquireRights(contentUri);
        //enteringFullScreen = true;
        //startPlayback();
        drm.setVideoPlayerView(this);


        return main;
    }



    private void startPlayback() {
        logMessage("Playback start.");
        playButton.setText("Stop");
        bgImage.setVisibility(View.GONE);

        if (useMediaCodec) {
            mediaCodecView.setDataSource(this, Uri.parse(assetUri), null /* headers */, true /* encrypted */);
            mediaCodecView.setMediaController(new MediaController(context));
            mediaCodecView.requestFocus();
            mediaCodecView.start();
        } else {
            videoView.setVideoPath(assetUri);
            videoView.setMediaController(new MediaController(context));
            videoView.setOnErrorListener(new OnErrorListener() {
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    String message = "Unknown error: " + what;
                    switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        message = "Unable to play media";
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        message = "Server failed";
                        break;
                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        message = "Invalid media";
                        break;
                    }
                    logMessage(message + "\n");

                    updateLogs();
                    bgImage.setVisibility(View.VISIBLE);
                    return false;
                }
            });

            videoView.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion.");
                    stopPlayback();
                }
            });

            videoView.setOnInfoListener(new OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    String message = "Unknown info message";
                    switch (what) {
                    case MediaPlayer.MEDIA_INFO_UNKNOWN:
                        message = "Unknown info message 2";
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        message = "Video rendering start";
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        message = "Video track lagging";
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        message = "Buffering start";
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        message = "Buffering end";
                        break;
                    /*** TODO: Below needs to be added to MediaPlayer.java. Hard coded for now --Zan
                    case MediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH: ***/
                    case 703:
                        message = "Network bandwidth";
                        break;
                    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        message = "Bad interleaving";
                        break;
                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        message = "Not seekable";
                        break;
                    case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        message = "Metadata update";
                        break;
                    }
                    logMessage(message + "\n");

                    updateLogs();

                    return true;
                }
            });

            videoView.requestFocus();

            videoView.start();

            if (videoView.getFullScreen()) {
                sidePanel.setVisibility(View.GONE);
            } else {
                sidePanel.setVisibility(View.VISIBLE);
            }

            fullScreen.setVisibility(View.VISIBLE);
            videoView.setFullScreenDimensions(contentView.getRight() - contentView.getLeft(), contentView.getBottom() - contentView.getTop());
        }
    }

    private void stopPlayback() {
        // 플레이 완료 후 플레이어 종료 처리
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 300);

        currentSeek = -1;
        logMessage("Stop Playback.");
        playButton.setText("Play");
        bgImage.setVisibility(View.VISIBLE);

        if (useMediaCodec) {
            mediaCodecView.reset();
        } else {
            videoView.stopPlayback();

            fullScreen.setVisibility(View.INVISIBLE);
            if (videoView.getFullScreen() && !enteringFullScreen) {
                videoView.setVisibility(View.INVISIBLE);
                videoView.setFullScreen(false);
                videoView.setVisibility(View.VISIBLE);
                sidePanel.setVisibility(View.VISIBLE);
                fullScreen.setText(FULLSCREEN);
            }
        }
        enteringFullScreen = false;
    }

    private View createButtons() {
        mediaCodecModeButton = new Button(this);
        if (useMediaCodec) {
            mediaCodecModeButton.setText("normal_mode");
        } else {
            mediaCodecModeButton.setText("mediacodec_mode");
        }
        mediaCodecModeButton.setTextSize(BUTTON_FONT_SIZE);

        mediaCodecModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStop();

                useMediaCodec = (useMediaCodec) ? false : true;
                Log.d(TAG, "Click media codec mode.  useMediaCodec = "+useMediaCodec);
                contentView = createView();
                if (drm.isProvisionedDevice()) {
                    setContentView(contentView);
                } else {
                    setContentView(R.layout.widevine_sampleplayer_notprovisioned);
                }
            }
        });

        playButton = new Button(this);
        playButton.setText("Play");
        playButton.setTextSize(BUTTON_FONT_SIZE);

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click play");
                Button b = (Button) v;
                if (b.getText().equals(PLAY)) {
                    startPlayback();
                } else {
                    stopPlayback();
                }
            }
        });

        Button rightsButton = new Button(this);
        rightsButton.setText("acquire_rights");
        rightsButton.setTextSize(BUTTON_FONT_SIZE);

        rightsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click rights");
                drm.acquireRights(assetUri);
                updateLogs();
            }
        });

        Button removeButton = new Button(this);
        removeButton.setText("remove_rights");
        removeButton.setTextSize(BUTTON_FONT_SIZE);

        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click remove rights");
                drm.removeRights(assetUri);
                updateLogs();
            }
        });

        Button checkButton = new Button(this);
        checkButton.setText("show_rights");
        checkButton.setTextSize(BUTTON_FONT_SIZE);

        checkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click check rights");
                drm.showRights(assetUri);
                updateLogs();
            }
        });

        Button checkConstraints = new Button(this);
        checkConstraints.setText("constraints");
        checkConstraints.setTextSize(BUTTON_FONT_SIZE);

        checkConstraints.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Click get constraints");
                drm.getConstraints(assetUri);
                updateLogs();

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        params.setMargins(0, 0, 0, 5);
        LinearLayout buttonsLeft = new LinearLayout(this);

        buttonsLeft.setOrientation(LinearLayout.VERTICAL);
        buttonsLeft.addView(playButton, params);
        buttonsLeft.addView(rightsButton, params);
        buttonsLeft.addView(checkConstraints, params);

        LinearLayout buttonsRight = new LinearLayout(this);
        buttonsRight.addView(mediaCodecModeButton, params);
        buttonsRight.setOrientation(LinearLayout.VERTICAL);
        buttonsRight.addView(checkButton, params);
        buttonsRight.addView(removeButton, params);

        LinearLayout.LayoutParams paramsSides = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        paramsSides.gravity = Gravity.BOTTOM;

        LinearLayout buttons = new LinearLayout(this);
        buttons.addView(buttonsLeft, paramsSides);
        buttons.addView(buttonsRight, paramsSides);

        return buttons;
    }

    private void updateLogs() {
        //hRefresh.sendEmptyMessage(REFRESH);
    }



    private void logMessage(String message) {
        Log.d(TAG, message);
        drm.logBuffer.append(message);
    }

}
