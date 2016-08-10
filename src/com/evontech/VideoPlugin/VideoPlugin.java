package com.evontech.VideoPlugin;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
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
    private CardView mPricePopUp;
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

    // new view changes
    private RelativeLayout layout_tip_send_receive = null;
    private RelativeLayout layout_low_credit = null;
    private RelativeLayout layout_tip = null;
//    private ImageView img_add_credit = null;
    private RelativeLayout layout_plus_credit = null;
    private ProgressDialog dialogWait = null;

//    private RelativeLayout layout_send_tip = null;

    // tip dialog
    private RelativeLayout layout_close = null;
    private LinearLayout layout_addmore = null;
    private Button btn_sendtip = null;
    private Button btn_ten_dollar = null;
    private Button btn_twenty_dollar = null;
    private Button btn_fourty_dollar = null;
    private Button btn_sixty_dollar = null;
    private EditText edt_tipamount = null;
    private LinearLayout layout_progress_tip = null;
    private RelativeLayout layout_others = null;

    //add credit
    private RelativeLayout layout_close_add_credit = null;
    private Button btn_buy_ten = null;
    private Button btn_buy_twetnty = null;
    private Button btn_buy_fourty = null;
    private Button btn_buy_sixty = null;
    private LinearLayout layout_progress = null;
    private LinearLayout layout_credit_btns = null;



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

            if(action.equalsIgnoreCase(ACTION_APIRESPONSE)){
                initVideoCall(action, args, callbackContext);

            }

            if(action.equalsIgnoreCase(ACTION_BALANCE_WARNING)){
                showLowBalanceWarning();

            }

            if(action.equalsIgnoreCase(ACTION_TIPRECEIVED)){
                initVideoCall(action, args, callbackContext);

            }

            if(action.equalsIgnoreCase(ACTION_GETUSERBALANCE)){
                initVideoCall(action, args, callbackContext);

            }

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
            String sessonToken = callBean.getToken();//object.getString("sessonToken");
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



            if(callBean != null){
                mCaller = false;
                JSONObject json = getJson(Constants.INIT_COMPLETE, SUCCESS);
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

        layout_tip_send_receive = (RelativeLayout) mCallView.findViewById(R.id.layout_tip_send_receive);
        layout_tip = (RelativeLayout) mCallView.findViewById(R.id.layout_tip);
        layout_low_credit = (RelativeLayout) mCallView.findViewById(R.id.layout_low_credit);
        layout_plus_credit = (RelativeLayout) mCallView.findViewById(R.id.layout_plus_credit);
//        layout_send_tip = (RelativeLayout) mCallView.findViewById(R.id.layout_send_tip);


        setMargins(mCallView, 0, 500,0,0);

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


        mPricePopUp = (CardView) mCallView.findViewById(R.id.cv_connecting_price_dialog);

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
        layout_plus_credit.setOnClickListener(this);


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

        if(mCallPerMinute != null ){

            layout_tip_send_receive.setVisibility(View.VISIBLE);
            layout_tip.setVisibility(View.VISIBLE);
//            layout_low_credit.setVisibility(View.VISIBLE);

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

            //aaabb/123456 susie/123456
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
                JSONObject endcalljson = getJson(Constants.CALL_END_BEFORE_CONNECT, SUCCESS);

                mCallBackContext.successMessage(json);
                mCallBackContext.successMessage(endcalljson);
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
            case R.id.layout_plus_credit:
                showSelectTipAmount();
                break;
            case R.id.layout_close:
                closeCreditDialog();
                break;
            case R.id.layout_addmore:
                addMoreCredit();
                break;

            case R.id.btn_sendtip:
                sendTip(null, true);
                break;

            case R.id.btn_ten_dollar:
                sendTip(Constants.TEN_DOLLARS, true);
                break;

            case R.id.btn_twenty_dollar:
                sendTip(Constants.TWENTY_DOLLARS, true);
                break;

            case R.id.btn_fourty_dollar:
                sendTip(Constants.FOURTY_DOLLARS, true);
                break;

            case R.id.btn_sixty_dollar:
                sendTip(Constants.SIXTY_DOLLARS, true);
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

    /**
     * slides upward
     */
    private void slideToUp(){
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

    private void slideToLeft(){

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

            dialogWait = new ProgressDialog(cordova.getActivity());
            dialogWait.setMessage(cordova.getActivity().getResources().getString(R.string.wait_message));

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

                initCall(callBean);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheckAudio == PackageManager.PERMISSION_GRANTED && permissionCheckModifyAudio == PackageManager.PERMISSION_GRANTED) {
//                    cordova.getThreadPool().execute(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });

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

    /**
     * shows low balance warning
     */
    private void showLowBalanceWarning(){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    layout_low_credit.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * shows credit dialog to send
     * money from one dialog to another
     */
    private void showSelectTipAmount(){
        try{
            layout_low_credit.setVisibility(View.INVISIBLE);
            layout_tip_send_receive.setVisibility(View.INVISIBLE);
//            layout_send_tip.setVisibility(View.VISIBLE);

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // custom dialog
                    // custom dialog
                    final Dialog dialog = new Dialog(cordova.getActivity());
                    dialog.setContentView(R.layout.send_tip);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    cordova.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;



                    layout_close =  (RelativeLayout) dialog.findViewById(R.id.layout_close);

                    layout_addmore =  (LinearLayout) dialog.findViewById(R.id.layout_addmore);
                    btn_sendtip =  (Button) dialog.findViewById(R.id.btn_sendtip);
                    btn_ten_dollar =  (Button) dialog.findViewById(R.id.btn_ten_dollar);
                    btn_twenty_dollar =  (Button) dialog.findViewById(R.id.btn_twenty_dollar);
                    btn_fourty_dollar =  (Button) dialog.findViewById(R.id.btn_fourty_dollar);
                    btn_sixty_dollar =  (Button) dialog.findViewById(R.id.btn_sixty_dollar);
                    edt_tipamount =  (EditText) dialog.findViewById(R.id.edt_tipamount);
                    layout_progress_tip =  (LinearLayout) dialog.findViewById(R.id.layout_progress_tip);
                    layout_others =  (RelativeLayout) dialog.findViewById(R.id.layout_others);

                    layout_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            closeCreditDialog();
                        }
                    });
                    layout_addmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            closeCreditDialog();
                            showAddAmountDialog();
                        }
                    });
                    btn_sendtip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_progress_tip.setVisibility(View.VISIBLE);
                            layout_others.setVisibility(View.GONE);
                            closeCreditDialog();
                        }
                    });
                    btn_ten_dollar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_progress_tip.setVisibility(View.VISIBLE);
                            layout_others.setVisibility(View.GONE);
                            closeCreditDialog();
                        }
                    });
                    btn_twenty_dollar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_progress_tip.setVisibility(View.VISIBLE);
                            layout_others.setVisibility(View.GONE);
                            closeCreditDialog();
                        }
                    });
                    btn_fourty_dollar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            closeCreditDialog();
                        }
                    });
                    btn_sixty_dollar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_progress_tip.setVisibility(View.VISIBLE);
                            layout_others.setVisibility(View.GONE);
                            closeCreditDialog();
                        }
                    });


                    lp.width = (int)(width * 0.8);
                    lp.height = (int)(height * 0.8);
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * closes the credit dialog
     */
    private void closeCreditDialog(){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    layout_low_credit.setVisibility(View.VISIBLE);
                    layout_tip_send_receive.setVisibility(View.VISIBLE);
//                    layout_send_tip.setVisibility(View.INVISIBLE);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * add more credit
     */
    private void addMoreCredit(){

    }


    /**
     * sennds amount to different users
     * @param amount to be sent to other users
     * @param isCustom if amount is custom or fixed
     */
    private void sendTip(String amount, boolean isCustom){
        try {
            if(isCustom){
                if(!edt_tipamount.getText().toString().isEmpty()){
                    Log.e(TAG, ""+edt_tipamount.getText().toString());
                }
            }else {

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * shows amount to be added
     */
    private void showAddAmountDialog(){
        {
            try{
                layout_low_credit.setVisibility(View.INVISIBLE);
                layout_tip_send_receive.setVisibility(View.INVISIBLE);
//            layout_send_tip.setVisibility(View.VISIBLE);

                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final Dialog dialog = new Dialog(cordova.getActivity());
                        dialog.setContentView(R.layout.buy_credit);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        cordova.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        int height = displaymetrics.heightPixels;
                        int width = displaymetrics.widthPixels;


                        layout_close_add_credit = (RelativeLayout) dialog.findViewById(R.id.layout_close_add_credit);


                        btn_buy_ten =  (Button) dialog.findViewById(R.id.btn_buy_ten);
                        btn_buy_twetnty =  (Button) dialog.findViewById(R.id.btn_buy_twetnty);
                        btn_buy_fourty =  (Button) dialog.findViewById(R.id.btn_buy_fourty);
                        btn_buy_sixty =  (Button) dialog.findViewById(R.id.btn_buy_sixty);
                        layout_progress = (LinearLayout) dialog.findViewById(R.id.layout_progress);
                        layout_credit_btns = (LinearLayout) dialog.findViewById(R.id.layout_credit_btns);

                        layout_close_add_credit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                closeCreditDialog();

                            }
                        });
                        btn_buy_ten.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_credit_btns.setVisibility(View.INVISIBLE);
                                layout_progress.setVisibility(View.VISIBLE);
                                buyCredit(Constants.TEN_DOLLARS);
                            }
                        });
                        btn_buy_twetnty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_credit_btns.setVisibility(View.INVISIBLE);
                                layout_progress.setVisibility(View.VISIBLE);
                                buyCredit(Constants.TWENTY_DOLLARS);
                            }
                        });
                        btn_buy_fourty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_credit_btns.setVisibility(View.INVISIBLE);
                                layout_progress.setVisibility(View.VISIBLE);
                                buyCredit(Constants.FOURTY_DOLLARS);
                            }
                        });
                        btn_buy_sixty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout_credit_btns.setVisibility(View.INVISIBLE);
                                layout_progress.setVisibility(View.VISIBLE);
                                buyCredit(Constants.SIXTY_DOLLARS);
                            }
                        });


                        lp.width = (int)(width * 0.8);
                        lp.height = (int)(height * 0.7);
                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                        dialog.show();
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * sends call back to app for buying credit
     * @param amount which going to be credited
     */
    private void buyCredit(String amount){
        try {
            Log.e(TAG, "buy amount "+amount);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
