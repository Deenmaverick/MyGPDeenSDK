package com.deenislamic.sdk.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class  JustifiedTextView extends AppCompatTextView {

    public JustifiedTextView(Context context) {
        super(context);
    }

    public JustifiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JustifiedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getText() instanceof Spannable) {
            Spannable spannable = (Spannable) getText();
            SpannableStringBuilder builder = new SpannableStringBuilder(spannable);
            builder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(builder);
        }
        super.onDraw(canvas);
    }
}



