package fr.blahrache.hy.lapitchat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mAppointmentsDatabase;

    private String mReceiverId;
    private String mSenderId;

    private Users mReceiver;
    private Users mSender;
    private Appointments mAppointment;

    private Calendar mCurrentDate;


    // Widgets
    private CircleImageView appointement_receiver_image;
    private TextView appointement_receiver_name;
    private TextView appointement_receiver_status;
    private ImageView appointement_receiver_online_icon;

    private EditText appointment_subject_et;
    private EditText appointment_body_et;
    private EditText appointment_address_et;

    private Button appointment_date_btn;
    private TextView appointment_date_tv;

    private Button appointment_hour_btn;
    private TextView appointment_hour_tv;

    private Button appointment_send_btn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mToolbar = (Toolbar) findViewById(R.id.appointment_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Send appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAppointmentsDatabase = FirebaseDatabase.getInstance().getReference().child("Appointments");

        mReceiverId = getIntent().getStringExtra("user_id");
        mSenderId = mAuth.getCurrentUser().getUid();

        mReceiver = new Users();
        mSender = new Users();
        mAppointment = new Appointments();

        mCurrentDate = Calendar.getInstance();


        appointement_receiver_image = (CircleImageView) findViewById(R.id.appointement_receiver_image);
        appointement_receiver_name = (TextView) findViewById(R.id.appointement_receiver_name);
        appointement_receiver_status = (TextView) findViewById(R.id.appointement_receiver_status);
        appointement_receiver_online_icon = (ImageView) findViewById(R.id.appointement_receiver_online_icon);

        appointment_subject_et = (EditText) findViewById(R.id.appointment_subject_et);
        appointment_body_et = (EditText) findViewById(R.id.appointment_body_et);
        appointment_address_et = (EditText) findViewById(R.id.appointment_address_et);

        appointment_date_btn = (Button) findViewById(R.id.appointment_date_btn);
        appointment_date_tv = (TextView) findViewById(R.id.appointment_date_tv);

        appointment_hour_btn = (Button) findViewById(R.id.appointment_hour_btn);
        appointment_hour_tv = (TextView) findViewById(R.id.appointment_hour_tv);


        appointment_date_btn = (Button) findViewById(R.id.appointment_date_btn);
        appointment_hour_btn = (Button) findViewById(R.id.appointment_hour_btn);

        appointment_send_btn = (Button) findViewById(R.id.appointment_send_btn);



        appointment_date_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                int mounth = mCurrentDate.get(Calendar.MONTH);
                int year = mCurrentDate.get(Calendar.YEAR);
                mounth += 1;

                DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mounthOfYear, int dayOfMounth) {
                        mAppointment.setYear(year);
                        mAppointment.setMounth(mounthOfYear);
                        mAppointment.setDay(dayOfMounth);
                        appointment_date_tv.setText(mAppointment.getDay() + "/" + mAppointment.getMounth() + "/" + mAppointment.getYear());
                    }
                }, year, mounth, day);
                datePickerDialog.show();
            }
        });

        appointment_hour_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int hour = mCurrentDate.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentDate.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mAppointment.setHour(hourOfDay);
                        mAppointment.setMinute(minute);
                        appointment_hour_tv.setText(mAppointment.getHour() + ":" + mAppointment.getMinute());
                    }
                }, hour, minute, true);
                timePickerDialog.show();

            }
        });

        appointment_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppointment.setSubject(appointment_subject_et.getText().toString());
                mAppointment.setBody(appointment_body_et.getText().toString());
                mAppointment.setAddress(appointment_address_et.getText().toString());

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

                Toast.makeText(AppointmentActivity.this, "The appointment has seccussfully sent", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(AppointmentActivity.this, MainActivity.class);
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


                Picasso.with(AppointmentActivity.this).load(mReceiver.getThumb_image()).placeholder(R.mipmap.default_avatar).into(appointement_receiver_image);
                appointement_receiver_name.setText(mReceiver.getName());
                appointement_receiver_status.setText(mReceiver.getStatus());
                if(mReceiver.getOnlin().equals("true")){
                    appointement_receiver_online_icon.setVisibility(View.VISIBLE);
                } else {
                    appointement_receiver_online_icon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
