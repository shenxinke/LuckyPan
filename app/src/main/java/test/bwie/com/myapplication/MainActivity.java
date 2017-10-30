package test.bwie.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LucyPan mLucyPan;
    private ImageView mStartBtn;
    private int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLucyPan = (LucyPan) findViewById(R.id.id_LucyPan);
        mStartBtn = (ImageView) findViewById(R.id.id_stard_btn);
        checkId();
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLucyPan.isStart()){
                    mLucyPan.luckyStart(i);
                    mStartBtn.setImageResource(R.mipmap.stop);
                }else {
                    if(!mLucyPan.isShouldEnd()){
                        mLucyPan.luckyEnd();
                        mStartBtn.setImageResource(R.mipmap.start);
                    }
                }
            }
        });
    }
    public void checkId(){
        Random rand = new Random();
        i = rand.nextInt(1000);
        if(i<10&&i>0){
            i=0;
        }else if(i<30&&i>10){
            i=1;
        }else if(i<50&&i>30){
            i=3;
        }else if(i<150&&i>50){
            i=4;
        }else if(i<300&&i>150){
            i=2;
        }else if(i<1000&&i>300){
            i=5;
        }
    }
}
