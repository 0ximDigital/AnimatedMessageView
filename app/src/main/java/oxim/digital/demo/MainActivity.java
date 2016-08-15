package oxim.digital.demo;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import oxim.digital.animatedmessageview.CircleIconView;
import oxim.digital.animatedmessageview.demo.R;

public class MainActivity extends AppCompatActivity {

    private CircleIconView circleIconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.circleIconView = (CircleIconView) findViewById(R.id.test_circle_icon);
        this.circleIconView.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.black_space_invader));
    }
}
