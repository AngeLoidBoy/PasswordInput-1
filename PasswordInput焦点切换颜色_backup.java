package com.ethanco.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import com.ethanco.lib.utils.DisplayUtil;

/**
 * Created by EthanCo on 2016/7/25.
 */
public class PasswordInput extends EditText {

    private static final String TAG = "Z-SimplePasswordInput";

    //============================= Z-�߿� ==============================/
    @ColorInt
    private int borderNotFocusedColor = Color.BLACK; //�߿�δѡ��ʱ����ɫ
    @ColorInt
    private int borderFocusedColor = Color.BLUE; //�߿�ѡ��ʱ����ɫ
    private int borderWidth; //�߿���


    //============================= Z-Բ�� ==============================/
    @ColorInt
    private int dotNotFocusedColor = Color.BLACK; //Բ��δѡ��ʱ����ɫ
    @ColorInt
    private int dotFocusedColor = Color.BLUE; //Բ��ѡ��ʱ����ɫ
    private float dotRadius; //Բ��뾶

    //============================= Z-���� ==============================/
    @ColorInt
    private int backgroundColor = Color.WHITE; //����ɫ

    //============================= Z-���� ==============================/
    private Paint mBorderPaint; //�߿򻭱�
    private Paint mDotPaint; //Բ�㻭��
    private Paint mBackgroundPaint; //��������

    //============================= Z-���� ==============================/
    private int boxCount = 4; //�ַ����������
    private float boxMarge; //�ַ������marge
    private float boxRadius; //�ַ�����ı߽�Բ��
    private float[] scans;  //�ַ��������ű�������
    private int[] alphas;   //�ַ�����͸��������

    //============================= Z-���� ==============================/
    private int currTextLen = 0; //��������Text����
    private boolean focusColorChangeEnable = true; //��ý���ʱ��ɫ�Ƿ�ı�
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];

    public PasswordInput(Context context) {
        this(context, null);

    }

    public PasswordInput(Context context, AttributeSet attrs) {
        super(context, attrs);

        //��ȡϵͳ����
        getSystemVar();
        //��ʼ���Զ�������
        initAttrVar(context, attrs);
        //��ʼ�������洢����
        initAnimArr();
        //��ʼ������
        initPaint();
        //��ʼ��EditText
        initView();
    }

    private void getSystemVar() {
        //TODO How to get maxLength ?
        //TypedArray taSystem = context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.TextView);
        //maxLength = taSystem.getInt(com.android.internal.R.styleable.TextView_maxLength, maxLength);
        //taSystem.recycle();
    }

    private void initAttrVar(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PasswordInput);
        backgroundColor = ta.getColor(R.styleable.PasswordInput_backgroundColor, backgroundColor);
        int focusedColor = ta.getColor(R.styleable.PasswordInput_focusedColor, Color.BLACK);
        int notFocusedColor = ta.getColor(R.styleable.PasswordInput_notFocusedColor, Color.BLUE);
        boxCount = ta.getInt(R.styleable.PasswordInput_boxCount, boxCount);
        focusColorChangeEnable = ta.getBoolean(R.styleable.PasswordInput_focusColorChangeEnable, true);
        dotRadius = ta.getDimension(R.styleable.PasswordInput_dotRaduis, DisplayUtil.dp2px(context, 11));
        ta.recycle();

        borderFocusedColor = focusedColor;
        borderNotFocusedColor = notFocusedColor;
        dotFocusedColor = focusedColor;
        dotNotFocusedColor = notFocusedColor;

        borderWidth = DisplayUtil.dp2px(context, 1);
        boxRadius = DisplayUtil.dp2px(context, 3);
        boxMarge = DisplayUtil.dp2px(context, 3);
    }

    private void initAnimArr() {
        scans = new float[boxCount];
        alphas = new int[boxCount];
        for (int i = 0; i < alphas.length; i++) {
            alphas[i] = 255;
        }
    }

    private void initPaint() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderPaint.setColor(borderNotFocusedColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setColor(dotNotFocusedColor);
        mDotPaint.setStyle(Paint.Style.FILL);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(backgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    private void initView() {
        setCursorVisible(false); //��겻�ɼ�
        setInputType(InputType.TYPE_CLASS_NUMBER); //���������������
        //����������󳤶�
        setMaxLen(boxCount);
    }

    private void setMaxLen(int maxLength) {
        if (maxLength >= 0) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            setFilters(NO_FILTERS);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();

        canvas.save();

        //���Ʊ���
        drawBackGround(canvas, height, width);
        //���Ʊ߿�
        drawBorder(canvas, height, width);
        //����Բ��
        drawDot(canvas, height, width);

        canvas.restore();
    }

    private void drawBackGround(Canvas canvas, int height, int width) {
        canvas.drawRect(0, 0, width, height, mBackgroundPaint);
    }

    private void drawBorder(Canvas canvas, int height, int width) {
        for (int i = 0; i < boxCount; i++) {
            RectF rect = generationSquareBoxRectF(height, width, i);
            canvas.drawRoundRect(rect, boxRadius, boxRadius, mBorderPaint);
        }
    }

    private void drawDot(Canvas canvas, int height, int width) {
        float cx, cy = height / 2;
        float half = width / boxCount / 2;
        for (int i = 0; i < boxCount; i++) {
            mDotPaint.setAlpha(alphas[i]);
            cx = width * i / boxCount + half;
            Log.i(TAG, "onDraw scans[" + i + "]: " + scans[i]);
            canvas.drawCircle(cx, cy, dotRadius * scans[i], mDotPaint);
        }
    }

    @NonNull
    private RectF generationSquareBoxRectF(int height, int width, int i) {
        float boxWidth = (width / boxCount);
        float boxHeight = height;
        float left = boxMarge + boxWidth * i;
        float right = boxWidth * (i + 1) - boxMarge;
        float top = boxMarge;
        float bottom = boxHeight - boxMarge;

        float min = Math.min(boxWidth, boxHeight);

        float dw = (boxWidth - min) / 2F;
        float dh = (boxHeight - min) / 2F;
        left += dw;
        right -= dw;
        top += dh;
        bottom -= dh;

        return new RectF(left, top, right, bottom);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (null == scans) return;

        this.currTextLen = text.toString().length();
        final boolean isAdd = lengthAfter - lengthBefore > 0 ? true : false;

        //��ʼTextCahnged����
        startTextChangedAnim(isAdd);
        //֪ͨTextChangeListen
        notifyTextChangeListen(text);
    }

    /**
     * ��ʼTextChanged����
     *
     * @param isAdd true:�ַ���С���� (����) false:�ַ��Ӵ�С (ɾ��)
     */
    private void startTextChangedAnim(boolean isAdd) {
        final ValueAnimator scanAnim;
        final ValueAnimator alphaAnim;
        final int index;
        if (isAdd) {
            index = currTextLen - 1;
            scanAnim = ValueAnimator.ofFloat(0F, 1F);
            alphaAnim = ValueAnimator.ofInt(0, 255);
        } else {
            index = currTextLen;
            scanAnim = ValueAnimator.ofFloat(1F, 0F);
            alphaAnim = ValueAnimator.ofInt(255, 0);
        }

        if (scans.length >= currTextLen) {

            scanAnim.setDuration(750);
            scanAnim.setRepeatCount(0);
            scanAnim.setInterpolator(new OvershootInterpolator());
            scanAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float scale = (float) valueAnimator.getAnimatedValue();
                    scans[index] = scale;
                    postInvalidate();
                }
            });

            alphaAnim.setDuration(750);
            alphaAnim.setRepeatCount(0);
            alphaAnim.setInterpolator(new LinearInterpolator());
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int alpha = (int) valueAnimator.getAnimatedValue();
                    alphas[index] = alpha;
                    postInvalidate();
                }
            });

            scanAnim.start();
            alphaAnim.start();
        }
    }

    private void notifyTextChangeListen(CharSequence text) {
        if (null != textLenChangeListen) {
            textLenChangeListen.onTextLenChange(text, currTextLen);
        }
    }

    @Override
    protected void onFocusChanged(final boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focusColorChangeEnable) {
            startFocusChangedAnim(focused);
        }
    }

    /**
     * ��ʼFocusChanged����
     *
     * @param focused
     */
    private void startFocusChangedAnim(final boolean focused) {
        final ValueAnimator scanAnim;

        scanAnim = ValueAnimator.ofFloat(1F, 0.1F, 1F);

        scanAnim.setDuration(750);
        scanAnim.setRepeatCount(0);
        scanAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scanAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = (float) valueAnimator.getAnimatedValue();
                for (int i = 0; i < scans.length; i++) {
                    if (scans[i] != 0) {
                        scans[i] = scale;
                    }
                }
                if (scale <= 0.15) {
                    if (focused) {
                        mBorderPaint.setColor(borderFocusedColor);
                        mDotPaint.setColor(dotFocusedColor);
                    } else {
                        mBorderPaint.setColor(borderNotFocusedColor);
                        mDotPaint.setColor(dotNotFocusedColor);
                    }
                }
                postInvalidate();
            }
        });
        scanAnim.start();
    }

    public interface TextLenChangeListen {
        void onTextLenChange(CharSequence text, int len);
    }

    private TextLenChangeListen textLenChangeListen;

    /**
     * ����Text���ȸı����
     *
     * @param lenListen
     */
    public void setTextLenChangeListen(TextLenChangeListen lenListen) {
        textLenChangeListen = lenListen;
    }
}
