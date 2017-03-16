package cn.appleye.stepsview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.appleye.stepsview.util.DisplayUtil;
import cn.appleye.stepsview.widget.TextImageView;

/**
 * Created by liuliaopu on 2017/1/23.
 */

public class StepsView extends FrameLayout{
    private static final String TAG = "StepsView";
    /**
     * 步骤数
     * */
    private int mStepsCount;
    /**
     * 当前步骤
     * */
    private int mCurrentStepIndex = 0;

    /**
     * 步骤控件列表
     * */
    private ArrayList<TextImageView> mStepViews = new ArrayList<>();

    /**
     * 步骤之间分割线列表
     * */
    private ArrayList<ImageView> mSplitLines = new ArrayList<>();

    /**
     * 步骤变化监听
     * */
    private OnStepChangedListener mOnStepChangedListener;

    /**
     * 左右边距
     * */
    private final int mMarginLeftRightSide;
    /**
     * 控件之间间隙
     * */
    private final int mMarginLeftRightGap;
    /**
     * 步骤字体颜色值
     * */
    private final int mStepTextColor;
    /**
     * 步骤字体大小值
     * */
    private final int mStepTextSize;

    /**
     * 所有控件总宽度
     * */
    private int mTotalWidth;
    /**
     * step控件的宽度
     * */
    private int mStepViewWidth = -1;
    /**
     * step控件高度
     * */
    private int mStepViewHeight = -1;
    /**
     * split控件宽度
     * */
    private int mSplitLineWidth = -1;
    /**
     * split控件高度
     * */
    private int mSplitLineHeight = -1;

    /**
     * 下三角颜色
     * */
    private int mDownTriangleColor;

    public StepsView(Context context) {
        this(context, null);
    }

    public StepsView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public StepsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.steps_view);
        mDownTriangleColor = typedArray.getColor(R.styleable.steps_view_down_triangle_color, 0xffffffff);
        typedArray.recycle();

        Resources res = context.getResources();
        mMarginLeftRightSide = res.getDimensionPixelOffset(R.dimen.margin_left_right_side);
        mMarginLeftRightGap = res.getDimensionPixelOffset(R.dimen.margin_left_right_gap);

        mStepTextColor = res.getColor(R.color.default_text_color);

        mStepTextSize = DisplayUtil.sp2px(context, 18);
    }

    /**
     * 设置步骤数，会把当前步骤设置为初始值，列表也会初始化
     * @param count 数
     * */
    public void setStepsCount(int count) {
        mStepsCount = count;
        initViews();

        setCurrentStep(0);
    }

    /**
     * 初始化所有子view
     * */
    private void initViews(){
        removeAllViews();

        mStepViews.clear();
        mSplitLines.clear();

        mTotalWidth = 0;

        mStepViewWidth = -1;
        mSplitLineWidth = -1;

        for(int i=0; i<mStepsCount; i++) {
            TextImageView stepView = new TextImageView(getContext());
            stepView.setText((i+1) + "");
            stepView.setTextColor(mStepTextColor);
            stepView.setTextSize(mStepTextSize);

            //默认第一个步骤为正在处理
            if(i==0) {
                stepView.setImageResource(R.drawable.step_doing);
            } else {
                stepView.setImageResource(R.drawable.step_disable);
            }

            stepView.measure(0, 0);

            if(mStepViewWidth < 0) {
                mStepViewWidth = stepView.getMeasuredWidth();
                mStepViewHeight = stepView.getMeasuredHeight();
            }

            mTotalWidth += mStepViewWidth;
            addView(stepView);

            mStepViews.add(stepView);

            /*分割线比步骤小一个*/
            if(i < mStepsCount - 1) {
                ImageView splitLineView = new ImageView(getContext());
                splitLineView.setImageResource(R.drawable.split_line_disable);
                splitLineView.measure(0, 0);
                mTotalWidth += mMarginLeftRightGap*2;//添加控件间距

                if(mSplitLineWidth < 0) {
                    mSplitLineWidth = splitLineView.getMeasuredWidth();
                    mSplitLineHeight = splitLineView.getMeasuredHeight();
                }

                mTotalWidth += mSplitLineWidth;//添加分割线宽度

                addView(splitLineView);

                mSplitLines.add(splitLineView);
            }
        }

    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        int height = bottom - top;

        int leftBound = (width - mTotalWidth)/2;
        int childCount = getChildCount();
        for(int i=0; i<childCount; i++) {
            View childView = getChildAt(i);
            int childViewWidth = 0;
            int childViewHeight = 0;
            if(i%2==0) {
                childViewWidth = mStepViewWidth;
                childViewHeight = mStepViewHeight;
            } else {
                childViewWidth = mSplitLineWidth;
                childViewHeight = mSplitLineHeight;
            }
            if(i != 0) {
                leftBound += mMarginLeftRightGap;
            }

            childView.layout(leftBound, (height-childViewHeight)/2,
                    leftBound+childViewWidth, (height+childViewHeight)/2);
            leftBound += childViewWidth;
        }
    }

    /**
     * 设置步骤变化监听
     * @param listener {@link OnStepChangedListener}
     * */
    public void setStepChangedListener(OnStepChangedListener listener) {
        mOnStepChangedListener = listener;
    }

    /**
     * 跳到当前步骤
     * @param stepIndex 步骤索引
     * */
    public void setCurrentStep(int stepIndex){

        if(stepIndex < 0) {
            return;
        }

        mCurrentStepIndex = stepIndex;

        updateStepStatus(mCurrentStepIndex);

        if(mOnStepChangedListener != null) {
            mOnStepChangedListener.onStepChanged(mCurrentStepIndex);
        }
    }

    /**
     * 跳到下一步
     * */
    public void nextStep() {
        if(mCurrentStepIndex >= mStepsCount) {
            mCurrentStepIndex = mStepsCount;
            return;
        }

        setCurrentStep(++mCurrentStepIndex);
    }

    /**
     * 跳到上一步
     * */
    public void lastStep() {
        if(mCurrentStepIndex <= 0) {
            mCurrentStepIndex = 0;
            return;
        }
        setCurrentStep(--mCurrentStepIndex);
    }

    private void updateStepStatus(int stepIndex) {
        if(stepIndex < 0) {
            return;
        }

        if(stepIndex >= mStepsCount) {
            doneAll();
            return;
        }

        //小于当前步骤的，设置已经完成该步骤
        for(int i=0; i<stepIndex; i++) {
            TextImageView stepImageView = mStepViews.get(i);
            stepImageView.setImageResource(R.drawable.step_done);
            stepImageView.setText(null);

            ImageView splitLineView = mSplitLines.get(i);
            splitLineView.setImageResource(R.drawable.split_line_enable);
        }

        //所有大于当前步骤的，设置为未完成状态
        for(int i=stepIndex+1; i<mStepsCount; i++) {
            TextImageView stepImageView = mStepViews.get(i);
            stepImageView.setImageResource(R.drawable.step_disable);
            stepImageView.setText((i+1) + "");

            ImageView splitLineView = mSplitLines.get(i-1);
            splitLineView.setImageResource(R.drawable.split_line_disable);
        }

        //当前步骤正在进行时
        TextImageView stepImageView = mStepViews.get(stepIndex);
        stepImageView.setText((stepIndex+1) + "");
        stepImageView.setImageResource(R.drawable.step_doing);

        invalidate();
    }

    /**
     * 所有的步骤都已完成
     * */
    private void doneAll() {
        for(int i=0; i<mStepsCount; i++) {
            TextImageView stepImageView = mStepViews.get(i);
            stepImageView.setImageResource(R.drawable.step_done);
            stepImageView.setText(null);

            if(i != mStepsCount - 1) {
                ImageView splitLineView = mSplitLines.get(i);
                splitLineView.setImageResource(R.drawable.split_line_enable);
            }
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        int selectedIndex = mCurrentStepIndex >= mStepsCount ? mStepsCount - 1 : mCurrentStepIndex;

        View childView = mStepViews.get(selectedIndex);

        if(childView == null) {
            return;
        }

        int height = getHeight();
        int left = childView.getLeft();

        /*下三角高度*/
        int downTrian = (getHeight() - mStepViewHeight) /2;

        Path path = new Path();
        path.moveTo(left + mStepViewWidth/2, height - downTrian);
        path.lineTo(left + mStepViewWidth/2 - downTrian, height);
        path.lineTo(left + mStepViewWidth/2 + downTrian, height);
        path.close();

        Paint paint = new Paint();
        paint.setColor(mDownTriangleColor);

        canvas.drawPath(path, paint);
    }

    public interface OnStepChangedListener{
        /**
         * 进行到某一步，从0开始计算
         * @param stepIndex 步骤索引
         * */
        void onStepChanged(int stepIndex);
    }
}
