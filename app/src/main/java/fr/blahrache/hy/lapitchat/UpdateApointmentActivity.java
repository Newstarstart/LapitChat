package fr.blahrache.hy.lapitchat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateApointmentActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mAppointmentsDatabase;

    private String mReceiverId;
    private String mSenderId;

    private Users mReceiver;
    private Users mSender;
    private Appointments mAppointment;
    private Appointments mOldAppointment;

    private Calendar mCurrentDate;


    // Widgets
    private CircleImageView update_appointement_receiver_image;
    private TextView update_appointement_receiver_name;
    private TextView update_appointement_receiver_status;
    private ImageView update_appointement_online_icon;

    private EditText update_appointment_subject_et;
    private EditText update_appointment_body_et;
    private EditText update_appointment_address_et;

    private Button update_appointment_date_btn;
    private TextView update_appointment_date_tv;

    private Button update_appointment_hour_btn;
    private TextView update_appointment_hour_tv;

    private Button update_appointment_send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_apointment);


        mToolbar = (Toolbar) findViewById(R.id.update_appointment_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAppointmentsDatabase = FirebaseDatabase.getInstance().getReference().child("Appointments");

        mReceiverId = getIntent().getStringExtra("user_id");
        mSenderId = mAuth.getCurrentUser().getUid();

        mReceiver = new Users();
        mSender = new Users();
        mAppointment = new Appointments();
        mOldAppointment = new Appointments();

        mCurrentDate = Calendar.getInstance();


        update_appointement_receiver_image = (CircleImageView) findViewById(R.id.update_appointement_receiver_image);
        update_appointement_receiver_name = (TextView) findViewById(R.id.update_appointement_receiver_name);
        update_appointement_receiver_status = (TextView) findViewById(R.id.update_appointement_receiver_status);
        update_appointement_online_icon = (ImageView) findViewById(R.id.update_appointement_online_icon);

        update_appointment_subject_et = (EditText) findViewById(R.id.update_appointment_subject_et);
        update_appointment_body_et = (EditText) findViewById(R.id.update_appointment_body_et);
        update_appointment_address_et = (EditText) findViewById(R.id.update_appointment_address_et);

        update_appointment_date_btn = (Button) findViewById(R.id.update_appointment_date_btn);
        update_appointment_date_tv = (TextView) findViewById(R.id.update_appointment_date_tv);

        update_appointment_hour_btn = (Button) findViewById(R.id.update_appointment_hour_btn);
        update_appointment_hour_tv = (TextView) findViewById(R.id.update_appointment_hour_tv);


        update_appointment_date_btn = (Button) findViewById(R.id.update_appointment_date_btn);
        update_appointment_hour_btn = (Button) findViewById(R.id.update_appointment_hour_btn);

        update_appointment_send_btn = (Button) findViewById(R.id.update_appointment_send_btn);



        update_appointment_date_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                int mounth = mCurrentDate.get(Calendar.MONTH);
                int year = mCurrentDate.get(Calendar.YEAR);
                mounth += 1;

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateApointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mounthOfYear, int dayOfMounth) {
                        mAppointment.setYear(year);
                        mAppointment.setMounth(mounthOfYear);
                        mAppointment.setDay(dayOfMounth);
                        update_appointment_date_tv.setText(mAppointment.getDay() + "/" + mAppointment.getMounth() + "/" + mAppointment.getYear());
                    }
                }, year, mounth, day);
                datePickerDialog.show();
            }
        });


        update_appointment_hour_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int hour = mCurrentDate.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentDate.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateApointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mAppointment.setHour(hourOfDay);
                        mAppointment.setMinute(minute);
                        update_appointment_hour_tv.setText(mAppointment.getHour() + ":" + mAppointment.getMinute());
                    }
                }, hour, minute, true);
                timePickerDialog.show();

            }
        });


        update_appointment_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppointment.setSubject(update_appointment_subject_et.getText().toString());
                mAppointment.setBody(update_appointment_body_et.getText().toString());
                mAppointment.setAddress(update_appointment_address_et.getText().toString());

                //Toast.makeText(AppointmentActivity.this, mAppointment.toString(), Toast.LENGTH_LONG).show();

                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("subject").setValue(mAppointment.getSubject());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("body").setValue(mAppointment.getBody());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("address").setValue(mAppointment.getAddress());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("year").setValue(mAppointment.getYear());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("mounth").setValue(mAppointment.getMounth());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("day").setValue(mAppointment.getDay());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("hour").setValue(mAppointment.getHour());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("minute").setValue(mAppointment.getMinute());
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("type").setValue("received");
                mAppointmentsDatabase.child(mReceiverId).child(mSenderId).child("seen").setValue("no");

                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("subject").setValue(mAppointment.getSubject());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("body").setValue(mAppointment.getBody());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("address").setValue(mAppointment.getAddress());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("year").setValue(mAppointment.getYear());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("mounth").setValue(mAppointment.getMounth());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("day").setValue(mAppointment.getDay());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("hour").setValue(mAppointment.getHour());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("minute").setValue(mAppointment.getMinute());
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("type").setValue("send");
                mAppointmentsDatabase.child(mSenderId).child(mReceiverId).child("seen").setValue("yes");

                Toast.makeText(UpdateApointmentActivity.this, "The appointment has seccussfully updated", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(UpdateApointmentActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mReceiver.setName(dataSnapshot.child(mReceiverId).child("name").getValue().toString());
                mReceiver.setStatus(dataSnapshot.child(mReceiverId).child("status").getValue().toString());
                mReceiver.setImage(dataSnapshot.child(mReceiverId).child("image").getValue().toString());
                mReceiver.setThumb_image(dataSnapshot.child(mReceiverId).child("thumb_image").getValue().toString());
                mReceiver.setOnlin(dataSnapshot.child(mReceiverId).child("online").getValue().toString());

                mSender.setName(dataSnapshot.child(mSenderId).child("name").getValue().toString());
                mSender.setStatus(dataSnapshot.child(mSenderId).child("status").getValue().toString());
                mSender.setImage(dataSnapshot.child(mSenderId).child("image").getValue().toString());
                mSender.setThumb_image(dataSnapshot.child(mSenderId).child("thumb_image").getValue().toString());


                Picasso.with(UpdateApointmentActivity.this).load(mReceiver.getThumb_image()).placeholder(R.mipmap.default_avatar).into(update_appointement_receiver_image);
                update_appointement_receiver_name.setText(mReceiver.getName());
                update_appointement_receiver_status.setText(mReceiver.getStatus());
                if(mReceiver.getOnlin().equals("true")){
                    update_appointement_online_icon.setVisibility(View.VISIBLE);
                } else {
                    update_appointement_online_icon.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mAppointmentsDatabase.child(mSenderId).child(mReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mOldAppointment.setSubject(dataSnapshot.child("subject").getValue().toString());
                mOldAppointment.setBody(dataSnapshot.child("body").getValue().toString());
                mOldAppointment.setAddress(dataSnapshot.child("address").getValue().toString());
                mOldAppointment.setYear(Integer.parseInt(dataSnapshot.child("year").getValue().toString()));
                mOldAppointment.setMounth(Integer.parseInt(dataSnapshot.child("mounth").getValue().toString()));
                mOldAppointment.setDay(Integer.parseInt(dataSnapshot.child("day").getValue().toString()));
                mOldAppointment.setHour(Integer.parseInt(dataSnapshot.child("hour").getValue().toString()));
                mOldAppointment.setMinute(Integer.parseInt(dataSnapshot.child("minute").getValue().toString()));
                mOldAppointment.setType(dataSnapshot.child("type").getValue().toString());
                mOldAppointment.setSeen(dataSnapshot.child("seen").getValue().toString());


                update_appointment_subject_et.setText(mOldAppointment.getSubject());
                update_appointment_body_et.setText(mOldAppointment.getBody());
                update_appointment_address_et.setText(mOldAppointment.getAddress());
                update_appointment_date_tv.setText(mOldAppointment.getYear() + "/" + mOldAppointment.getMounth() + "/" + mOldAppointment.getDay());
                update_appointment_hour_tv.setText(mOldAppointment.getHour() + ":" + mOldAppointment.getMinute());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
