package com.wangjie.rapidfloatingactionbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/29/15.
 */
public class RapidFloatingActionLayout extends RelativeLayout implements OnClickListener {
    public RapidFloatingActionLayout(Context context) {
        super(context);
        initAfterConstructor();
    }

    public RapidFloatingActionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parserAttrs(context, attrs, 0, 0);
        initAfterConstructor();
    }

    public RapidFloatingActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parserAttrs(context, attrs, defStyleAttr, 0);
        initAfterConstructor();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RapidFloatingActionLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parserAttrs(context, attrs, defStyleAttr, defStyleRes);
        initAfterConstructor();
    }

    private void parserAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RapidFloatingActionLayout, defStyleAttr, defStyleRes);
        frameColor = a.getColor(R.styleable.RapidFloatingActionLayout_rfal_frame_color, getContext().getResources().getColor(R.color.rfab__color_frame));
        frameAlpha = a.getFloat(R.styleable.RapidFloatingActionLayout_rfal_frame_alpha,
                Float.valueOf(getResources().getString(R.string.rfab_rfal__float_convert_color_alpha))
        );

        frameAlpha = frameAlpha > 1f ? 1f : (frameAlpha < 0f ? 0f : frameAlpha);

        a.recycle();

    }

    public static final long ANIMATION_DURATION = 150/*ms*/;

    private void initAfterConstructor() {

    }

    private OnRapidFloatingActionListener onRapidFloatingActionListener;

    public void setOnRapidFloatingActionListener(OnRapidFloatingActionListener onRapidFloatingActionListener) {
        this.onRapidFloatingActionListener = onRapidFloatingActionListener;
    }

    private View fillFrameView;
    private RapidFloatingActionContent contentView;

    private int frameColor;
    private float frameAlpha;

    public RapidFloatingActionLayout setContentView(RapidFloatingActionContent contentView) {
        if (null == contentView) {
            throw new RuntimeException("contentView can not be null");
        }
        if (null != this.contentView) {
            throw new RuntimeException("contentView: [" + this.contentView + "] is already initialed");
        }
        this.contentView = contentView;
        // 添加背景覆盖层
        fillFrameView = new View(getContext());
        fillFrameView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fillFrameView.setBackgroundColor(frameColor);
        fillFrameView.setVisibility(GONE);
        fillFrameView.setOnClickListener(this);
        this.addView(fillFrameView, Math.max(this.getChildCount() - 1, 0));

        // 添加内容
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ABOVE, onRapidFloatingActionListener.obtainRFAButton().getId());
        lp.addRule(RelativeLayout.ALIGN_RIGHT, onRapidFloatingActionListener.obtainRFAButton().getId());
        this.contentView.setLayoutParams(lp);
        this.contentView.setVisibility(GONE);
        this.addView(this.contentView);
        return this;
    }

    @Override
    public void onClick(View v) {
        if (fillFrameView == v) {
            collapseContent();
        }
    }

    private boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void toggleContent() {
        if (isExpanded) {
            collapseContent();
        } else {
            expandContent();
        }
    }

    public void expandContent() {
        isExpanded = true;
        contentAnimator.setTarget(this.contentView);
        contentAnimator.setFloatValues(0.0f, 1.0f);
        contentAnimator.setPropertyName("alpha");

        fillFrameAnimator.setTarget(this.fillFrameView);
        fillFrameAnimator.setFloatValues(0.0f, frameAlpha);
        fillFrameAnimator.setPropertyName("alpha");

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(contentAnimator, fillFrameAnimator);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        onRapidFloatingActionListener.onExpandAnimator(animatorSet);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                contentView.setVisibility(VISIBLE);
                fillFrameView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                isExpanded = true;
            }
        });

        animatorSet.start();

    }

    public void collapseContent() {
        isExpanded = false;
        contentAnimator.setTarget(this.contentView);
        contentAnimator.setFloatValues(1.0f, 0.0f);
        contentAnimator.setPropertyName("alpha");

        fillFrameAnimator.setTarget(this.fillFrameView);
        fillFrameAnimator.setFloatValues(frameAlpha, 0.0f);
        fillFrameAnimator.setPropertyName("alpha");

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(contentAnimator, fillFrameAnimator);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        onRapidFloatingActionListener.onExpandAnimator(animatorSet);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                contentView.setVisibility(VISIBLE);
                fillFrameView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                contentView.setVisibility(GONE);
                fillFrameView.setVisibility(GONE);
            }
        });
        animatorSet.start();

    }

    ObjectAnimator contentAnimator = new ObjectAnimator();
    ObjectAnimator fillFrameAnimator = new ObjectAnimator();


}