package cn.appleye.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.appleye.stepsview.StepsView;

public class MainActivity extends AppCompatActivity {

    private StepsView mStepsView;
    private View mNextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStepsView = (StepsView) findViewById(R.id.steps_view);
        mStepsView.setStepsCount(5);

        mNextView = findViewById(R.id.next_step_view);

        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStepsView.nextStep();
            }
        });
    }
}
