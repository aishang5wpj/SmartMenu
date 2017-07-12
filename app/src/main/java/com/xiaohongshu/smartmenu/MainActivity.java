package com.xiaohongshu.smartmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.xiaohongshu.demo4copy.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SmartMenu mSmartMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.left_top_btn).setOnClickListener(this);
        findViewById(R.id.center_top_btn).setOnClickListener(this);
        findViewById(R.id.right_top_btn).setOnClickListener(this);
        findViewById(R.id.left_center_btn).setOnClickListener(this);
        findViewById(R.id.center_btn).setOnClickListener(this);
        findViewById(R.id.right_center_btn).setOnClickListener(this);
        findViewById(R.id.left_bottom_btn).setOnClickListener(this);
        findViewById(R.id.center_bottom_btn).setOnClickListener(this);
        findViewById(R.id.right_bottom_btn).setOnClickListener(this);

        mSmartMenu = new SmartMenu.Builder(this)
                .setView(R.layout.smart_list)
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartMenu.destory();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        mSmartMenu.dismiss();
//        mSmartMenu.show((int) ev.getRawX(), (int) ev.getRawY());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if (mSmartMenu.isShowing()) {
            mSmartMenu.dismiss();
        } else {
            mSmartMenu.show(v);
        }
    }
}
