package fr.blahrache.hy.lapitchat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mAppointmentsList;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mAppointmentsDatabase;
    private DatabaseReference mRequestsDatabase;



    private String mCurrentUserId;
    private String mSenderUserId;

    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAppointmentsList = (RecyclerView) mMainView.findViewById(R.id.appointments_requests_list);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAppointmentsDatabase = FirebaseDatabase.getInstance().getReference().child("Appointments").child(mCurrentUserId);
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends_requests").child(mCurrentUserId);


        mAppointmentsList.setHasFixedSize(true);
        mAppointmentsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Appointments, AppointmentsViewHolder> appointmentsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Appointments, AppointmentsViewHolder>(
                Appointments.class,
                R.layout.appointments_single_layout,
                AppointmentsViewHolder.class,
                mAppointmentsDatabase
        ) {
            @Override
            protected void populateViewHolder(final AppointmentsViewHolder appointmentViewHolder, Appointments model, int position) {

                final String list_user_id = getRef(position).getKey();
                final Users user = new Users();
                final Appointments appointment = new Appointments();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        user.setName(dataSnapshot.child("name").getValue().toString());
                        user.setStatus(dataSnapshot.child("status").getValue().toString());
                        user.setImage(dataSnapshot.child("image").getValue().toString());
                        user.setThumb_image(dataSnapshot.child("thumb_image").getValue().toString());
                        user.setOnlin(dataSnapshot.child("online").getValue().toString());

                        appointmentViewHolder.setName(user.getName());
                        appointmentViewHolder.setUserImage(user.getThumb_image(), getContext());
                        appointmentViewHolder.setUserOnline(user.getOnlin());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mAppointmentsDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        appointment.setSubject(dataSnapshot.child("subject").getValue().toString());////
                        appointment.setBody(dataSnapshot.child("body").getValue().toString());
                        appointment.setAddress(dataSnapshot.child("address").getValue().toString());
                        appointment.setYear(Integer.parseInt(dataSnapshot.child("year").getValue().toString()));
                        appointment.setMounth(Integer.parseInt(dataSnapshot.child("mounth").getValue().toString()));
                        appointment.setDay(Integer.parseInt(dataSnapshot.child("day").getValue().toString()));
                        appointment.setHour(Integer.parseInt(dataSnapshot.child("hour").getValue().toString()));
                        appointment.setMinute(Integer.parseInt(dataSnapshot.child("minute").getValue().toString()));
                        appointment.setType(dataSnapshot.child("type").getValue().toString());
                        appointment.setSeen(dataSnapshot.child("seen").getValue().toString());

                        appointmentViewHolder.setDate(appointment.getDay()+ "/" + appointment.getMounth() +
                                "/" + appointment.getYear() + " at " + appointment.getHour() + ":" + appointment.getMinute());

                        appointmentViewHolder.setAppointmentSeen(appointment.getSeen());
                        appointmentViewHolder.setAppointmentSendReceived(appointment.getType());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                appointmentViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Details",
                                "Place",
                                "Mark as seen",
                                "Mark as not Seen",
                                "Update appointment",
                                "Send message",
                                "Open profile"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setItems(options, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                switch (position){
                                    case 0:
                                        //Toast.makeText(getContext(), "Details", Toast.LENGTH_LONG).show();
                                        Intent detailsAppointmentIntent = new Intent(getContext(), DetailsAppointmentActivity.class);
                                        detailsAppointmentIntent.putExtra("user_id", list_user_id);
                                        startActivity(detailsAppointmentIntent);
                                        break;
                                    case 1:
                                        //Toast.makeText(getContext(), "Place", Toast.LENGTH_LONG).show();
                                        Intent placeAppointmentIntent = new Intent(getContext(), PlaceActivity.class);
                                        placeAppointmentIntent.putExtra("address", appointment.getAddress());
                                        placeAppointmentIntent.putExtra("otherUser", appointment.getType());
                                        startActivity(placeAppointmentIntent);
                                        break;
                                    case 2:
                                        //Toast.makeText(getContext(), "Mark as seen", Toast.LENGTH_LONG).show();
                                        mAppointmentsDatabase.child(list_user_id).child("seen").setValue("yes");
                                        break;
                                    case 3:
                                        //Toast.makeText(getContext(), "Mark as not Seen", Toast.LENGTH_LONG).show();
                                        mAppointmentsDatabase.child(list_user_id).child("seen").setValue("no");
                                        break;
                                    case 4:
                                        //Toast.makeText(getContext(), "Update appointment", Toast.LENGTH_LONG).show();
                                        Intent updateAppointmentIntent = new Intent(getContext(), UpdateApointmentActivity.class);
                                        updateAppointmentIntent.putExtra("user_id", list_user_id);
                                        startActivity(updateAppointmentIntent);
                                        break;
                                    case 5:
                                        //Toast.makeText(getContext(), "Send message", Toast.LENGTH_LONG).show();
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("user_id", list_user_id);
                                        startActivity(chatIntent);
                                        break;
                                    case 6:
                                        //Toast.makeText(getContext(), "Open profile", Toast.LENGTH_LONG).show();
                                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                        profileIntent.putExtra("user_id", list_user_id);
                                        startActivity(profileIntent);
                                        break;
                                }
                            }
                        });
                        builder.show();

                    }
                });
            }
        };
        mAppointmentsList.setAdapter(appointmentsRecyclerViewAdapter);
    }

    public static class AppointmentsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public AppointmentsViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.appointment_single_name);
            userNameView.setText(name);
        }
        public void setUserImage(String thumb_image, Context context){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.appointment_single_image);
            if(!thumb_image.equals("default")){
                Picasso.with(context).load(thumb_image).placeholder(R.mipmap.default_avatar).into(userImageView);
            }
        }
        public void setDate(String date){
            TextView userNameView = (TextView) mView.findViewById(R.id.appointment_single_date);
            userNameView.setText(date);
        }

        public void setUserOnline(String onlineStatus){
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.appointmet_single_online_icon);
            if(onlineStatus.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
        public void setAppointmentSeen(String appointmentSeen){
            ImageView userSeenView = (ImageView) mView.findViewById(R.id.appointmet_single_seen_icon);
            if(appointmentSeen.equals("yes")){
                userSeenView.setVisibility(View.VISIBLE);
            } else {
                userSeenView.setVisibility(View.INVISIBLE);
            }
        }
        public void setAppointmentSendReceived(String appointmentSendReceived){
            ImageView userSendView = (ImageView) mView.findViewById(R.id.appointmet_single_send_icon);
            ImageView userReceivedView = (ImageView) mView.findViewById(R.id.appointmet_single_received_icon);
            if(appointmentSendReceived.equals("send")){
                userSendView.setVisibility(View.VISIBLE);
                userReceivedView.setVisibility(View.INVISIBLE);
            } else {
                userSendView.setVisibility(View.INVISIBLE);
                userReceivedView.setVisibility(View.VISIBLE);
            }
        }
    }
}
