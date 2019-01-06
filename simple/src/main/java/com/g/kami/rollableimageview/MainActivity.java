package com.g.kami.rollableimageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private final int DEFAULT_REPEAT_TIME = -1;

    private final int DEFAULT_LAST_TIME = 5000;

    private EditText repeatTimeET; // 滚动次数输入框

    private EditText lastTimeET; // 持续时间输入框

    private Button upBtn, downBtn, leftBtn, rightBtn; // 上下左右滚动按钮

    private RollableImageView mRollableImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
    }

    private void initWidget(){
        repeatTimeET = findViewById(R.id.repeat_time_edit_text);
        lastTimeET = findViewById(R.id.last_time_edit_view);
        upBtn = findViewById(R.id.roll_up_btn);
        upBtn.setOnClickListener(v -> roll(RollableImageView.RollDirection.UP));
        downBtn = findViewById(R.id.roll_down_btn);
        downBtn.setOnClickListener(v -> roll(RollableImageView.RollDirection.DOWN));
        leftBtn = findViewById(R.id.roll_left_btn);
        leftBtn.setOnClickListener(v -> roll(RollableImageView.RollDirection.LEFT));
        rightBtn = findViewById(R.id.roll_right_btn);
        rightBtn.setOnClickListener(v -> roll(RollableImageView.RollDirection.RIGHT));
        mRollableImageView = findViewById(R.id.rollable_imageview);
    }

    private void roll(@RollableImageView.RollDirection int rollDirection){
        if (rollDirection == RollableImageView.RollDirection.UP
                || rollDirection == RollableImageView.RollDirection.DOWN){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.height_background);
            mRollableImageView.setImageBitmap(bitmap);
        }else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.long_background);
            mRollableImageView.setImageBitmap(bitmap);
        }
        String repeatTimeS = repeatTimeET.getText().toString();
        int repeatTimeI = DEFAULT_REPEAT_TIME;
        try {
            repeatTimeI = Integer.valueOf(repeatTimeS);
        }catch (NumberFormatException ignored){
        }
        String lastTimeS = lastTimeET.getText().toString();
        int lastTimeI = 0;
        try {
            lastTimeI = Integer.valueOf(lastTimeS);
        } catch (NumberFormatException ignored) {
        }
        if (0 == lastTimeI)
            lastTimeI = DEFAULT_LAST_TIME;
        if (repeatTimeI < 0){
            mRollableImageView.startRollInfinite(lastTimeI, rollDirection);
        }else {
            mRollableImageView.startRoll(repeatTimeI, lastTimeI, rollDirection);
        }
    }


}
