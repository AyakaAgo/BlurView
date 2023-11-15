package com.windmill.blur;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * LayoutChangeHelper
 * <p>
 * blur is only changed after view layout
 */
public class LayoutChangeHelper extends PreDrawHelper {
    private boolean needRefresh;

    /**
     * @param blurView View which will draw it's blurred underlying content
     * @param target   Root View where blurView's underlying content starts drawing.
     *                 Can be Activity's root content layout (android.R.id.content)
     * @param impl     sets the blur impl
     */
    public LayoutChangeHelper(@NonNull View blurView, @NonNull View target, @NonNull BlurImpl impl) {
        super(blurView, target, impl);
        target.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> needRefresh = true);
    }

    @Override
    protected void perform() {
        if (needRefresh) {
            needRefresh = false;

            //super.perform();
            target.post(super::perform);
        }
    }

}
