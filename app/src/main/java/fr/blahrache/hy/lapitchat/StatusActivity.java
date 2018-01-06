package fr.blahrache.hy.lapitchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //The toolbar and the progress dialog
    private Toolbar mToolbar;
    private ProgressDialog mProgress;

    //The widgets of the activity
    private EditText mStatus;
    private Button mSavebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Show the toolbar
        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus = (EditText) findViewById(R.id.status_input);
        mSavebtn = (Button) findViewById(R.id.status_save_btn);

        //Set the value of the status
        String status_value = getIntent().getStringExtra("status_value");
        mStatus.setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String status = mStatus.getText().toString();
                if(!TextUtils.isEmpty(status)){
                    //show the progress dialog
                    mProgress = new ProgressDialog(StatusActivity.this);
                    mProgress.setTitle("Saving changes");
                    mProgress.setMessage("Please waite wile we save the changes.");
                    mProgress.show();

                    mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgress.dismiss();
                            } else {
                                mProgress.hide();
                                Toast.makeText(StatusActivity.this, "There wase some errors while saving changes.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(StatusActivity.this, "The statsu cannot be empty.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
