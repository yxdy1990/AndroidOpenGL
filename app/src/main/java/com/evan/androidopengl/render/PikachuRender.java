package com.evan.androidopengl.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.evan.androidopengl.obj3d.Obj3D;
import com.evan.androidopengl.obj3d.ObjFilter;
import com.evan.androidopengl.obj3d.ObjReader;
import com.evan.androidopengl.utils.Gl2Utils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Evan on 2018/1/27.
 */

public class PikachuRender extends GLRender {
    private static final String Tag = "PikachuRender";
    private List<ObjFilter> filters;

    public PikachuRender(GLSurfaceView view) {
        super(view);

        Log.i(Tag, "PikachuRender constructor called.");

        List<Obj3D> model = ObjReader.readMultiObj(mView.getContext(), "assets/pikachu/pikachu.obj");

        filters = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            ObjFilter f = new ObjFilter(mView.getContext().getResources());
            f.setObj3D(model.get(i));
            filters.add(f);
        }
        Log.i(Tag, "3D Obj Count of Pikachu: " + filters.size());
        mRenderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.i(Tag, "onSurfaceCreated.");

        for (ObjFilter filter : filters) {
            filter.create();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.i(Tag, "onSurfaceChanged.");

        for (ObjFilter filter : filters) {
            filter.setSize(width, height);

            float[] matrix = Gl2Utils.getOriginalMatrix();
            Matrix.translateM(matrix, 0, 0, -0.3f, 0);
            Matrix.scaleM(matrix, 0, 0.008f, 0.008f * width / height, 0.008f);
            filter.setMatrix(matrix);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //Log.i(Tag, "onDrawFrame.");
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        for (ObjFilter filter : filters) {
            Matrix.rotateM(filter.getMatrix(), 0, 1.0f, 0, 1, 0);
            filter.draw();
        }
    }
}
