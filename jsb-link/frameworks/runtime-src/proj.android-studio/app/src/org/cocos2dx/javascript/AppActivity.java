/****************************************************************************
Copyright (c) 2015-2016 Chukong Technologies Inc.
Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.javascript;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.game.v0812.R;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.mcxiaoke.bus.Bus;
import com.mcxiaoke.bus.annotation.BusReceiver;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

public class AppActivity extends Cocos2dxActivity {
    private static AppActivity app = null;
    private static final int REQUEST_CODE = 12;
    private static Handler mHandler = new Handler();
    private static Handler mArrayHandler = new Handler();
    private static ClipboardManager mClipboardManager;
    private RelativeLayout mLayout;
    private static View mViewStartPage;
    private Cocos2dxGLSurfaceView mGLSurfaceView;
    private ImageView mImgStartPage;
    private RelativeLayout mBtnCountTime;
    private TextView mTvCount;
    private static  Runnable mRunnableCountTime;
    private static  Runnable mRunnableArraryString;
    private int mCountTime = 1;
    private int i = 0;
    public  int progress = 0;
    private static View loadPage;
    private static TextView textView;
    private static com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar roundCornerProgressBar;
    private static String[] array = new String[]{"赢了不吱声，说明城府深；输了不投降，竞争意识强" ,
            "看准下重注，超越拆迁户",
            "想要打牌手气好，心理素质加技巧" ,
            "吃吃喝喝都是赔，唯有打牌有来回" ,
            "邀请好友来扫码，每天稳赢几十把" ,
            "打牌打得好，说明有头脑" ,
            "打牌不怕炸，说明胆子大" ,
            "打牌打得精，说明思路清"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = this ;
//        debugModel();
        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            //  so just quietly finish and go away, dropping the user back into the activity
            //  at the top of the stack (ie: the last state of this task)
            // Don't need to finish it again since it's finished in super.onCreate .
            return;
        }
        // DO OTHER INITIALIZATION BELOW
        SDKWrapper.getInstance().init(this);
        mRunnableArraryString = new Runnable() {
            @Override
            public void run() {
                textView.setText(array[i]);
                mArrayHandler.postDelayed(mRunnableArraryString,1500);
                i =  (int)(Math.random()* array.length);

            }
        };
        mRunnableCountTime = new Runnable() {
            @Override
            public void run() {
                mTvCount.setText("跳过 " + mCountTime + "s ");
                if (mCountTime <= -1) {
                    gotoMain();
                    return;
                }
                mHandler.postDelayed(mRunnableCountTime, 1200);
                mCountTime--;
            }
        };
        mClipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
        LayoutInflater inflater = getLayoutInflater();
        mViewStartPage = inflater.inflate(R.layout.activity_start_page, null);
        mLayout = (RelativeLayout) mViewStartPage.findViewById(R.id.box_start_page);
        mImgStartPage = (ImageView) mViewStartPage.findViewById(R.id.img_start_page);
        mBtnCountTime = (RelativeLayout) mViewStartPage.findViewById(R.id.box_count_time);
        mTvCount = (TextView) mViewStartPage.findViewById(R.id.tv_count);
        textView = (TextView) mViewStartPage.findViewById(R.id.text);
        roundCornerProgressBar = mViewStartPage.findViewById(R.id.progress);
        roundCornerProgressBar.setMax(1);
        roundCornerProgressBar.setVisibility(View.GONE);
        mFrameLayout.addView(mViewStartPage);
        // 添加加载图
        /*ImageView imageView = new ImageView(getContext());
        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImage.addRule(RelativeLayout.CENTER_IN_PARENT);

        Glide.with(getContext())
                .load(R.mipmap.giphy)
                .into(imageView);
        mFrameLayout.addView(imageView);*/
        mHandler.post(mRunnableCountTime);
        mArrayHandler.post(mRunnableArraryString);
        mBtnCountTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMain();
            }
        });
        checkPermission();
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
//        mViewStartPage.setVisibility(View.GONE);
    }
    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取绑定数据
            String bindData = appData.getData();
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
        }
    };

    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        mGLSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        mGLSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
        SDKWrapper.getInstance().setGLSurfaceView(mGLSurfaceView, this);
        return mGLSurfaceView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKWrapper.getInstance().onResume(); }

    @Override
    protected void onPause() {
        super.onPause();
        SDKWrapper.getInstance().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKWrapper.getInstance().onDestroy();
        mHandler.removeCallbacks(mRunnableCountTime);
        wakeUpAdapter = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKWrapper.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKWrapper.getInstance().onNewIntent(intent);
        OpenInstall.getWakeUp(intent, wakeUpAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKWrapper.getInstance().onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKWrapper.getInstance().onStop();
        Bus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        SDKWrapper.getInstance().onBackPressed();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SDKWrapper.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SDKWrapper.getInstance().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SDKWrapper.getInstance().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        SDKWrapper.getInstance().onStart();
        super.onStart();
        Bus.getDefault().register(this);

    }

    private void checkPermission() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MANAGE_DOCUMENTS

        };


        boolean hasPermissions = false;
        for (int i = 0; i < permissions.length; i++) {
            int hasWriteStoragePermission = ContextCompat.checkSelfPermission(AppActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("Test Game ", permissions[i] + "  =  " + hasWriteStoragePermission);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                hasPermissions = false;
                break;
            }
        }
        if (!hasPermissions) {
            //没有权限，向用户请求权限
            ActivityCompat.requestPermissions(AppActivity.this, permissions, REQUEST_CODE);
        }
        // 兼容小米的权限申请。
        /*if (Build.VERSION.SDK_INT >= 23) {
            int checkLocalPhonePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkLocalPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        permissions, REQUEST_CODE);
                return;
            }
            //适配小米机型
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int checkOp = appOpsManager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, android.os.Process.myUid(), getPackageName());
            if (checkOp == AppOpsManager.MODE_IGNORED) {
                ActivityCompat.requestPermissions(this,
                        permissions, REQUEST_CODE);
                return;
            }
        }*/


    }

    private static final String TAG = "AppActivity";
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {

                String[] thePermissions = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.MANAGE_DOCUMENTS

                };
                boolean hasPermissions = false;
                for (int i = 0; i < thePermissions.length; i++) {
                    int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    Log.d(TAG, "onRequestPermissionsResult: "+permissions[i] + "  =  " + hasWriteStoragePermission);
                    if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                        hasPermissions = false;
                        break;
                    }
                }
                if (!hasPermissions) {
                    //没有权限，向用户请求权限
                    new AlertDialog.Builder(AppActivity.this)
                            .setMessage("为了给您带来更好的体验，请授权我们。")
                            .setPositiveButton("OK", (dialog1, which) ->
                                    ActivityCompat.requestPermissions(AppActivity.this,
                                            thePermissions,
                                            REQUEST_CODE))
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                    return;
                }
            }
//            // TODO: 2019/6/15 去适配MIUI
            if (Build.MANUFACTURER.equals("Xiaomi")) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 弹出对话框，让用户去设置权限
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("我们需要您同意我们获取读写文件权限")
                            .setPositiveButton("前往授权", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    // 根据包名打开对应的设置界面
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消授权", null).create();
                    dialog.show();
                }
            }

        }
    }

    private void gotoMain() {
        mHandler.removeCallbacks(mRunnableCountTime);
        mTvCount.setVisibility(View.GONE);
        mImgStartPage.setImageResource(R.mipmap.bg);
        roundCornerProgressBar.setVisibility(View.VISIBLE);


    }

    // 供JS调用
    public static void doCaptcha(String jsUrl) {
        Bus.getDefault().post(new MessageEvent(jsUrl));

    }

    // 供JS调用
    public static void printLog(String msg) {
        Log.e("Test", msg);
    }


    // 供JS调用
    public static void nativeLog(String msg) {
        Log.e("Js log info  >>>>", msg);
    }


    @BusReceiver
    public void onSomeEvent(GotoMainEvent event) {
        gotoMain();
    }

    @BusReceiver
    public void onSomeEvent(MessageEvent event) {
        String jsUrl = event.getJsUrl();
        Log.e("url=", jsUrl);
        Intent it = new Intent(getContext(), VerifyPopupActivity.class);
        it.putExtra("jsurl", jsUrl);
        startActivityForResult(it, 1);
    }

    @BusReceiver
    public void onSomeEvent(EventCaptcha event) {
        boolean success = event.isSuccess();
        String ticket = event.getTicket();
        String randstr = event.getRandstr();
        String msg;
        String status = "0";
        if (success) {
            msg = "验证成功,票据为" + ticket + "  --  randstr = " + randstr;
            status = "200";
        } else {
            msg = "验证失败,票据为" + ticket + "  --  randstr = " + randstr;
            status = "0";
        }
        sendJSCaptcha(status, ticket);
        Log.e("Test", msg);
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void sendJSCaptcha(final String status, final String ticket) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                // js 脚本语句句
                String js = "androidBridge(\"" + status + "\",\"" + ticket + "\")";
                // 回调给 js 执⾏行行 androidBridge ⽅方法。
                Cocos2dxJavascriptJavaBridge.evalString(js);
            }
        });
    }

    public static void showToast(String msg) {
        Toast.makeText(AppActivity.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void openUrlByBrowse(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean copyValues(final String values) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, values));
            }
        });
        return true;
    }


    // 直接结束倒计时
    public static void JSGotoMain(String value) {
        Utils.log("JSGotoMain");
        Bus.getDefault().post(new GotoMainEvent());
    }


    public static float showProgress(float i ) {
        Log.d("showProgress",i+"");
        return i ;


    }
    // 加载资源进度控制
    public static void getLoadngingProgressRate(float f){
        Log.d(TAG, "getLoadngingProgressRate: "+f);
        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 設定模組與 Dialog 的風格
                roundCornerProgressBar.setProgress(f);
                if (roundCornerProgressBar.getProgress()>=1.0){
                    mViewStartPage.setVisibility(View.GONE);



                }
            }
        });

        }




    // 热跟新进度控制
    public static void getUpdateProgressRate(float f){
        Log.d(TAG, "getUpdateProgressRate: "+f);
        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 設定模組與 Dialog 的風格
                roundCornerProgressBar.setProgress(f);
                if (roundCornerProgressBar.getProgress()>=1.0){
                    mArrayHandler.removeCallbacks(mRunnableArraryString);
                    textView.setText("更新完毕，将为您重启应用，祝您游戏愉快");
                }
            }
        });

    }
    // 显示更新失败弹窗
    public static void showUpdateFailedDialog(){

        Log.d(TAG, "showUpdateFailedDialog: "+"in");
        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog   dialog = new Dialog(getContext(), R.style.selectorDialog);
                dialog.setContentView(R.layout.layout_alertdialog);
                ImageButton button = dialog.findViewById(R.id.btn);
                TextView textView = dialog.findViewById(R.id.text);
                textView.setText("更新失败！请检查网络后重试。");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        app.runOnGLThread(new Runnable() {
                            @Override
                            public void run() {
                                Cocos2dxJavascriptJavaBridge.evalString("window.retryUpdate()");
                                dialog.dismiss();
                            }
                        });
                    }
                });
                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                lp.dimAmount=0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

            }
        });
    }

    private static  void  showDialog(){

    }


}


