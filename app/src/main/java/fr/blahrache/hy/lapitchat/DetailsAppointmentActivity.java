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

public class DetailsAppointmentActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mAppointmentsDatabase;

    private String mReceiverId;
    private String mSenderId;

    private Users mReceiver;
    private Appointments mAppointment;



    private CircleImageView details_appointement_receiver_image;
    private TextView details_appointement_receiver_name;
    private TextView details_appointement_receiver_status;
    private ImageView details_appointment_user_online_icon;

    private TextView details_appointment_subject_tv;
    private TextView details_appointment_body_tv;
    private TextView details_appointment_address_tv;
    private TextView details_appointment_date_tv;
    private TextView details_appointment_hour_tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_appointment);


        mToolbar = (Toolbar) findViewById(R.id.details_appointment_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Details appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAppointmentsDatabase = FirebaseDatabase.getInstance().getReference().child("Appointments");

        mReceiverId = getIntent().getStringExtra("user_id");
        mSenderId = mAuth.getCurrentUser().getUid();

        mReceiver = new Users();
        mAppointment = new Appointments();

        details_appointement_receiver_image = (CircleImageView) findViewById(R.id.details_appointement_receiver_image);
        details_appointement_receiver_name = (TextView) findViewById(R.id.details_appointement_receiver_name);
        details_appointement_receiver_status = (TextView) findViewById(R.id.details_appointement_receiver_status);
        details_appointment_user_online_icon = (ImageView) findViewById(R.id.details_appointment_user_online_icon);

        details_appointment_subject_tv = (TextView) findViewById(R.id.details_appointment_subject_tv);
        details_appointment_body_tv = (TextView) findViewById(R.id.details_appointment_body_tv);
        details_appointment_address_tv = (TextView) findViewById(R.id.details_appointment_address_tv);
        details_appointment_date_tv = (TextView) findViewById(R.id.details_appointment_date_tv);
        details_appointment_hour_tv = (TextView) findViewById(R.id.details_appointment_hour_tv);


        mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("seen").setValue("yes");

        mUsersDatabase.child(mReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mReceiver.setName(dataSnapshot.child("name").getValue().toString());
                mReceiver.setStatus(dataSnapshot.child("status").getValue().toString());
                mReceiver.setImage(dataSnapshot.child("image").getValue().toString());
                mReceiver.setThumb_image(dataSnapshot.child("thumb_image").getValue().toString());
                mReceiver.setOnlin(dataSnapshot.child("online").getValue().toString());


                Picasso.with(DetailsAppointmentActivity.this).load(mReceiver.getThumb_image()).placeholder(R.mipmap.default_avatar).into(details_appointement_receiver_image);
                details_appointement_receiver_name.setText(mReceiver.getName());
                details_appointement_receiver_status.setText(mReceiver.getStatus());
                if(mReceiver.getOnlin().equals("true")){
                    details_appointment_user_online_icon.setVisibility(View.VISIBLE);
                } else {
                    details_appointment_user_online_icon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAppointmentsDatabase.child(mSenderId).child(mReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mAppointment.setSubject(dataSnapshot.child("subject").getValue().toString());
                mAppointment.setBody(dataSnapshot.child("body").getValue().toString());
                mAppointment.setAddress(dataSnapshot.child("address").getValue().toString());
                mAppointment.setYear(Integer.parseInt(dataSnapshot.child("year").getValue().toString()));
                mAppointment.setMounth(Integer.parseInt(dataSnapshot.child("mounth").getValue().toString()));
                mAppointment.setDay(Integer.parseInt(dataSnapshot.child("day").getValue().toString()));
                mAppointment.setHour(Integer.parseInt(dataSnapshot.child("hour").getValue().toString()));
                mAppointment.setMinute(Integer.parseInt(dataSnapshot.child("minute").getValue().toString()));
                mAppointment.setType(dataSnapshot.child("type").getValue().toString());
                mAppointment.setSeen(dataSnapshot.child("seen").getValue().toString());


                details_appointment_subject_tv.setText(mAppointment.getSubject());
                details_appointment_body_tv.setText(mAppointment.getBody());
                details_appointment_address_tv.setText(mAppointment.getAddress());
                details_appointment_date_tv.setText(mAppointment.getDay() + "/" + mAppointment.getMounth() + "/" + mAppointment.getYear());
                details_appointment_hour_tv.setText(mAppointment.getHour() + ":" + mAppointment.getMinute());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
