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

        ArrayList<Impl> classes = new ArrayList<>();
        classes.add(new Impl(getString(R.string.blurview_predraw_webview), WebViewPreDrawBlurActivity.class));
        classes.add(new Impl(getString(R.string.blurview_layoutchange_webview), WebViewLayoutChangeBlurActivity.class));
        classes.add(new Impl(getString(R.string.blurview_layoutchange_imageview), ImageViewLayoutChangeBlurActivity.class));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            classes.add(new Impl(getString(R.string.view_simple_rendereffect), RawWebViewPreDrawBlurActivity.class));
        }

        for (Impl impl : classes) {
            boolean hasChild = content.getChildCount() > 0;

            Button btn = new Button(this);
            btn.setText(impl.name);
            btn.setTag(impl.className);
            btn.setAllCaps(false);
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

    private static class Impl {
        protected String name;
        protected Class<? extends Activity> className;

        protected Impl(String name, Class<? extends Activity> className) {
            this.name = name;
            this.className = className;
        }
    }

}
