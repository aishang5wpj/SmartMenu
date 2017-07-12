package com.xiaohongshu.smartmenu;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.xiaohongshu.demo4copy.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wupengjian on 17/6/29.
 */
public class SmartMenu {

    private Activity mActivity;
    private PopupWindow mWindow;
    private View mView;
    private View[] mSpiriteArr;
    private int mActivityWidth = -1, mActivityHeight = -1;

    private SmartMenu() {

    }

    public <T> T findViewById(int id) {
        if (mView == null) {
            return null;
        }
        return (T) mView.findViewById(id);
    }

    public void show(View view) {
        if (!isWindowPrepared() || isShowing()) {
            return;
        }
        int[] location = new int[2];
        view.getLocationInWindow(location);
        ShowPosition position = caculateShowPosition(location[0], location[1]
                , view.getMeasuredWidth(), view.getMeasuredHeight());
        showOnScreen(position);
    }

    public void show(int x, int y) {
        if (!isWindowPrepared() || isShowing()) {
            return;
        }
        ShowPosition position = caculateShowPosition(x, y);
        showOnScreen(position);
    }

    /**
     * 先显示window，再显示小箭头
     * <p>
     * {@link #caculateShowPosition(int, int, int, int)}}计算出来的position并不一定是window最终显示的
     * 位置，比如如果计算出来的window位置导致window有一部分显示在屏幕外，则PopupWindow会被自动调整到屏幕内，
     * 这部分工作是系统自动完成的，而我们并不能直接知道这种调整，所以这种时候ShowPosition中的位置和Window实际
     * 显示在屏幕中的位置并不一致，所以只有在View被添加到屏幕中后，手动计算Window的偏移，然后得到小箭头的偏移，
     * 最终完成小箭头位置的摆放
     *
     * @param position
     */
    private void showOnScreen(final ShowPosition position) {
        final ShowPosition pos = position;
        if (pos == null) {
            return;
        }
        mWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.NO_GRAVITY, pos.x, pos.y);
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                showSpirite(position);
                mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void showSpirite(ShowPosition position) {
        final ShowPosition pos = position;
        for (View view : mSpiriteArr) {
            view.setVisibility(View.GONE);
        }
        int[] location = new int[2];
        mView.getLocationOnScreen(location);
        ViewGroup.MarginLayoutParams params;
        int spriteX = pos.x - location[0] + pos.spriteX;
        int spriteY = pos.y - location[1] + pos.spriteY;
//        int spriteX = pos.spriteX;
//        int spriteY = pos.spriteY;
        switch (pos.spriteGravity) {
            case ShowPosition.LEFT:
                mSpiriteArr[0].setVisibility(View.VISIBLE);
                params = (ViewGroup.MarginLayoutParams) mSpiriteArr[0].getLayoutParams();
                params.setMargins(spriteX, 0, spriteY, 0);
                mSpiriteArr[0].setLayoutParams(params);
                break;
            case ShowPosition.TOP:
                mSpiriteArr[1].setVisibility(View.VISIBLE);
                params = (ViewGroup.MarginLayoutParams) mSpiriteArr[1].getLayoutParams();
                params.setMargins(spriteX, 0, spriteY, 0);
                mSpiriteArr[1].setLayoutParams(params);
                break;
            case ShowPosition.RIGHT:
                mSpiriteArr[2].setVisibility(View.VISIBLE);
                params = (ViewGroup.MarginLayoutParams) mSpiriteArr[2].getLayoutParams();
                params.setMargins(spriteX, 0, spriteY, 0);
                mSpiriteArr[2].setLayoutParams(params);
                break;
            case ShowPosition.BOTTOM:
                mSpiriteArr[3].setVisibility(View.VISIBLE);
                params = (ViewGroup.MarginLayoutParams) mSpiriteArr[3].getLayoutParams();
                params.setMargins(spriteX, 0, spriteY, 0);
                mSpiriteArr[3].setLayoutParams(params);
                break;
        }
    }

    private ShowPosition caculateShowPosition(int x, int y) {
        return caculateShowPosition(x, y, 0, 0);
    }

    /**
     * 优先计算垂直方向上的位置，如果垂直方向没有合适的位置，先右后左计算位置
     * （事实上基本走不到计算左边、右边位置的逻辑，上下方向已经可以满足大部分情况，所以计算左右位置的算法没有仔细去算
     * ，不一定管用）
     *
     * @param x
     * @param y
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private ShowPosition caculateShowPosition(int x, int y, int targetWidth, int targetHeight) {
        if (mActivityWidth == -1 && mActivityHeight == -1) {
            mActivityWidth = getActivityWidth();
            mActivityHeight = getActivityHeight();
        }
        ShowPosition position = caculateBottomPosition(x, y, targetWidth, targetHeight);
        if (position != null) {
            return position;
        }
        position = caculateTopPosition(x, y, targetWidth, targetHeight);
        if (position != null) {
            return position;
        }
        position = caculateRightPosition(x, y, targetWidth, targetHeight);
        if (position != null) {
            return position;
        }
        position = caculateLeftPosition(x, y, targetWidth, targetHeight);
        if (position != null) {
            return position;
        }
        return position;
    }

    /**
     * 计算当menu应该在target左边时可以摆放的位置，当menu在target左边，箭头方向应该是右边
     *
     * @param x
     * @param y
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private ShowPosition caculateLeftPosition(int x, int y, int targetWidth, int targetHeight) {
        ShowPosition position = new ShowPosition();
        //spirite被隐藏了，getWindowWidth()计算宽度时没有算进去，所以加上spirite的额外宽度
        position.x = x - getWindowWidth() - mSpiriteArr[2].getMeasuredWidth();
        position.y = y + (targetHeight - getWindowHeight()) / 2;
        //相对target垂直居中
        position.spriteX = 0;
        position.spriteY = (targetHeight - mSpiriteArr[2].getMeasuredHeight()) >> 1;
        position.spriteGravity = ShowPosition.RIGHT;
        if (position.x < 0) {
            return null;
        }
        return position;
    }

    /**
     * 计算当menu应该在target上边时可以摆放的位置，当menu在target上边，箭头方向应该是底部
     *
     * @param x
     * @param y
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private ShowPosition caculateTopPosition(int x, int y, int targetWidth, int targetHeight) {
        ShowPosition position = new ShowPosition();
        //相对target水平居中
        int offsetX = (targetWidth - getWindowWidth()) / 2;
        position.x = x + offsetX;
        //spirite被隐藏了，getWindowHeight时没有算进去，所以加上spirite的额外高度
        position.y = y - getWindowHeight() - mSpiriteArr[3].getMeasuredHeight();
        //相对target水平居中
        position.spriteX = (targetWidth - mSpiriteArr[3].getMeasuredWidth()) / 2 - offsetX;
        position.spriteY = 0;
        position.spriteGravity = ShowPosition.BOTTOM;
        if (position.y < 0) {
            return null;
        }
        return position;
    }

    /**
     * 计算当menu应该在target右边时可以摆放的位置，当menu在target右边，箭头方向应该是左边
     *
     * @param x
     * @param y
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private ShowPosition caculateRightPosition(int x, int y, int targetWidth, int targetHeight) {
        ShowPosition position = new ShowPosition();
        position.x = x + targetWidth;
        position.y = y + (targetHeight - getWindowHeight()) / 2;
        //相对target垂直居中
        position.spriteX = 0;
        position.spriteY = (targetHeight - mSpiriteArr[0].getMeasuredHeight()) >> 1;
        position.spriteGravity = ShowPosition.LEFT;
        if (position.x + getWindowWidth() > mActivityWidth) {
            return null;
        }
        return position;
    }

    /**
     * 计算当menu应该在target底部时可以摆放的位置，当menu在target底部，箭头方向应该是上边
     *
     * @param x
     * @param y
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private ShowPosition caculateBottomPosition(int x, int y, int targetWidth, int targetHeight) {
        ShowPosition position = new ShowPosition();
        //相对target水平居中
        int offsetX = (targetWidth - getWindowWidth()) / 2;
        position.x = x + offsetX;
        //spirite被隐藏了，getWindowHeight时没有算进去，所以加上spirite的额外高度
        position.y = y + targetHeight;
        //相对target水平居中
        position.spriteX = (targetWidth - mSpiriteArr[1].getMeasuredWidth()) / 2 - offsetX;
        position.spriteY = 0;
        position.spriteGravity = ShowPosition.TOP;
        if (position.y + getWindowHeight() > mActivityHeight) {
            return null;
        }
        return position;
    }

    private int getWindowWidth() {
        return mView.getMeasuredWidth();
    }

    private int getWindowHeight() {
        return mView.getMeasuredHeight();
    }

    public boolean isShowing() {
        if (!isWindowPrepared()) {
            return false;
        }
        return mWindow.isShowing();
    }

    public void dismiss() {
        if (!isWindowPrepared() || !isShowing()) {
            return;
        }
        mWindow.dismiss();
    }

    public void destory() {
        dismiss();
        mWindow = null;
        mActivity = null;
        mView = null;
    }

    /**
     * window是否处于可活动状态
     *
     * @return
     */
    private boolean isWindowPrepared() {
        if (mActivity == null || mActivity.isFinishing()
                || mWindow == null || mSpiriteArr == null) {
            return false;
        }
        return true;
    }

    private int getActivityWidth() {
        if (mActivity == null || mActivity.isFinishing()) {
            return 0;
        }
        return mActivity.getWindow().getDecorView().getMeasuredWidth();
    }

    private int getActivityHeight() {
        if (mActivity == null || mActivity.isFinishing()) {
            return 0;
        }
        return mActivity.getWindow().getDecorView().getMeasuredHeight();
    }

    public static class Builder {

        private Activity mActivity;
        private View mView;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder setView(@LayoutRes int resource) {
            View view = LayoutInflater.from(mActivity).inflate(resource, null);
            setView(view);
            return this;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public SmartMenu build() {
            SmartMenu smartMenu = new SmartMenu();
            smartMenu.mActivity = mActivity;
            smartMenu.mView = mView;
            smartMenu.mWindow = initWindow();
            smartMenu.mSpiriteArr = new View[4];
            smartMenu.mSpiriteArr[0] = mView.findViewById(R.id.smart_menu_spirite_left);
            smartMenu.mSpiriteArr[1] = mView.findViewById(R.id.smart_menu_spirite_up);
            smartMenu.mSpiriteArr[2] = mView.findViewById(R.id.smart_menu_spirite_right);
            smartMenu.mSpiriteArr[3] = mView.findViewById(R.id.smart_menu_spirite_down);
            return smartMenu;
        }

        private PopupWindow initWindow() {
            if (mActivity == null || mActivity.isFinishing()) {
                return null;
            }
            final View contentView = mView;
            //赋值之后，Builder中的mView和SmartMenu中的mView不再是同一个对象，后者是前者的一个子View
            mView = LayoutInflater.from(mActivity).inflate(R.layout.smart_menu_root, null);
            ViewGroup container = (ViewGroup) mView.findViewById(R.id.smart_menu_content_layout);
            container.addView(contentView);
            PopupWindow window = new PopupWindow(mView
                    , WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //立即测量宽高，否则计算位置时得不到window的宽高导致位置不准
            mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setTouchable(true);
            window.setFocusable(true);
            window.setOutsideTouchable(false);
            return window;
        }
    }

    private static class ShowPosition {

        @IntDef({LEFT, TOP, RIGHT, BOTTOM})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Gravity {
        }

        public static final int LEFT = 0;
        public static final int TOP = 1;
        public static final int RIGHT = 2;
        public static final int BOTTOM = 3;

        /**
         * SmartMenu的X坐标，相对屏幕的位置
         */
        public int x;
        /**
         * SmartMenu的Y坐标，相对屏幕的位置
         */
        public int y;
        /**
         * 小箭头的x坐标，相对SmartMenu的位置
         */
        public int spriteX;
        /**
         * 小箭头的y坐标，相对SmartMenu的位置
         */
        public int spriteY;
        /**
         * 小箭头的方向
         */
        @Gravity
        public int spriteGravity;
    }
}
