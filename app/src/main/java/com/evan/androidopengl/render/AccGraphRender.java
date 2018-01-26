package com.evan.androidopengl.render;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.evan.androidopengl.utils.Gl2Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Evan on 2018/1/25.
 */

public class AccGraphRender extends GLRender {
    public static String Tag = "AccelerGraphRender";
    public static int SENSOR_HISTORY_LEN = 100;
    public static float SENSOR_FILTER = 0.1f;

    private SensorEventListener listener;
    private SensorManager sm;
    private Sensor sensor;

    private AccelerData[] sensorData;
    private FloatBuffer coordXBuffer;
    private FloatBuffer coordYBuffer;
    private FloatBuffer vertexBuffer;
    private int mProgram = 0;

    public AccGraphRender(GLSurfaceView view) {
        super(view);
        Log.i(Tag, "AccGraphRender constructor called.");

        sm = (SensorManager) mView.getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float xyz[] = event.values;
                // 获得加速器三个方位x,y,z的数值集合
                float x = (int) xyz[0];
                float y = (int) xyz[1];
                float z = (int) xyz[2];

                AccelerData newData = new AccelerData(x, y, z);
                for (int i = 0; i < SENSOR_HISTORY_LEN - 1; i++) {
                    sensorData[i] = sensorData[i + 1];
                }
                sensorData[SENSOR_HISTORY_LEN - 1] = newData;
                Log.i(Tag, "onSensorChanged: x " + newData.x + " y " + newData.y + " z " + newData.z);
                // 请求渲染
                mView.requestRender();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.i(Tag, "onAccuracyChanged: " + accuracy);
            }
        };
        sensorData = new AccelerData[SENSOR_HISTORY_LEN];
        generateCoordinatePos();
    }

    private void generateCoordinatePos() {
        ByteBuffer bb1 = ByteBuffer.allocateDirect(6 * 4);
        ByteBuffer bb2 = ByteBuffer.allocateDirect(6 * 4);

        bb1.order(ByteOrder.nativeOrder());
        bb2.order(ByteOrder.nativeOrder());
        coordXBuffer = bb1.asFloatBuffer();
        coordYBuffer = bb2.asFloatBuffer();

        coordXBuffer.put(new float[]{-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f});
        coordYBuffer.put(new float[]{0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f});
        coordXBuffer.position(0);
        coordYBuffer.position(0);
    }

    private void prepareRenderVertexData() {
        ByteBuffer bb = ByteBuffer.allocateDirect(SENSOR_HISTORY_LEN * 3 * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();

        for (int i = 0; i < SENSOR_HISTORY_LEN; i++) {
            float t = (float) i / (float) (SENSOR_HISTORY_LEN - 1);
            float x = -1.f * (1.f - t) + 1.f * t;
            AccelerData data = sensorData[i];
            float[] tempData;

            if (data != null) {
                tempData = new float[]{x, data.x, 0.0f, x, data.y, 0.0f, x, data.z, 0.0f};
            } else {
                tempData = new float[]{x, 0.0f, 0.0f, x, 0.0f, 0.0f, x, 0.0f, 0.0f};
            }
            vertexBuffer.put(tempData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Tag, "AccGraphRender onResume.");
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME); // 最后一个参数可设置灵敏度
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(Tag, "AccGraphRender onPause.");
        sm.unregisterListener(listener);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.i(Tag, "onSurfaceCreated.");
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        if (mProgram == 0) {
            mProgram = Gl2Utils.createGlProgramByRes(mView.getResources(), "graph_shader.glslv", "graph_shader.glslf");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.i(Tag, "onSurfaceChanged.");

        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.i(Tag, "onDrawFrame.");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glUseProgram(mProgram);

        int vPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int uFragColorHandle = GLES20.glGetUniformLocation(mProgram, "uFragColor");

        prepareRenderVertexData();

        GLES20.glEnableVertexAttribArray(vPositionHandle);
        // 坐标X轴
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 0, coordXBuffer);
        GLES20.glUniform4f(uFragColorHandle, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2);

        // 坐标Y轴
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 0, coordYBuffer);
        GLES20.glUniform4f(uFragColorHandle, 0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2);

        /* { (x1,X1,z1), (x1,Y1,z1), (x1,Z1,z1), (x2,X2,z2), (x2,Y2,z2), (x2,Z2,z2) } */
        /* stride表示间隔个数 * float类型字节数 */
        // X轴分量
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 9 * 4, vertexBuffer);
        GLES20.glUniform4f(uFragColorHandle, 1.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, SENSOR_HISTORY_LEN);

        // Y轴分量
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 9 * 4, vertexBuffer);
        GLES20.glUniform4f(uFragColorHandle, 1.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, SENSOR_HISTORY_LEN);

        // Z轴分量
        vertexBuffer.position(6);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 9 * 4, vertexBuffer);
        GLES20.glUniform4f(uFragColorHandle, 0.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, SENSOR_HISTORY_LEN);

        GLES20.glDisableVertexAttribArray(vPositionHandle);
    }

    private class AccelerData {
        private float x;
        private float y;
        private float z;

        public AccelerData(float x, float y, float z) {
            float a = SENSOR_FILTER;
            if (x != 0) {
                this.x = x * a;
            } else {
                this.x = 0.05f; // 防止与X轴重合
            }
            if (y != 0) {
                this.y = y * a;
            } else {
                this.y = 0.08f; // 防止与X轴重合
            }
            if (z != 0) {
                this.z = z * a;
            } else {
                this.z = 0.1f; // 防止与X轴重合
            }
        }
    }
}
