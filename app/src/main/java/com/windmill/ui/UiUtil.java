package com.windmill.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

public final class UiUtil {

    private UiUtil() {
    }

    @Dimension(unit = Dimension.DP)
    public static int getDpValue(@NonNull Context ctx, @Px int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, ctx.getResources().getDisplayMetrics());
    }

}
