package com.evan.androidopengl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.evan.androidopengl.render.GLView;

public class GLActivity extends AppCompatActivity {
    private static final String Tag = "GLActivity";
    private GLView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gl);
        try {
            String className = getIntent().getStringExtra("className");
            Class clazz = Class.forName(className);

            Log.i(Tag, "onCreate: " + className);
            mGLView = findViewById(R.id.gl_view);
            mGLView.setRenderClass(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}
