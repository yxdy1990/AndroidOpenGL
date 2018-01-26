package com.evan.androidopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.evan.androidopengl.render.AccGraphRender;
import com.evan.androidopengl.render.GLView;

public class GLActivity extends AppCompatActivity {
    private GLView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gl);

        mGLView = findViewById(R.id.gl_view);
        mGLView.setRenderClassAndMode(AccGraphRender.class, -1);
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
