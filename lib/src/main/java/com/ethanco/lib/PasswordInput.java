package com.ethanco.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * @Description 密码输入框
 * Created by EthanCo on 2016/7/4.
 */
public class PasswordInput extends EditText {
    private int backgroundColor;
    private float boxRadius;
    private int boxBorderColor;
    private int passwordColor;
    private int passwordLen = 6;
    private float boxMarge = 10;
    private float boxBorderWidth = 2;
    private float passwordWidth = 10;

    private int textLength;

    private Paint backgroundPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint passwordPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(ANTI_ALIAS_FLAG);

    public PasswordInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        boxBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, boxBorderWidth, dm);
        passwordWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, passwordWidth, dm);
        boxMarge = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, boxMarge, dm);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordInput, 0, 0);
        backgroundColor = a.getColor(R.styleable.PasswordInput_myBackground, Color.WHITE);
        boxRadius = a.getDimension(R.styleable.PasswordInput_myBoxRadius, 0);
        boxBorderColor = a.getColor(R.styleable.PasswordInput_myBoxBorderColor, Color.GRAY);
        boxBorderWidth = a.getDimension(R.styleable.PasswordInput_myBoxBorderWidth, boxBorderWidth);
        boxMarge = a.getDimension(R.styleable.PasswordInput_myBoxMarge, boxMarge);
        passwordColor = a.getColor(R.styleable.PasswordInput_myPasswordColor, Color.GRAY);
        passwordWidth = a.getDimension(R.styleable.PasswordInput_myPasswordWidth, passwordWidth);
        passwordLen = a.getInt(R.styleable.PasswordInput_myPasswordLength, passwordLen);
        a.recycle();

        backgroundPaint.setColor(backgroundColor);
        borderPaint.setStrokeWidth(boxBorderWidth);
        borderPaint.setColor(boxBorderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        passwordPaint.setStrokeWidth(passwordWidth);
        passwordPaint.setStyle(Paint.Style.FILL);
        passwordPaint.setColor(passwordColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 背景
        RectF rectBg = new RectF(0, 0, width, height);
        backgroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectBg, 0, 0, backgroundPaint);

        for (int i = 0; i < passwordLen; i++) {
            float boxWidth = (width / passwordLen);
            float boxHeight = height;
            float left = boxMarge + boxWidth * i;
            float right = boxWidth * (i + 1) - boxMarge;
            float top = boxMarge;
            float bottom = boxHeight - boxMarge;
            RectF rect = new RectF(left, top, right, bottom);
            borderPaint.setColor(boxBorderColor);
            canvas.drawRoundRect(rect, boxRadius, boxRadius, borderPaint);
        }

        // 密码
        float cx, cy = height / 2;
        float half = width / passwordLen / 2;
        for (int i = 0; i < textLength; i++) {
            cx = width * i / passwordLen + half;
            canvas.drawCircle(cx, cy, passwordWidth, passwordPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.textLength = text.toString().length();
        invalidate();

        if (null != textLenChangeListen) {
            textLenChangeListen.onTextLenChange(text, textLength);
        }
    }

    public interface TextLenChangeListen {
        void onTextLenChange(CharSequence text, int len);
    }

    private TextLenChangeListen textLenChangeListen;

    public void setTextLenChangeListen(TextLenChangeListen lenListen) {
        textLenChangeListen = lenListen;
    }
}