package org.cocos2dx.javascript;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.app.game.v0812.R;
import com.mcxiaoke.bus.Bus;
import com.token.verifysdk.VerifyCoder;


public class VerifyPopupActivity extends Activity {

    private WebView mWebView;
    private RelativeLayout mContainer;
    private ProgressBar mProgressBar;
    private float mDensity;
    private float mScale = 0.8f; //默认弹框验证码宽度是屏幕宽度*0.7
    private final float F_DEFAULT_POPUP_IFRAME_WIDTH = 17f * 10;
    private final int F_MAX_IFRAME_WIDTH_SCALE = 2;
    private final int F_CAP_TYPE_CLICK_CHAR_ZH = 4;//图中点字(中文)
    private final int F_CAP_TYPE_CLICK_CHAR_EN = 6;//图中点字(英文)
    private final int F_CAP_TYPE_SLIDE_PUZZLE = 6;//滑动拼图

    private boolean isSuccess = false;  // 用来判断是否成功

    private VerifyCoder.VerifyListener mListener = new VerifyCoder.VerifyListener() {

        @Override
        public void onVerifySucc(String ticket, String randstr) {
            isSuccess = true;

            Bus.getDefault().post(new EventCaptcha(true, ticket, randstr));

            finish();
        }

        @Override
        public void onVerifyFail() {
            //Bus.getDefault().post(new EventCaptcha(false, "", ""));
            finish();
        }

        @Override
        public void onIframeLoaded(int state, String info) {
            //收到验证码页面(包括图片)加载完成回调时，把Loading隐藏，WebView显示
            mProgressBar.setVisibility(View.INVISIBLE);
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onIFrameResize(float width, float height) {
            //验证
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.width = (int) (width * mDensity);
            attributes.height = (int) (height * mDensity);
            getWindow().setAttributes(attributes);
        }

    };

    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String jsurl = getIntent().getStringExtra("jsurl");
        if (jsurl == null) {
            finish();
            return;
        }

        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mDensity = (float) (metrics.density * 1.01);
        int windowWidth = metrics.widthPixels;

        /*
         * 以滑动拼图弹框验证码为例，取弹框验证码宽度为屏幕宽度0.7
         * 滑动拼图标准宽18.2*16dp，标准高16.1*16dp,最大缩放比例2 ----capType=7
         * 图中点字标准宽18.2*16dp，标准高19.6*16dp,最大缩放比例2 ----capType=4,6
         * */
        int iframeWidthPX = (int) (windowWidth * mScale);
        int iframeWidthDP = (int) (iframeWidthPX / mDensity);
        if (iframeWidthDP >= (int) (F_DEFAULT_POPUP_IFRAME_WIDTH * F_MAX_IFRAME_WIDTH_SCALE)) {
            iframeWidthDP = (int) (F_DEFAULT_POPUP_IFRAME_WIDTH * F_MAX_IFRAME_WIDTH_SCALE);
            iframeWidthPX = (int) (iframeWidthDP * mDensity);
        }
        //根据验证码类型和弹框宽度，获取验证码弹框高度
        int iframeHeightDP = VerifyCoder.getPopupIframeHeightByWidthAndCaptype(iframeWidthDP, F_CAP_TYPE_SLIDE_PUZZLE);
        int iframeHeightPX = (int) (iframeHeightDP * mDensity);

        //设置主题色，弹框验证码，弹框宽度
        VerifyCoder verifyCoder = VerifyCoder.getVerifyCoder();
        verifyCoder.setJson("themeColor:'ff0000',type:'popup',fwidth:" + iframeWidthDP);
        mWebView = verifyCoder.getWebView(getApplicationContext(), jsurl, mListener);
        mWebView.requestFocus();
        mWebView.forceLayout();
      /*  mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDefaultFontSize(13);
        mWebView.getSettings().setBuiltInZoomControls(true);*/


        //业务可根据自己需要实现不同的loading展现
        setContentView(R.layout.activity_verify_popup);
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView.setVisibility(View.INVISIBLE);
        mContainer.addView(mWebView);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = iframeWidthPX;
        attributes.height = iframeHeightPX;
        getWindow().setAttributes(attributes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.freeMemory();
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        VerifyCoder.getVerifyCoder().release();


        if (!isSuccess) {
            Bus.getDefault().post(new EventCaptcha(false, "", ""));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
