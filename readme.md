#SmartMenu

一款可以根据按钮位置来自动调整自己位置的菜单悬浮窗。

![](https://github.com/aishang5wpj/smartmenu/raw/master/images/screenshot/smart_menu.gif)

#使用方式

```

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
```