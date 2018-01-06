package fr.blahrache.hy.lapitchat;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private final String NOT_FRIENDS_STATE = "not_friends";
    private final String FRIENDS_STATE = "friends";
    private final String REQUEST_SENT_STATE = "request_sent";
    private final String REQUEST_RECEIVED_STATE = "request_received";

    //
    private String mCurrent_state = NOT_FRIENDS_STATE;


    //Firebase references
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendsRequestsDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrent_user;

    //Widgets
    private ImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileStatus;
    private TextView mProfileFriendCount;
    private Button mProfileSendReqBtn;
    private Button mProfileDeclineBtn;

    //
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        //the id of the user that we are visiting
        final String user_id = getIntent().getStringExtra("user_id");

        //Firebase references
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends_requests");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        //Widgets
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_display_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendCount = (TextView) findViewById(R.id.profile_total_friends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mProfileDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);
        mProfileDeclineBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineBtn.setEnabled(false);


        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Loading user data");
        mProgressDialog.setMessage("Please wait wile we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the data of the user that we are visiting
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.mipmap.default_avatar).into(mProfileImage);

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mCurrent_user.getUid())){
                    if(dataSnapshot.child(mCurrent_user.getUid()).hasChild(user_id)){
                        //mProfileSendReqBtn.setEnabled(true);
                        mProfileSendReqBtn.setText("Unfriend this person");
                        mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.red));
                        mCurrent_state = FRIENDS_STATE;
                    } else {
                        //mProfileSendReqBtn.setEnabled(true);
                        mProfileSendReqBtn.setText("Send friend request");
                        mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        mCurrent_state = NOT_FRIENDS_STATE;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFriendsRequestsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mCurrent_user.getUid())){
                    if(dataSnapshot.child(mCurrent_user.getUid()).hasChild(user_id)){
                        String req_type = dataSnapshot.child(mCurrent_user.getUid()).child(user_id).child("request_type").getValue().toString();
                        if(req_type.equals("received")){
                            //mProfileSendReqBtn.setEnabled(true);
                            mProfileSendReqBtn.setText("Accept friend request");
                            mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.green));
                            mCurrent_state = REQUEST_RECEIVED_STATE;
                            mProfileDeclineBtn.setVisibility(View.VISIBLE);
                            mProfileDeclineBtn.setEnabled(true);
                        } else if(req_type.equals("sent")){
                            //mProfileSendReqBtn.setEnabled(true);
                            mProfileSendReqBtn.setText("Cancel friend request");
                            mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.red));
                            mCurrent_state = REQUEST_SENT_STATE;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //mProfileSendReqBtn.setEnabled(false);

                /* ============== NOT_FRIENDS_STATE ============== */
                if(mCurrent_state.equals(NOT_FRIENDS_STATE)){
                    mFriendsRequestsDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsRequestsDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mProfileSendReqBtn.setEnabled(true);

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrent_user.getUid());
                                        notificationData.put("type", "friend_request");
                                        notificationData.put("seen", "no");
                                        notificationData.put("date", DateFormat.getDateTimeInstance().format(new Date()));

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ProfileActivity.this, "Notification request notification was successfully sent", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, "Failed sending request notification. Please try again", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                        mProfileSendReqBtn.setText("Cancel friend request");
                                        mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.red));
                                        mCurrent_state = REQUEST_SENT_STATE;

                                        Toast.makeText(ProfileActivity.this, "Request was successfully sent", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed sending request. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                /* ============== REQUEST_SENT_STATE ============== */
                else if(mCurrent_state.equals(REQUEST_SENT_STATE)){
                    mFriendsRequestsDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsRequestsDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mProfileSendReqBtn.setEnabled(true);
                                        mProfileSendReqBtn.setText("Send friend request");
                                        mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                        mCurrent_state = NOT_FRIENDS_STATE;

                                        Toast.makeText(ProfileActivity.this, "Request was successfully canceled", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed canceling request. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                /* ============== REQUEST_RECEIVED_STATE ============== */
                else if(mCurrent_state.equals(REQUEST_RECEIVED_STATE)){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsDatabase.child(mCurrent_user.getUid()).child(user_id).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsDatabase.child(user_id).child(mCurrent_user.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mProfileSendReqBtn.setEnabled(true);
                                        mProfileSendReqBtn.setText("Unfriend this person");
                                        mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.red));
                                        mCurrent_state = FRIENDS_STATE;
                                    }
                                });
                                mFriendsRequestsDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFriendsRequestsDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                                mProfileDeclineBtn.setEnabled(false);
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed accepting request. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                /* ============== FRIENDS_STATE ============== */
                else if(mCurrent_state.equals(FRIENDS_STATE)){
                    mFriendsDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendsDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProfileSendReqBtn.setText("Send friend request");
                                        mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                        mCurrent_state = NOT_FRIENDS_STATE;
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed unfriending this person. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

        mProfileDeclineBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                mFriendsRequestsDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mFriendsRequestsDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //mProfileSendReqBtn.setEnabled(true);
                                    mProfileSendReqBtn.setText("Send friend request");
                                    mProfileSendReqBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    mCurrent_state = NOT_FRIENDS_STATE;
                                    mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineBtn.setEnabled(false);

                                    Toast.makeText(ProfileActivity.this, "Request was successfully canceled", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed canceling request. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
