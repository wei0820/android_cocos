package org.cocos2dx.javascript.service;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.game.v0812.R;
import com.bumptech.glide.Glide;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

public class AlertMAnager  extends Cocos2dxActivity {
    public static void setDilog(Context context,String s){

        Dialog dialog = new Dialog(context, R.style.selectorDialog);
        dialog.setContentView(R.layout.layout_photo);
        // 動畫設定 (指定旋轉動畫) (startAngle, endAngle, rotateX, rotateY)

        RelativeLayout relativeLayout = dialog.findViewById(R.id.lay);
        ImageView img = dialog.findViewById(R.id.img);
        ImageView close = dialog.findViewById(R.id.close);
        ImageView reload = dialog.findViewById(R.id.reload);
        ImageButton imageButton = dialog.findViewById(R.id.btn);
        ImageButton imageButton1 = dialog.findViewById(R.id.close);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"in",Toast.LENGTH_SHORT).show();
                //動畫路徑設定(x1,x2,y1,y2)
                //讀入動畫設定
                reload.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim));

            }
        });

        Glide.with(context)
                .load(s)
                .into(img);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        imageButton.setScaleY((float)0.9);
                        imageButton.setScaleX((float)0.9);

                        break;


                    case MotionEvent.ACTION_UP:
                        imageButton.setScaleY(1);
                        imageButton.setScaleX(1);

                        break;
                }


                return false;
            }
        });
        imageButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        imageButton1.setScaleY((float)0.9);
                        imageButton1.setScaleX((float)0.9);

                        break;


                    case MotionEvent.ACTION_UP:
                        imageButton1.setScaleY(1);
                        imageButton1.setScaleX(1);

                        break;
                }


                return false;
            }
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        // 圖片配置動畫

        // 動畫開始

        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        lp.width =  WindowManager.LayoutParams.MATCH_PARENT;
        lp.height =  WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
