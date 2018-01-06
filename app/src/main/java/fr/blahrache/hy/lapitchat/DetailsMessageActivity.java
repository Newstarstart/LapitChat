package fr.blahrache.hy.lapitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsMessageActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessagesDatabase;

    private String mReceiverId;
    private String mSenderId;
    private String mMessageId;

    private Users mReceiver;
    private Messages mMessage;



    private CircleImageView details_message_receiver_image;
    private TextView details_message_receiver_name;
    private TextView details_message_receiver_status;
    private ImageView details_message_user_online_icon;

    private TextView details_message_subject_tv;
    private TextView details_message_body_tv;
    private TextView details_message_date_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_message);


        mToolbar = (Toolbar) findViewById(R.id.details_message_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Details message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages");

        mMessageId = getIntent().getStringExtra("message_id");
        mSenderId = mAuth.getCurrentUser().getUid();

        mReceiver = new Users();
        mMessage = new Messages();


        details_message_receiver_image = (CircleImageView) findViewById(R.id.details_message_receiver_image);
        details_message_receiver_name = (TextView) findViewById(R.id.details_message_receiver_name);
        details_message_receiver_status = (TextView) findViewById(R.id.details_message_receiver_status);
        details_message_user_online_icon = (ImageView) findViewById(R.id.details_message_user_online_icon);

        details_message_subject_tv = (TextView) findViewById(R.id.details_message_subject_tv);
        details_message_body_tv = (TextView) findViewById(R.id.details_message_body_tv);
        details_message_date_tv = (TextView) findViewById(R.id.details_message_date_tv);

        mMessagesDatabase.child(mSenderId).child(mMessageId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessage.setSubject(dataSnapshot.child("subject").getValue().toString());
                mMessage.setBody(dataSnapshot.child("body").getValue().toString());
                mMessage.setDate(dataSnapshot.child("date").getValue().toString());
                mMessage.setType(dataSnapshot.child("type").getValue().toString());
                if(mMessage.getType().equals("send")){
                    mMessage.setTo(dataSnapshot.child("to").getValue().toString());
                } else {
                    mMessage.setFrom(dataSnapshot.child("from").getValue().toString());
                }

                details_message_subject_tv.setText(mMessage.getSubject());
                details_message_body_tv.setText(mMessage.getBody());
                details_message_date_tv.setText(mMessage.getDate());

                mUsersDatabase.child(mMessage.getFrom()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mReceiver.setName(dataSnapshot.child("name").getValue().toString());
                        mReceiver.setStatus(dataSnapshot.child("status").getValue().toString());
                        mReceiver.setImage(dataSnapshot.child("image").getValue().toString());
                        mReceiver.setThumb_image(dataSnapshot.child("thumb_image").getValue().toString());
                        mReceiver.setOnlin(dataSnapshot.child("online").getValue().toString());


                        details_message_receiver_name.setText(mReceiver.getName());
                        details_message_receiver_status.setText(mReceiver.getStatus());
                        Picasso.with(DetailsMessageActivity.this).load(mReceiver.getThumb_image()).placeholder(R.mipmap.default_avatar).into(details_message_receiver_image);
                        if(mReceiver.getOnlin().equals("true")){
                            details_message_user_online_icon.setVisibility(View.VISIBLE);
                        } else {
                            details_message_user_online_icon.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
