package com.evontech.VideoPlugin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.opentok.android.OpentokError;
import com.opentok.android.Stream;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.cordova.hellocordova.MainActivity;
import io.cordova.hellocordova.R;


/**
 * Created by amit rai on 3/7/2016.
 */
public class VideoPlugin extends CordovaPlugin implements SessionListeners, ActivityListener, View.OnClickListener {
    public static final String ACTION_INIT_CALL = "initializeVideoCalling";
    public static final String ACTION_ENDCALL = "endCalling";
    public static final String ACTION_BALANCE_WARNING = "showLowBalanceWarning";
    public static final String ACTION_GETUSERBALANCE = "getUserBalance";
    public static final String ACTION_APIRESPONSE = "receivedResponseFromAPI";
    public static final String ACTION_TIPRECEIVED = "tipReceived";
    private static final String TAG = CordovaPlugin.class.getSimpleName();
    private static MySession mSession;
    private ImageView mVideoCallBtn, mMicBtn, mDisconnectBtn, mSwipeBtn;
    private View mCallView, mNoneView;
    private ViewGroup mViewGroup;
    private LinearLayout mParentProgressDialog;
    private Chrono mTimerTxt;
    private RelativeLayout mPricePopUp;
    private String mCallPerMinute, mUserBalance, mProfileImageUrl;
    private RelativeLayout mCallingViewParent;
    private boolean isCallingViewVisible = true;
    private Handler handler = new Handler();
    private Runnable callRunnable;
    private CallbackContext mCallBackContext;
    private long mCallTime;
    private boolean resumeHasRun = false;
    private ImageView mProfilePicConnecting;
    private ImageView mImageNonView;
    private JSONArray mJsonData;
    private boolean CALL_DISCONNECT = false;
//    private com.listeners.SessionListeners sessionListeners = null;

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private String MISSED_CALL = null;
    private boolean prev_command =false;
    private boolean call_initialized = false;
    private boolean mCaller = false;
    private boolean mDisconnect = false;
    private boolean mMissedCall = false;

    private CallBean callBean = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {



        if (action != null && !action.isEmpty()){

            if(action.equalsIgnoreCase(ACTION_INIT_CALL))
                initVideoCall(action, args, callbackContext);

            if(action.equalsIgnoreCase(ACTION_ENDCALL)){
                String object = args.getString(0);
                mMissedCall = true;
                endCall(object);
            }

            if(action.equalsIgnoreCase(ACTION_APIRESPONSE))
                initVideoCall(action, args, callbackContext);

            if(action.equalsIgnoreCase(ACTION_BALANCE_WARNING))
                initVideoCall(action, args, callbackContext);

            if(action.equalsIgnoreCase(ACTION_TIPRECEIVED))
                initVideoCall(action, args, callbackContext);

            if(action.equalsIgnoreCase(ACTION_GETUSERBALANCE))
                initVideoCall(action, args, callbackContext);

        }
        return false;
    }

    /**
     * initiates video calling
     * @param callBean
     */
    private void initCall(CallBean callBean) {
        try {
            mDisconnect = false;
            call_initialized = true;
            CALL_DISCONNECT = false;
            String apiKey = callBean.getApiKey();//.getString("apiKey");
            String sessonId = callBean.getSessionId();//object.getString("sessonId");
            String sessonToken = callBean.getSessionId();//object.getString("sessonToken");
            mCallPerMinute = callBean.getCallPerMinute();//object.getString("callPerMinute");
            mUserBalance = "12121";//object.getString("userBalance");
            mProfileImageUrl = callBean.getProfileImage();//object.getString("profileImageUrl");

//            String apiKey = args.get(0).toString();//.getString("apiKey");
//            String sessonId = args.get(1).toString();//object.getString("sessonId");
//            String sessonToken = args.get(2).toString();//object.getString("sessonToken");
//            mCallPerMinute = args.get(3).toString();//object.getString("callPerMinute");
//            mUserBalance = args.get(4).toString();//object.getString("userBalance");
//            mProfileImageUrl = args.get(5).toString();//object.getString("profileImageUrl");



//            mCallPerMinute = "0";
//            mUserBalance = "100";
//            mProfileImageUrl = "http://i2.wp.com/allindiaroundup.com/wp-content/uploads/2015/01/mr-bean-as-a-serial-killer-whatsapp-dp4.jpg";

//        String sessonId = OpenTokConfig.SESSION_ID;
//        String apiKey = OpenTokConfig.API_KEY;
//        String sessonToken = OpenTokConfig.TOKEN;



            if(mCallPerMinute != null && mCallPerMinute.equals("0")){
                mCaller = false;
                JSONObject json = getJson(Constants.RECEIVER_INITIALIZED, SUCCESS);
                mCallBackContext.successMessage(json);
                mSession = new MySession(cordova.getActivity(), this, apiKey, sessonId, false);
            }
            else{
                mCaller = true;
                JSONObject json = getJson(Constants.INITIALIZATION_COMPLETE, SUCCESS);
                mCallBackContext.successMessage(json);
                mSession = new MySession(cordova.getActivity(), this, apiKey, sessonId, true);
            }

            addView();
            mSession.connect(sessonToken);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addView() {
        mViewGroup = (ViewGroup) webView.getView();

        LayoutInflater inflator = LayoutInflater.from(cordova.getActivity());
        mCallView = inflator.inflate(R.layout.room, null);
        setMargins(mCallView, 0, 500,0,0);
//        View nullView = inflator.inflate(R.layout.view_none_video,null);

//        mSession.setNullView(nullView);

        ViewGroup preview = (ViewGroup) mCallView.findViewById(R.id.preview);  // User View
        mSession.setPreviewView(preview);

        RelativeLayout playersView = (RelativeLayout) mCallView.findViewById(R.id.pager);  // Subscriber View
        mSession.setPlayersViewContainer(playersView);

        // Progress bar Views
        mParentProgressDialog = (LinearLayout) mCallView.findViewById(R.id.ll_parent_connecting);
        mProfilePicConnecting = (ImageView) mCallView.findViewById(R.id.iv_connecting_img);
        TextView price = (TextView) mCallView.findViewById(R.id.tv_dialog_price);
        price.setText("Once connected this video chat \n will be billed at " + mCallPerMinute + " per min.");

        //        creator.into(mProfilePicConnecting);
        ProgressBar progressbar = (ProgressBar) mCallView.findViewById(R.id.pb_connecting);
        progressbar.getIndeterminateDrawable().setColorFilter(cordova.getActivity().getResources().getColor(android.R.color.holo_green_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        mNoneView = mCallView.findViewById(R.id.non_view);
        mImageNonView = (ImageView) mCallView.findViewById(R.id.iv_no_view_img);
//        creator.into(mImageNonView);


        mPricePopUp = (RelativeLayout) mCallView.findViewById(R.id.cv_connecting_price_dialog);

        if (mCallPerMinute.equalsIgnoreCase("0")) {
            mPricePopUp.setVisibility(View.GONE);
        }

        /*Calling Views*/
        mCallingViewParent = (RelativeLayout) mCallView.findViewById(R.id.rl_calling_view);
        mVideoCallBtn = (ImageView) mCallView.findViewById(R.id.iv_video_call);
        mVideoCallBtn.setEnabled(false);
        mMicBtn = (ImageView) mCallView.findViewById(R.id.iv_audio_call);
        mMicBtn.setEnabled(false);
        mDisconnectBtn = (ImageView) mCallView.findViewById(R.id.iv_end_call);
        mSwipeBtn = (ImageView) mCallView.findViewById(R.id.iv_swipe_camera);
        mSwipeBtn.setEnabled(false);
        mTimerTxt = (Chrono) mCallView.findViewById(R.id.cm_timer);
        mVideoCallBtn.setOnClickListener(this);
        mMicBtn.setOnClickListener(this);
        mDisconnectBtn.setOnClickListener(this);
        mSwipeBtn.setOnClickListener(this);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mViewGroup.addView(mCallView);
                RequestCreator creator = Picasso.with(cordova.getActivity()).load(mProfileImageUrl);
                creator.into(mProfilePicConnecting);
                creator.into(mImageNonView);
                mTimerTxt.setActivity(cordova.getActivity());

                mTimerTxt.setOnChronometerTickListener(new Chrono.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chrono chronometer) {
//                        mTimerTxt.setText(chronometer.getText());
                    }
                });
            }
        };
        cordova.getActivity().runOnUiThread(runnable);


    }

    @Override
    public void onStreamDrop(Stream stream) {

        disconnectCall();
    }

    @Override
    public void onVideoViewChange(boolean hasVideo) {
//        mCallBackContext.success("Video View Change");
        if (hasVideo) {
            mNoneView.setVisibility(View.INVISIBLE);
        } else {
            mNoneView.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void onPublisherCreate() {
//        mCallBackContext.success("init complete ");
//    }

    @Override
    public void onCallConnected() {
        JSONObject json = getJson(Constants.CONNECTION_CREATED, SUCCESS);
        mCallBackContext.successMessage(json);

        if(mCallPerMinute != null && mCallPerMinute.equals("0")){
            JSONObject json_callstarted = getJson(Constants.CALL_STARTED, SUCCESS);
            mCallBackContext.successMessage(json_callstarted);

            mParentProgressDialog.setVisibility(View.GONE);
            mVideoCallBtn.setEnabled(true);
            mMicBtn.setEnabled(true);
            mDisconnectBtn.setEnabled(true);
            mSwipeBtn.setEnabled(true);
            mSwipeBtn.setVisibility(View.VISIBLE);
            mCallView.setEnabled(true);
            mCallView.setOnClickListener(this);

            mTimerTxt.setBase(SystemClock.elapsedRealtime());
            mTimerTxt.start();

            visibleCallingViews();
            callThread();
        }
    }

    private void visibleCallingViews() {
//        mCallingViewParent.setVisibility(View.VISIBLE);
        SlideToAbove();
        SlideToLeft();
        mSwipeBtn.setVisibility(View.VISIBLE);
        isCallingViewVisible = true;
        callThread();
    }

    private void invisibleCallingViews() {
        SlideToDown();
        SlideToRight();

        isCallingViewVisible = false;
    }

    private void callThread() {
        callRunnable = new Runnable() {
            @Override
            public void run() {

                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invisibleCallingViews();
                    }
                });

            }
        };
        handler.postDelayed(callRunnable, 3000);
    }

    @Override
    public void onCallDisconnected() {
        disconnectCall();
        JSONObject json = null;
        if(mMissedCall)
            return;

        if(mCaller && !MySession.CALL_STARTED)
            json = getJson(Constants.CALL_ENDED_BY_RECEIVER, SUCCESS);
        else
            json = getJson(Constants.CALL_END, SUCCESS);

        mCallBackContext.successMessage(json);
    }

    @Override
    public void onCallRejected() {
        JSONObject json = getJson(Constants.CALL_ENDED_BY_RECEIVER, SUCCESS);
        mCallBackContext.successMessage(json);
    }

    @Override
    public void onCallStarted() {
        JSONObject json = getJson(Constants.CALL_STARTED, SUCCESS);
        mCallBackContext.successMessage(json);

        if(mCallPerMinute != null && !mCallPerMinute.equals("0")){

//            mCallBackContext.success(Constants.CALL_STARTED);
            mParentProgressDialog.setVisibility(View.GONE);
            mVideoCallBtn.setEnabled(true);
            mMicBtn.setEnabled(true);
            mDisconnectBtn.setEnabled(true);
            mSwipeBtn.setEnabled(true);
            mSwipeBtn.setVisibility(View.VISIBLE);
            mCallView.setEnabled(true);
            mCallView.setOnClickListener(this);

            mTimerTxt.setBase(SystemClock.elapsedRealtime());
            mTimerTxt.start();

            visibleCallingViews();
            callThread();
        }
    }

    @Override
    public void onCallEndBeforeConnect() {
        JSONObject json = null;

        if(mMissedCall)
            return;

        if(mDisconnect)
            json = getJson(Constants.CALL_ENDED_BY_RECEIVER, SUCCESS);
        else
            json = getJson(Constants.CALL_END, SUCCESS);

        mCallBackContext.successMessage(json);
    }

    @Override
    public void onCallEndByReceiver() {
        JSONObject json = getJson(Constants.CALL_END, SUCCESS);
        mCallBackContext.successMessage(json);
    }

    @Override
    public void onError(OpentokError error) {
        if(error != null){
            JSONObject json = getJson(error.toString(), ERROR);
            mCallBackContext.successMessage(json);
        }
    }

    @Override
    public void onCallEnded() {
        JSONObject json = getJson(Constants.CALL_END, SUCCESS);
        mCallBackContext.successMessage(json);
    }

    @Override
    public void onReciverInitialized() {
        JSONObject json = getJson(Constants.RECEIVER_INITIALIZED, SUCCESS);
        mCallBackContext.successMessage(json);
    }

    @Override
    public void onCallerInitialized() {
        JSONObject json = getJson(Constants.INITIALIZATION_COMPLETE, SUCCESS);
        mCallBackContext.successMessage(json);
    }

    @Override
    public void onReceiverInitialized() {
        JSONObject json = getJson(Constants.RECEIVER_INITIALIZED, SUCCESS);
        mCallBackContext.successMessage(json);
    }

    private void disconnectCall() {
        try {

            if (CALL_DISCONNECT)
                return;

            CALL_DISCONNECT = true;

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewGroup.removeView(mCallView);
                }
            });

            ((MainActivity) cordova.getActivity()).setActivityListener(null);
            if(MySession.CALL_CONNECTED){
//            JSONObject json = getJson(Constants.CALL_END, SUCCESS);
//            mCallBackContext.successMessage(json);
            }
            if (mSession.getSubscriber() != null) {
//            JSONObject json = getJson(Constants.CALL_END, SUCCESS);
//            mCallBackContext.successMessage(json);
            } else if(mCallPerMinute != null && mCallPerMinute.equals("0")){
                JSONObject json = getJson(Constants.CALL_ENDED_BY_RECEIVER, SUCCESS);
                mCallBackContext.successMessage(json);
            }

            else {
                JSONObject json = getJson(Constants.DISCONNECT_SUCCESS, SUCCESS);
                mCallBackContext.successMessage(json);
            }

            mSession.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_call:
                muteAudio();
                break;
            case R.id.iv_video_call:
                hideCam();
                break;
            case R.id.iv_end_call:
                mDisconnect = true;
                disconnectCall();
                break;

            case R.id.iv_swipe_camera:
                mSession.swipeCamera();
                break;
            case R.id.mainlayout:
                if (isCallingViewVisible) {
                    invisibleCallingViews();
                    handler.removeCallbacks(callRunnable);
                } else {
                    visibleCallingViews();
                }
                break;
        }
    }

    /**
     * Hide/Show Video Camera of Subscriber
     */
    public void hideCam() {
        mSession.hideVideo();
        if (mSession.isCameraOn()) {
            mVideoCallBtn.setImageResource(R.drawable.camera);

        } else {
            mVideoCallBtn.setImageResource(R.drawable.camera_no);
        }
    }

    /**
     * Enable/Disable Mic.
     */
    public void muteAudio() {
        mSession.muteMic();
        if (mSession.isMicMuted()) {
            mMicBtn.setImageResource(R.drawable.mic_no);
        } else {
            mMicBtn.setImageResource(R.drawable.mic);
        }
    }


    public void SlideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                10f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        mCallingViewParent.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mCallingViewParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

        });

    }

    public void SlideToDown() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        mCallingViewParent.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCallingViewParent.setVisibility(View.GONE);
            }

        });

    }

    public void SlideToLeft() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 10.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        mSwipeBtn.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mSwipeBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

        });

    }

    public void SlideToRight() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 5.2f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(600);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        mSwipeBtn.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mSwipeBtn.setVisibility(View.GONE);
//                mCallingViewParent.clearAnimation();
//
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                        mCallingViewParent.getWidth(), mCallingViewParent.getHeight());
//                lp.setMargins(0, mCallingViewParent.getWidth(), 0, 0);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                mCallingViewParent.setLayoutParams(lp);

            }

        });

    }

    @Override
    public void onPauseActivity() {
        if (mSession != null) {
            mSession.onPause();
        }
    }

    @Override
    public void onResumeActivity() {
        if (!resumeHasRun) {
            resumeHasRun = true;
            return;
        } else {
            if (mSession != null) {
                mSession.onResume();
            }
        }
    }

    @Override
    public void onStoppedActivity() {
//        if (mSession != null) {
//            mSession.disconnect();
//        }
    }

    @Override
    public void onDestroyActivity() {
        if (mSession != null) {
            mSession.disconnect();
        }
    }

    @Override
    public void onRequestAccessed() {
//        initCall(mJsonData);
        Log.e(TAG, "y m i commented");
    }

    /**
     * returns json for the message
     * @param message
     * @param message_type
     * @return
     */
    private JSONObject getJson(String message, String message_type){

        JSONObject jsonObj =null;
        try {
            if(message == null || message.equalsIgnoreCase(" ")){
                return new JSONObject("{\"data\":\" \",\"status\":\"success\"}");
            }else if(message.isEmpty()){
                return new JSONObject("{\"data\":\" \",\"status\":\"success\"}");
            }

            if(message_type.equals(SUCCESS)){
                if(message.equals("Initialization completed !!"))
                    jsonObj = new JSONObject("{\"data\":\"Initialization completed !!\",\"status\":\"success\"}");
                else if(message.equals("Successfully disconnected !!"))
                    jsonObj = new JSONObject("{\"data\":\"Successfully disconnected !!\",\"status\":\"success\"}");
                else
                    jsonObj = new JSONObject("{\"data\":"+message+",\"status\":\"success\"}");

                return jsonObj;

            }else if(message_type.equals(ERROR)){
                jsonObj = new JSONObject("{\"data\":\"{\\\"networkType\\\":\\\"unknown\\\",\\\"error\\\":\\\"+message+\\\"}\",\"status\":\"failure\"}");
                return jsonObj;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * sets margin to a view
     * @param v
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * this method returns the call back as it is and removes the calling view.
     */
    private void endCall(String message){
        try {

            if(!call_initialized)
                return;

            if(prev_command)
                return;

            prev_command = true;


            if (mSession != null)
                mSession.disconnect();

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewGroup.removeView(mCallView);
                }
            });


            if(message != null && message.equalsIgnoreCase("missedCall")){
                JSONObject json = getJson(message, SUCCESS);
                mCallBackContext.successMessage(json);
            }else if(message == null || message .equalsIgnoreCase("null")){
                JSONObject json = getJson(null, SUCCESS);
                mCallBackContext.successMessage(json);
            }else {
                JSONObject json = getJson(message, SUCCESS);
                mCallBackContext.successMessage(json);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * initialize video call
     */
    private boolean initVideoCall(String action, JSONArray args, CallbackContext callbackContext){
        try {
            prev_command =false;
            mMissedCall = false;

            Log.e(TAG, ""+args);

            Gson gson = new Gson();
            callBean = gson.fromJson(args.get(0).toString(), CallBean.class);
            Log.e(TAG, ""+callBean);
//            initCall(callBean);


            if (ACTION_INIT_CALL.equals(action)) {
                mCallBackContext = callbackContext;
                ((MainActivity) cordova.getActivity()).setActivityListener(this);
                mJsonData = args;

                int permissionCheck = ContextCompat.checkSelfPermission(cordova.getActivity(),
                        Manifest.permission.CAMERA);

                int permissionCheckAudio = ContextCompat.checkSelfPermission(cordova.getActivity(),
                        Manifest.permission.RECORD_AUDIO);

                int permissionCheckModifyAudio = ContextCompat.checkSelfPermission(cordova.getActivity(),
                        Manifest.permission.MODIFY_AUDIO_SETTINGS);


                if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheckAudio == PackageManager.PERMISSION_GRANTED && permissionCheckModifyAudio == PackageManager.PERMISSION_GRANTED) {
                    cordova.getThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            initCall(callBean);
                        }
                    });

                } else {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(cordova.getActivity(),
                            Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(cordova.getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                100);
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(cordova.getActivity(),
                            Manifest.permission.RECORD_AUDIO)) {
                        ActivityCompat.requestPermissions(cordova.getActivity(),
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                100);
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(cordova.getActivity(),
                            Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                        ActivityCompat.requestPermissions(cordova.getActivity(),
                                new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                                100);
                    } else {
                        ActivityCompat.requestPermissions(cordova.getActivity(),
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                                100);
                    }
                }
                return true;
            } else if(action!=null && action.equalsIgnoreCase("missedCall")){
                mMissedCall = true;
                endCall(action);
            }else if (ACTION_ENDCALL.equalsIgnoreCase(action)) {
                if(args != null){
                    String object = args.getString(0);
                    mMissedCall = true;
                    endCall(object);
                }else
                    disconnectCall();
            }else if(action == null || action == "null"){
                endCall(null);
            }else
                endCall(action);
            callbackContext.error("Invalid action");
            return false;
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }
    }
}
