package fr.blahrache.hy.lapitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessagesDatabase;

    private String mReceiverId;
    private String mSenderId;

    private Users mReceiver;

    // Widgets
    private CircleImageView chat_send_receiver_image;
    private TextView chat_send_receiver_name;
    private TextView chat_send_receiver_status;
    private ImageView chat_send_online_icon;

    private EditText chat_send_subject_et;
    private EditText chat_send_body_et;

    private Button chat_send_send_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mToolbar = (Toolbar) findViewById(R.id.chat_send_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Send message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages");

        mReceiverId = getIntent().getStringExtra("user_id");
        mSenderId = mAuth.getCurrentUser().getUid();

        mReceiver = new Users();

        chat_send_receiver_image = (CircleImageView) findViewById(R.id.chat_send_receiver_image);
        chat_send_receiver_name = (TextView) findViewById(R.id.chat_send_receiver_name);
        chat_send_receiver_status = (TextView) findViewById(R.id.chat_send_receiver_status);
        chat_send_online_icon = (ImageView) findViewById(R.id.chat_send_online_icon);

        chat_send_subject_et  = (EditText) findViewById(R.id.chat_send_subject_et);
        chat_send_body_et = (EditText) findViewById(R.id.chat_send_body_et);

        chat_send_send_btn = (Button) findViewById(R.id.chat_send_send_btn);





        chat_send_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Messages message = new Messages();
                final String date = DateFormat.getDateTimeInstance().format(new Date());
                Map<String, String> receivedMessage = new HashMap<String, String>();
                receivedMessage.put("subject", chat_send_subject_et.getText().toString());
                receivedMessage.put("body", chat_send_body_et.getText().toString());
                receivedMessage.put("from", mSenderId);
                receivedMessage.put("type", "received");
                receivedMessage.put("seen", "no");
                receivedMessage.put("date", date);

                mMessagesDatabase.child(mReceiverId).push().setValue(receivedMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Map<String, String> sendMessage = new HashMap<String, String>();
                            sendMessage.put("subject", chat_send_subject_et.getText().toString());
                            sendMessage.put("body", chat_send_body_et.getText().toString());
                            sendMessage.put("to", mReceiverId);
                            sendMessage.put("type", "send");
                            sendMessage.put("seen", "yes");
                            sendMessage.put("date", date);

                            mMessagesDatabase.child(mSenderId).push().setValue(sendMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChatActivity.this, "The message was successfully sent", Toast.LENGTH_LONG).show();
                                        Intent chatIntent = new Intent(ChatActivity.this, MainActivity.class);
                                        startActivity(chatIntent);
                                    } else {
                                        Toast.makeText(ChatActivity.this, "Failure to send the message. Please try again", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChatActivity.this, "Failure to send the message. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });




        mUsersDatabase.child(mReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mReceiver.setName(dataSnapshot.child("name").getValue().toString());
                mReceiver.setStatus(dataSnapshot.child("status").getValue().toString());
                mReceiver.setImage(dataSnapshot.child("image").getValue().toString());
                mReceiver.setThumb_image(dataSnapshot.child("thumb_image").getValue().toString());
                mReceiver.setOnlin(dataSnapshot.child("online").getValue().toString());

                Picasso.with(ChatActivity.this).load(mReceiver.getThumb_image()).placeholder(R.mipmap.default_avatar).into(chat_send_receiver_image);
                chat_send_receiver_name.setText(mReceiver.getName());
                chat_send_receiver_status.setText(mReceiver.getStatus());

                if(mReceiver.getOnlin().equals("true")){
                    chat_send_online_icon.setVisibility(View.VISIBLE);
                } else {
                    chat_send_online_icon.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
