package oxim.digital.animatedmessageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public final class CircleIconView extends View {

    private final Paint fillPaint;
    private final Paint transparentPaint;

    private final PorterDuffXfermode iconMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private final PorterDuffXfermode clearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private Rect maskSrcRect = new Rect();
    private Rect maskDestRect = new Rect();

    private Rect iconSrcRect = new Rect();
    private Rect iconDestRect = new Rect();

    private Bitmap icon = null;
    private Bitmap maskBitmap;

    private boolean cropImage = true;
    private int iconPadding;

    public CircleIconView(final Context context) {
        this(context, null);
    }

    public CircleIconView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIconView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        this.fillPaint.setColor(Color.BLACK);
        this.fillPaint.setStyle(Paint.Style.FILL);

        this.transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.transparentPaint.setColor(Color.TRANSPARENT);
        this.transparentPaint.setStyle(Paint.Style.FILL);
        this.transparentPaint.setXfermode(clearMode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        if (icon != null) {
            canvas.drawBitmap(icon, iconSrcRect, iconDestRect, fillPaint);
            canvas.drawBitmap(icon, iconSrcRect, iconDestRect, null);
        }

        if (cropImage) {
            fillPaint.setXfermode(iconMode);
            canvas.drawBitmap(maskBitmap, maskSrcRect, maskDestRect, fillPaint);
        }

        canvas.restoreToCount(saveCount);
        fillPaint.setXfermode(null);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int smallerSize = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(smallerSize, smallerSize);
    }

    @Override
    protected void onSizeChanged(final int newWidth, final int newHeight, final int oldWidth, final int oldHeight) {
        calculateIconSrcRect();
        iconDestRect.set(iconPadding, iconPadding, newWidth - iconPadding, newHeight - iconPadding);

        createMaskBitmap(newHeight);

        maskSrcRect.set(0, 0, maskBitmap.getWidth(), maskBitmap.getHeight());
        maskDestRect.set(iconDestRect);
    }

    private void createMaskBitmap(int size) {
        maskBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final int radius = size / 2;
        final Canvas canvas = new Canvas(maskBitmap);
        canvas.drawCircle(radius, radius, radius, fillPaint);
    }

    public void setIcon(@Nullable final Bitmap icon) {
        if (icon == null) {
            return;
        }
        this.icon = Bitmap.createBitmap(icon);
        calculateIconSrcRect();
        invalidate();
    }

    public void cropImageToCircle(final boolean cropImage) {
        this.cropImage = cropImage;
    }

    private void calculateIconSrcRect() {
        if (icon != null) {
            iconSrcRect.set(0, 0, icon.getWidth(), icon.getHeight());
        }
    }

    public void setIconPadding(final int iconPadding) {
        this.iconPadding = iconPadding;
    }
}
