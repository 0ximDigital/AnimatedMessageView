package oxim.digital.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import oxim.digital.animatedmessageview.AnimatedMessageView;
import oxim.digital.animatedmessageview.demo.R;

public class MainActivity extends AppCompatActivity {

    private AnimatedMessageView animatedMessageView;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.animatedMessageView = (AnimatedMessageView) findViewById(R.id.animated_message_view);
        this.testButton = (Button) findViewById(R.id.button);

        this.testButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                animatedMessageView.show();
            }
        });
    }
}
