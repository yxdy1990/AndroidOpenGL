package com.evan.androidopengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;


/**
 * Created by Evan on 2018/1/25.
 */

public class GLView extends GLSurfaceView {
    public static String Tag = "GLView";
    private Class<? extends GLRender> clazz;
    private GLRender render;
    private int renderMode;

    public GLView(Context context) {
        this(context, null);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(Tag, "GLView Constructor called.");
        this.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY;
    }

    public void setRenderClassAndMode(Class<? extends GLRender> clazz, int renderMode) {
        this.clazz = clazz;
        if (renderMode >= 0) {
            this.renderMode = renderMode;
        }
        initGLRender();
    }

    private void initGLRender() {
        Log.i(Tag, "initGLRender called, class: " + clazz.getName());
        try {
            Constructor constructor = clazz.getDeclaredConstructor(GLSurfaceView.class);
            if (constructor != null) {
                constructor.setAccessible(true);
                render = (GLRender) constructor.newInstance(this);

                if (render != null) {
                    setEGLContextClientVersion(2);
                    setRenderer(render);
                    setRenderMode(renderMode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (render != null) {
            render.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (render != null) {
            render.onPause();
        }
    }
}
