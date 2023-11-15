package com.windmill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.windmill.ui.UiUtil;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout content = new LinearLayout(this);
        content.setGravity(Gravity.CENTER);
        content.setOrientation(LinearLayout.VERTICAL);

        View.OnClickListener activityStartListener = v -> {
            try {
                startActivity(new Intent(MainActivity.this, (Class<?>) v.getTag()));
            } catch (Exception ignored) {
            }
        };

        ArrayList<Class<? extends Activity>> classes = new ArrayList<>();
        classes.add(PreDrawBlurActivity.class);
        classes.add(LayoutChangeBlurActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            classes.add(RawRenderEffectBlurBlurActivity.class);
        }

        for (Class<? extends Activity> cls : classes) {
            boolean hasChild = content.getChildCount() > 0;
            Button btn = new Button(this);
            btn.setText(cls.getSimpleName());
            btn.setTag(cls);
            btn.setOnClickListener(activityStartListener);
            content.addView(btn);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (hasChild) {
                layoutParams.topMargin = UiUtil.getDpValue(this, 12);
            }
        }

        setContentView(content);
    }

}
