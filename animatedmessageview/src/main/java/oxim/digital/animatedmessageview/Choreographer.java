package oxim.digital.animatedmessageview;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public abstract class Choreographer {

    protected final WeakReference<AnimatedMessageView> animatedMessageViewWeakReference;

    protected final TextView messageView;
    protected final CircleIconView iconView;

    public Choreographer(@NonNull final AnimatedMessageView animatedMessageView) {
        this.animatedMessageViewWeakReference = new WeakReference<>(animatedMessageView);

        this.iconView = (CircleIconView) animatedMessageView.findViewById(R.id.icon_view);
        this.messageView = (TextView) animatedMessageView.findViewById(R.id.message_view);
    }

    protected abstract void show();

    protected abstract void hide();

    protected abstract void onDraw(final Canvas canvas);

    protected abstract void onSizeChanged(final int newWidth, final int newHeight, final int oldWidth, final int oldHeight);
}
