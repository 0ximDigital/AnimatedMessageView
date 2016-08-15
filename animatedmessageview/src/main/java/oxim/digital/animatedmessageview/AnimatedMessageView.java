package oxim.digital.animatedmessageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public final class AnimatedMessageView extends FrameLayout {

    public static final float DEFAULT_ICON_PADDING = 4.f;
    public static final float DEFAULT_MESSAGE_TEXT_SIZE = 16.f;

    private final Paint backgroundPaint;

    private CircleIconView circleIconView;
    private TextView messageTextView;

    private Choreographer choreographer;

    private
    @DrawableRes
    int icon;
    private String message = "";

    private float iconPadding = DEFAULT_ICON_PADDING;
    private boolean cropIconToCircle = true;
    private float messageTextSize = DEFAULT_MESSAGE_TEXT_SIZE;
    private int messageTextColor;

    public AnimatedMessageView(final Context context) {
        this(context, null);
    }

    public AnimatedMessageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedMessageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.animated_message_view_layout, this);

        this.backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.backgroundPaint.setColor(Color.BLACK);

        if (attrs != null) {
            parseAttributes(attrs, defStyleAttr);
        }

        this.choreographer = new DefaultChoreographer(this, backgroundPaint);

        this.circleIconView = (CircleIconView) findViewById(R.id.icon_view);
        this.circleIconView.setIcon(BitmapFactory.decodeResource(getResources(), icon));
        this.circleIconView.cropImageToCircle(cropIconToCircle);
        this.circleIconView.setIconPadding((int) iconPadding);

        this.messageTextView = (TextView) findViewById(R.id.message_view);
        this.messageTextView.setTextColor(messageTextColor);
        this.messageTextView.setTextSize(messageTextSize);
        this.messageTextView.setText(message);
        this.messageTextView.setPadding(0, 0, (int) iconPadding * 2, 0);
    }

    private void parseAttributes(final AttributeSet attrs, final int defStyleAttrs) {
        final TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AnimatedMessageView, defStyleAttrs, 0);
        try {
            final String message = typedArray.getString(R.styleable.AnimatedMessageView_message);
            this.message = (message != null) ? message : "";
            this.icon = typedArray.getResourceId(R.styleable.AnimatedMessageView_viewIcon, 0);
            this.backgroundPaint.setColor(typedArray.getColor(R.styleable.AnimatedMessageView_backgroundColor, Color.BLACK));
            this.iconPadding = typedArray.getDimension(R.styleable.AnimatedMessageView_iconPadding, DEFAULT_ICON_PADDING);
            this.cropIconToCircle = typedArray.getBoolean(R.styleable.AnimatedMessageView_cropIconToCircle, true);
            this.messageTextSize = typedArray.getDimension(R.styleable.AnimatedMessageView_messageTextSize, DEFAULT_MESSAGE_TEXT_SIZE);
            this.messageTextColor = typedArray.getColor(R.styleable.AnimatedMessageView_messageTextColor, Color.WHITE);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int optimalWidth = getPaddingLeft() + circleIconView.getWidth() + messageTextView.getWidth() + getPaddingRight();
        final int optimalHeight = getPaddingTop() + Math.max(circleIconView.getHeight(), messageTextView.getHeight()) + getPaddingBottom();

        // TODO - height measuring has to be fixed
        final int width = resolveSizeForMeasurement(widthMeasureSpec, optimalWidth);
        final int height = resolveSizeForMeasurement(heightMeasureSpec, optimalHeight);

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
    protected void dispatchDraw(final Canvas canvas) {
        choreographer.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public void show() {
        choreographer.show();
    }

    public void hide() {
        choreographer.hide();
    }
}
