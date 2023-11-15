package com.windmill.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.Size;

public final class UiUtil {

    private UiUtil() {
    }

    @Dimension(unit = Dimension.DP)
    private static int getDpValue(@NonNull Resources res, @Px int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, res.getDisplayMetrics());
    }

    @Dimension(unit = Dimension.DP)
    public static int getDpValue(@NonNull Context ctx, @Px int size) {
        return getDpValue(ctx.getResources(), size);
    }

    @Dimension(unit = Dimension.DP)
    public static int getDpValue(@NonNull View view, @Px int size) {
        return getDpValue(view.getResources(), size);
    }

}
