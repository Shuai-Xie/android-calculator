package com.example.eric.mycal;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Eric
 * on 2016/11/9.
 */

public class AutoScaleTextView extends TextView {
    private static float DEFAULT_MIN_TEXTSIZE = 10;
    private static float DEFAULT_MAX_TEXTSIZE = 20;

    //Attributes
    private Paint testPaint;
    private float minTextSize;
    private float maxTextSize;

    public AutoScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        testPaint = new Paint();
        testPaint.set(this.getPaint());

        maxTextSize = this.getTextSize();
        if (maxTextSize <= DEFAULT_MIN_TEXTSIZE) {
            maxTextSize = DEFAULT_MAX_TEXTSIZE;
        }
        minTextSize = DEFAULT_MIN_TEXTSIZE;
    }

    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            int availableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
            float trySize = maxTextSize;
            testPaint.setTextSize(trySize);

            //调试代码1
            //System.out.println("testPaint.measureText(text)=" + testPaint.measureText(text) * 3);
            //System.out.println("availableWidth=" + availableWidth);
            while ((trySize > minTextSize) && (testPaint.measureText(text) * 3 > availableWidth)) {
                trySize -= 3;
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                testPaint.setTextSize(trySize);
            }
            this.setTextSize(trySize);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        refitText(text.toString(), this.getWidth());

        //调试代码1
        //System.out.println("this.getWidth()=" + this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }
}
