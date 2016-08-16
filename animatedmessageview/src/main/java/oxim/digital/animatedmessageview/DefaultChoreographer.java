package oxim.digital.animatedmessageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public final class DefaultChoreographer extends Choreographer {

    public static final int DURATION = 600;
    public static final int DEFAULT_X_OFFSET = 80;

    public static final float TRANSPARENT = 0.0f;
    public static final float OPAQUE = 1.0f;

    private final Paint backgroundPaint;

    private int halfHeight;
    private int halfWidth;

    private int strokeWidth;
    private int lineLeftX;
    private int lineRightX;

    private ValueAnimator initialRevealAnimator;
    private ValueAnimator horizontalRevealAnimator;

    private final InitialRevealAnimatorListener initialRevealAnimatorListener = new InitialRevealAnimatorListener();
    private final HorizontalRevealAnimatorListener horizontalRevealAnimatorListener = new HorizontalRevealAnimatorListener();

    private final ReverseInitialRevealAnimatorListener reverseInitialRevealAnimatorListener = new ReverseInitialRevealAnimatorListener();
    private final ReverseHorizontalRevealAnimatorListener reverseHorizontalRevealAnimatorListener = new ReverseHorizontalRevealAnimatorListener();

    private boolean isShown;

    public DefaultChoreographer(@NonNull final AnimatedMessageView animatedMessageView, final Paint backgroundPaint) {
        super(animatedMessageView);
        this.backgroundPaint = backgroundPaint;
        this.backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        this.isShown = false;

        prepareViews();
    }

    private void prepareViews() {
        iconView.animate().xBy(DEFAULT_X_OFFSET).alpha(TRANSPARENT).setDuration(0).start();
        messageView.animate().xBy(-DEFAULT_X_OFFSET).alpha(TRANSPARENT).setDuration(0).start();
    }

    @Override
    protected void show() {
        if (isAnimating() || isShown) {
            return;
        }
        initialRevealAnimator = ValueAnimator.ofInt(0, halfHeight * 2);
        initialRevealAnimator.addUpdateListener(initialRevealAnimatorListener);
        initialRevealAnimator.addListener(initialRevealAnimatorListener);
        initialRevealAnimator.setInterpolator(new DecelerateInterpolator());
        initialRevealAnimator.setDuration(DURATION / 2);
        initialRevealAnimator.start();

        horizontalRevealAnimator = ValueAnimator.ofInt(0, halfWidth - halfHeight);
        horizontalRevealAnimator.addUpdateListener(horizontalRevealAnimatorListener);
        horizontalRevealAnimator.addListener(horizontalRevealAnimatorListener);
        horizontalRevealAnimator.setDuration(DURATION);
        horizontalRevealAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void hide() {
        if (isAnimating() || !isShown) {
            return;
        }
        horizontalRevealAnimator.removeAllListeners();
        horizontalRevealAnimator.removeAllUpdateListeners();
        horizontalRevealAnimator.addUpdateListener(reverseHorizontalRevealAnimatorListener);
        horizontalRevealAnimator.addListener(reverseHorizontalRevealAnimatorListener);
        horizontalRevealAnimator.setInterpolator(new AccelerateInterpolator());
        horizontalRevealAnimator.reverse();

        initialRevealAnimator.removeAllUpdateListeners();
        initialRevealAnimator.removeAllListeners();
        initialRevealAnimator.setInterpolator(new AccelerateInterpolator());
        initialRevealAnimator.addUpdateListener(reverseInitialRevealAnimatorListener);
        initialRevealAnimator.addListener(reverseInitialRevealAnimatorListener);
    }

    private boolean isAnimating() {
        return ((initialRevealAnimator != null && initialRevealAnimator.isRunning()) || (horizontalRevealAnimator != null && horizontalRevealAnimator.isRunning()));
    }

    @Override
    protected void draw(final Canvas canvas) {
        backgroundPaint.setStrokeWidth(strokeWidth);

        canvas.drawLine(lineLeftX, halfHeight, lineRightX, halfHeight, backgroundPaint);
    }

    @Override
    protected void onSizeChanged(final int newWidth, final int newHeight, final int oldWidth, final int oldHeight) {
        this.halfHeight = newHeight / 2;
        this.halfWidth = newWidth / 2;
        this.lineLeftX = halfWidth;
        this.lineRightX = halfWidth + 1;
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
            animateViews();
        }

        private void animateViews() {
            iconView.animate().setInterpolator(new DecelerateInterpolator()).alpha(OPAQUE).xBy(-DEFAULT_X_OFFSET).setDuration(DURATION).start();
            messageView.animate().setInterpolator(new DecelerateInterpolator()).alpha(OPAQUE).xBy(DEFAULT_X_OFFSET).setDuration(DURATION).start();
        }
    }

    private final class HorizontalRevealAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

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

        @Override
        public void onAnimationEnd(final Animator animation) {
            isShown = true;
        }
    }

    private class ReverseInitialRevealAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

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
            isShown = false;
        }
    }

    private class ReverseHorizontalRevealAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            final int animatedValue = (int) animation.getAnimatedValue();
            lineLeftX = halfWidth - animatedValue;
            lineRightX = halfWidth + animatedValue;
            if (lineLeftX == lineRightX) {
                lineRightX = lineLeftX + 1;
            }
            final AnimatedMessageView view = animatedMessageViewWeakReference.get();
            if (view != null) {
                view.invalidate();
            }
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            animateViews();
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            initialRevealAnimator.reverse();
        }

        private void animateViews() {
            iconView.animate().setInterpolator(new DecelerateInterpolator()).alpha(TRANSPARENT).xBy(DEFAULT_X_OFFSET).setDuration(DURATION).start();
            messageView.animate().setInterpolator(new DecelerateInterpolator()).alpha(TRANSPARENT).xBy(-DEFAULT_X_OFFSET).setDuration(DURATION).start();
        }
    }
}
