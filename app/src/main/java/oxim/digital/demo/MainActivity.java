package oxim.digital.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import oxim.digital.animatedmessageview.AnimatedMessageView;
import oxim.digital.animatedmessageview.demo.R;

public class MainActivity extends AppCompatActivity {

    private AnimatedMessageView animatedMessageView;
    private Button magicButton;
    private Button noMagicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.animatedMessageView = (AnimatedMessageView) findViewById(R.id.animated_message_view);
        this.magicButton = (Button) findViewById(R.id.button);

        this.magicButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                animatedMessageView.show();
            }
        });

        this.noMagicButton = (Button) findViewById(R.id.not_button);
        this.noMagicButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                animatedMessageView.hide();
            }
        });
    }
}
