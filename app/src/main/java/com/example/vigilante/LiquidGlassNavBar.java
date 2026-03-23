package com.example.vigilante;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiquidGlassNavBar extends FrameLayout {

    private static final int TAB_COUNT = 4;
    private static final long ANIM_DURATION = 400;
    private static final float DRAG_THRESHOLD = 10f;

    private ImageView[] tabIcons = new ImageView[TAB_COUNT];
    private TextView[] tabLabels = new TextView[TAB_COUNT];

    private int selectedTab = 0;
    private float pillCenterX = 0;
    private int tabWidth = 0;

    private Paint barBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pillFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pillHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pillShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint refractionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF barRect = new RectF();
    private RectF pillRect = new RectF();
    private RectF highlightRect = new RectF();
    private float barCornerRadius;
    private float pillCornerRadius;
    private float pillHPad;
    private float pillVPad;

    private OnTabSelectedListener listener;

    private float touchDownX;
    private float touchDownPillCenterX;
    private boolean isDragging;

    private int[] iconResIds = {
            R.drawable.ic_nav_events,
            R.drawable.ic_nav_home,
            R.drawable.ic_nav_alerts,
            R.drawable.ic_nav_profile
    };
    private String[] labels = {"Events", "Home", "Alerts", "Profile"};

    private int activeColor;
    private int inactiveColor;
    private float density;

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public LiquidGlassNavBar(Context context) {
        super(context);
        init(context);
    }

    public LiquidGlassNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LiquidGlassNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        density = context.getResources().getDisplayMetrics().density;
        barCornerRadius = 34 * density;
        pillCornerRadius = 22 * density;
        pillHPad = 6 * density;
        pillVPad = 5 * density;

        activeColor = Color.parseColor("#1A1A1A");
        inactiveColor = Color.parseColor("#AAAAAA");

        // no clipping since we draw a glow that extends above our bounds
        setClipChildren(false);
        setClipToPadding(false);

        // outline provider gives us the elevation shadow in the bar shape
        setElevation(6 * density);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(android.view.View view, android.graphics.Outline outline) {
                float r = 34 * view.getContext().getResources().getDisplayMetrics().density;
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), r);
            }
        });
        // no clipToOutline since we paint everything ourselves including the glow above

        // bar background paint
        barBgPaint.setColor(Color.parseColor("#E8EAEC"));
        barBgPaint.setStyle(Paint.Style.FILL);

        // white capsule pill
        pillFillPaint.setColor(Color.WHITE);
        pillFillPaint.setStyle(Paint.Style.FILL);

        // glass highlight on capsule top
        pillHighlightPaint.setColor(Color.parseColor("#38FFFFFF"));
        pillHighlightPaint.setStyle(Paint.Style.FILL);

        // soft shadow under capsule
        pillShadowPaint.setColor(Color.parseColor("#18000000"));
        pillShadowPaint.setStyle(Paint.Style.FILL);
        pillShadowPaint.setMaskFilter(
                new android.graphics.BlurMaskFilter(4 * density, android.graphics.BlurMaskFilter.Blur.NORMAL));

        // refraction glow paint updated per frame with a RadialGradient
        refractionPaint.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // tab items
        LinearLayout tabContainer = new LinearLayout(context);
        tabContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(tabContainer, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        for (int i = 0; i < TAB_COUNT; i++) {
            LinearLayout tab = new LinearLayout(context);
            tab.setOrientation(LinearLayout.VERTICAL);
            tab.setGravity(Gravity.CENTER);

            ImageView icon = new ImageView(context);
            icon.setImageResource(iconResIds[i]);
            icon.setColorFilter(inactiveColor);
            LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                    (int) (22 * density), (int) (22 * density));
            tab.addView(icon, iconLp);

            TextView label = new TextView(context);
            label.setText(labels[i]);
            label.setTextSize(10);
            label.setGravity(Gravity.CENTER);
            label.setTextColor(inactiveColor);
            label.setTypeface(null, android.graphics.Typeface.NORMAL);
            LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            labelLp.topMargin = (int) (2 * density);
            tab.addView(label, labelLp);

            LinearLayout.LayoutParams tabLp = new LinearLayout.LayoutParams(
                    0, LayoutParams.MATCH_PARENT, 1);
            tabContainer.addView(tab, tabLp);

            tabIcons[i] = icon;
            tabLabels[i] = label;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tabWidth = w / TAB_COUNT;
        pillCenterX = selectedTab * tabWidth + tabWidth / 2f;
        barRect.set(0, 0, w, h);
        updateTabAppearance();
    }

    public void setSelectedTab(int position) {
        selectedTab = position;
        if (tabWidth > 0) {
            pillCenterX = position * tabWidth + tabWidth / 2f;
            updateTabAppearance();
            invalidate();
        } else {
            post(() -> {
                pillCenterX = selectedTab * tabWidth + tabWidth / 2f;
                updateTabAppearance();
                invalidate();
            });
        }
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    // Citation: Ved, March 14 2025, Claude referred to https://developer.android.com/reference/android/animation/ValueAnimator
    private void animateToTab(int position) {
        int prevTab = selectedTab;
        selectedTab = position;
        float targetX = position * tabWidth + tabWidth / 2f;

        ValueAnimator anim = ValueAnimator.ofFloat(pillCenterX, targetX);
        anim.setDuration(ANIM_DURATION);
        anim.setInterpolator(new OvershootInterpolator(0.6f));
        anim.addUpdateListener(a -> {
            pillCenterX = (float) a.getAnimatedValue();
            updateTabAppearance();
            invalidate();
        });
        anim.start();

        if (listener != null && position != prevTab) {
            listener.onTabSelected(position);
        }
    }

    private void updateTabAppearance() {
        for (int i = 0; i < TAB_COUNT; i++) {
            float tabCenter = i * tabWidth + tabWidth / 2f;
            float dist = Math.abs(pillCenterX - tabCenter);
            float t = Math.max(0, 1 - dist / tabWidth);

            int color = blendColor(inactiveColor, activeColor, t);
            tabIcons[i].setColorFilter(color);
            tabLabels[i].setTextColor(color);

            tabLabels[i].setTypeface(null, t > 0.5f ?
                    android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

            float scale = 1.0f + 0.08f * t;
            tabIcons[i].setScaleX(scale);
            tabIcons[i].setScaleY(scale);
        }
    }

    private int blendColor(int from, int to, float ratio) {
        float ir = 1 - ratio;
        int a = (int) (Color.alpha(from) * ir + Color.alpha(to) * ratio);
        int r = (int) (Color.red(from) * ir + Color.red(to) * ratio);
        int g = (int) (Color.green(from) * ir + Color.green(to) * ratio);
        int b = (int) (Color.blue(from) * ir + Color.blue(to) * ratio);
        return Color.argb(a, r, g, b);
    }

    @Override
    public void draw(Canvas canvas) {
        // draw the refraction glow ABOVE the bar (negative y) before anything else
        drawRefractionGlow(canvas);

        // draw bar background manually (rounded capsule)
        canvas.drawRoundRect(barRect, barCornerRadius, barCornerRadius, barBgPaint);

        // draw glass pill
        drawGlassPill(canvas);

        // draw children (icons + labels) on top
        super.dispatchDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // handled in draw() so skipping default to avoid double drawing children
    }

    private void drawRefractionGlow(Canvas canvas) {
        if (tabWidth == 0) return;

        float glowRadius = tabWidth * 0.7f;
        float glowCenterY = -glowRadius * 0.25f;

        // radial gradient: bright white center fading to transparent
        RadialGradient gradient = new RadialGradient(
                pillCenterX, glowCenterY,
                glowRadius,
                new int[]{
                        Color.argb(30, 255, 255, 255),
                        Color.argb(18, 255, 255, 255),
                        Color.argb(0, 255, 255, 255)
                },
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
        );
        refractionPaint.setShader(gradient);

        canvas.drawOval(
                pillCenterX - glowRadius,
                glowCenterY - glowRadius * 0.8f,
                pillCenterX + glowRadius,
                glowCenterY + glowRadius * 0.8f,
                refractionPaint
        );

        // secondary caustic with a tighter, brighter spot closer to the bar
        float causticRadius = tabWidth * 0.35f;
        float causticY = -causticRadius * 0.3f;
        RadialGradient caustic = new RadialGradient(
                pillCenterX, causticY,
                causticRadius,
                new int[]{
                        Color.argb(22, 255, 255, 255),
                        Color.argb(0, 255, 255, 255)
                },
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
        );
        refractionPaint.setShader(caustic);
        canvas.drawOval(
                pillCenterX - causticRadius,
                causticY - causticRadius * 0.5f,
                pillCenterX + causticRadius,
                causticY + causticRadius * 0.5f,
                refractionPaint
        );
    }

    private void drawGlassPill(Canvas canvas) {
        float halfW = tabWidth / 2f - pillHPad;
        pillRect.set(
                pillCenterX - halfW, pillVPad,
                pillCenterX + halfW, getHeight() - pillVPad);

        // soft shadow underneath the capsule
        float shadowOffset = 2 * density;
        RectF shadowRect = new RectF(pillRect);
        shadowRect.offset(0, shadowOffset);
        canvas.drawRoundRect(shadowRect, pillCornerRadius, pillCornerRadius, pillShadowPaint);

        // main white capsule
        canvas.drawRoundRect(pillRect, pillCornerRadius, pillCornerRadius, pillFillPaint);

        // top highlight for glass refraction shine on the capsule itself
        highlightRect.set(
                pillRect.left + 10 * density, pillRect.top + 2 * density,
                pillRect.right - 10 * density, pillRect.top + pillRect.height() * 0.32f);
        canvas.drawRoundRect(highlightRect, pillCornerRadius / 2, pillCornerRadius / 2, pillHighlightPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownPillCenterX = pillCenterX;
                isDragging = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - touchDownX;
                if (Math.abs(dx) > DRAG_THRESHOLD) {
                    isDragging = true;
                }
                if (isDragging) {
                    float minX = tabWidth / 2f;
                    float maxX = getWidth() - tabWidth / 2f;
                    pillCenterX = Math.max(minX, Math.min(maxX, touchDownPillCenterX + dx));
                    updateTabAppearance();
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    int nearest = Math.round((pillCenterX - tabWidth / 2f) / tabWidth);
                    nearest = Math.max(0, Math.min(TAB_COUNT - 1, nearest));
                    animateToTab(nearest);
                } else {
                    int tapped = (int) (event.getX() / tabWidth);
                    tapped = Math.max(0, Math.min(TAB_COUNT - 1, tapped));
                    animateToTab(tapped);
                }
                isDragging = false;
                return true;
        }
        return super.onTouchEvent(event);
    }
}
