package oxim.digital.animatedmessageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public final class AnimatedMessageView extends FrameLayout {

    private final Paint backgroundPaint;

    private Choreographer choreographer;

    private String message = "";
    private Drawable icon = null;

    public AnimatedMessageView(final Context context) {
        this(context, null);
    }

    public AnimatedMessageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedMessageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPaint.setColor(Color.BLACK);

        if (attrs != null) {
            parseAttributes(attrs, defStyleAttr);
        }
    }

    private void parseAttributes(final AttributeSet attrs, final int defStyleAttrs) {
        final TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AnimatedMessageView, defStyleAttrs, 0);
        try {
            final String message = typedArray.getString(R.styleable.AnimatedMessageView_message);
            this.message = (message != null) ? message : "";
            this.icon = typedArray.getDrawable(R.styleable.AnimatedMessageView_viewIcon);
            this.backgroundPaint.setColor(typedArray.getColor(R.styleable.AnimatedMessageView_backgroundColor, Color.BLACK));
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int smallerSize = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        final int width = resolveSizeForMeasurement(widthMeasureSpec, smallerSize);
        final int height = resolveSizeForMeasurement(heightMeasureSpec, smallerSize);

        setMeasuredDimension(width, height);
    }

    private int resolveSizeForMeasurement(final int sizeMeasureSpec, final int desiredSize) {
        final int sizeMode = MeasureSpec.getMode(sizeMeasureSpec);
        final int sizeSize = MeasureSpec.getSize(sizeMeasureSpec);
        if (sizeMode == MeasureSpec.EXACTLY) {
            return sizeSize;
        } else if (sizeMode == MeasureSpec.AT_MOST) {
            return Math.min(desiredSize, sizeSize);
        } else {
            return desiredSize;
        }
    }

    @Override
    protected void onSizeChanged(final int newWidth, final int newHeight, final int oldWidth, final int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        choreographer.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        choreographer.onDraw(canvas);
        super.onDraw(canvas);
    }
}
