package fr.blahrache.hy.lapitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class StartActivity extends AppCompatActivity {

    private ImageView mStartImage;
    private Button mRegBtn;
    private Button mLogBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mStartImage = (ImageView) findViewById(R.id.start_image);
        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mLogBtn = (Button) findViewById(R.id.start_log_btn);

        Picasso.with(StartActivity.this).load(R.mipmap.time_calendar).placeholder(R.mipmap.default_avatar).into(mStartImage);

        mRegBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
                //finish();
            }
        });

        mLogBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(reg_intent);
                //finish();
            }
        });
    }
}
