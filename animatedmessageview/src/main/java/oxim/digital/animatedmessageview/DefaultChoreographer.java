package oxim.digital.animatedmessageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;

public final class DefaultChoreographer extends Choreographer {

    public static final int DURATION = 500;

    private final Paint backgroundPaint;

    private int halfHeight;
    private int halfWidth;

    private int fullStrokeWidth;
    private int strokeWidth;
    private int lineLeftX;
    private int lineRightX;

    private ValueAnimator initialRevealAnimator;
    private ValueAnimator horizontalRevealAnimator;

    private final InitialRevealAnimatorListener initialRevealAnimatorListener = new InitialRevealAnimatorListener();
    private final HorizontalRevealAnimatorListener horizontalRevealAnimatorListener = new HorizontalRevealAnimatorListener();

    public DefaultChoreographer(@NonNull final AnimatedMessageView animatedMessageView, final Paint backgroundPaint) {
        super(animatedMessageView);
        this.backgroundPaint = backgroundPaint;
        this.backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void show() {
        initialRevealAnimator = ValueAnimator.ofInt(0, halfHeight);
        initialRevealAnimator.addUpdateListener(initialRevealAnimatorListener);
        initialRevealAnimator.addListener(initialRevealAnimatorListener);
        initialRevealAnimator.setInterpolator(new DecelerateInterpolator());
        initialRevealAnimator.setDuration(DURATION);
        initialRevealAnimator.start();

        horizontalRevealAnimator = ValueAnimator.ofInt(0, halfWidth - fullStrokeWidth);
        horizontalRevealAnimator.addUpdateListener(horizontalRevealAnimatorListener);
        horizontalRevealAnimator.setDuration(DURATION);
        horizontalRevealAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void hide() {
        // TODO
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        backgroundPaint.setStrokeWidth(strokeWidth);

        canvas.drawLine(lineLeftX, halfHeight, lineRightX, halfHeight, backgroundPaint);
    }

    @Override
    protected void onSizeChanged(final int newWidth, final int newHeight, final int oldWidth, final int oldHeight) {
        this.halfHeight = newHeight / 2;
        this.halfWidth = newWidth / 2;
        this.lineLeftX = halfWidth;
        this.lineRightX = halfWidth + 1;
        this.fullStrokeWidth = halfHeight;
    }

    private final class InitialRevealAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            strokeWidth = (int) animation.getAnimatedValue();
            final AnimatedMessageView view = animatedMessageViewWeakReference.get();
            if (view != null) {
                view.invalidate();
            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            horizontalRevealAnimator.start();
        }
    }

    private final class HorizontalRevealAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            final int animatedValue = (int) animation.getAnimatedValue();
            lineLeftX = halfWidth - animatedValue;
            lineRightX = halfWidth + animatedValue;
            if (lineLeftX == lineRightX) {
                lineRightX = lineRightX + 1;
            }
            final AnimatedMessageView view = animatedMessageViewWeakReference.get();
            if (view != null) {
                view.invalidate();
            }
        }
    }
}
