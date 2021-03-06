package com.evan.androidopengl;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.evan.androidopengl.render.AccGraphRender;
import com.evan.androidopengl.render.PikachuRender;

public class MainActivity extends AppCompatActivity {
    private static final String Tag = "MainActivity";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button AccGraph = findViewById(R.id.acc_graph);
        setViewClickListener(AccGraph, GLActivity.class, AccGraphRender.class.getName());

        Button Pikachu = findViewById(R.id.three_d_pikachu);
        setViewClickListener(Pikachu, GLActivity.class, PikachuRender.class.getName());
    }

    private void setViewClickListener(Button button, final Class clazz, final String name) {
        Log.i(Tag, "Subclass: " + name);

        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, clazz);

                intent.putExtra("className", name);
                startActivity(intent);
            }
        });
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
