package com.evan.androidopengl.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;


/**
 * Created by Evan on 2018/1/25.
 */

public abstract class GLRender implements GLSurfaceView.Renderer {
    private static final String Tag = "GLRender";
    protected GLSurfaceView mView;
    protected int mRenderMode;

    public GLRender(GLSurfaceView view) {
        this.mRenderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY;
        this.mView = view;
    }

    // 子类按照需要实现
    public void onResume() { }

    // 子类按照需要实现
    public void onPause() { }
}
