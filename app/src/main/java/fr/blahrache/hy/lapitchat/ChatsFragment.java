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
public class ChatsFragment extends Fragment {

    private RecyclerView mChatsList;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessagesDatabase;

    private String mCurrentUserId;

    private View mMainView;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mChatsList = (RecyclerView) mMainView.findViewById(R.id.chat_requests_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrentUserId);

        mChatsList.setHasFixedSize(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Messages, MessagesViewHolder> messagesRecyclerViewAdapter = new FirebaseRecyclerAdapter<Messages, MessagesViewHolder>(
                Messages.class,
                R.layout.appointments_single_layout,
                MessagesViewHolder.class,
                mMessagesDatabase
        ) {
            @Override
            protected void populateViewHolder(final MessagesViewHolder messageViewHolder, Messages model, int position) {

                final String list_message_id = getRef(position).getKey();
                //Toast.makeText(getContext(), list_message_id, Toast.LENGTH_LONG).show();
                final Users user = new Users();
                final Messages message = new Messages();

                mMessagesDatabase.child(list_message_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        message.setSubject(dataSnapshot.child("subject").getValue().toString());
                        message.setBody(dataSnapshot.child("body").getValue().toString());
                        message.setDate(dataSnapshot.child("date").getValue().toString());
                        message.setSeen(dataSnapshot.child("seen").getValue().toString());
                        message.setType(dataSnapshot.child("type").getValue().toString());


                        if(message.getType().equals("send")){
                            message.setTo(dataSnapshot.child("to").getValue().toString());
                        } else {
                            message.setFrom(dataSnapshot.child("from").getValue().toString());
                        }



                        mUsersDatabase.child(message.getFrom()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                user.setName(dataSnapshot.child("name").getValue().toString());
                                user.setStatus(dataSnapshot.child("status").getValue().toString());
                                user.setImage(dataSnapshot.child("image").getValue().toString());
                                user.setThumb_image(dataSnapshot.child("thumb_image").getValue().toString());
                                user.setOnlin(dataSnapshot.child("online").getValue().toString());

                                messageViewHolder.setName(user.getName());
                                messageViewHolder.setUserImage(user.getThumb_image(), getContext());
                                messageViewHolder.setUserOnline(user.getOnlin());
                                messageViewHolder.setDate(message.getDate());
                                messageViewHolder.setMessageSendReceived(message.getType());
                                messageViewHolder.setMessageSeen(message.getSeen());
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

                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Details",
                                "Mark as seen",
                                "Mark as not Seen",
                                "Respond",
                                "Open profile"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                switch (position){
                                    case 0:
                                        //Toast.makeText(getContext(), "Details", Toast.LENGTH_LONG).show();
                                        Intent detailsMessageIntent = new Intent(getContext(), DetailsMessageActivity.class);
                                        detailsMessageIntent.putExtra("message_id", list_message_id);
                                        startActivity(detailsMessageIntent);
                                        break;
                                    case 1:
                                        //Toast.makeText(getContext(), "Mark Seen", Toast.LENGTH_LONG).show();
                                        mMessagesDatabase.child(list_message_id).child("seen").setValue("yes");
                                        break;
                                    case 2:
                                        //Toast.makeText(getContext(), "Mark as not Seen", Toast.LENGTH_LONG).show();
                                        mMessagesDatabase.child(list_message_id).child("seen").setValue("no");
                                        break;
                                    case 3:
                                        //Toast.makeText(getContext(), "Send message", Toast.LENGTH_LONG).show();
                                        Intent sendMessageIntent = new Intent(getContext(), ChatActivity.class);
                                        sendMessageIntent.putExtra("user_id", message.getFrom());
                                        startActivity(sendMessageIntent);
                                        break;
                                    case 4:
                                        //Toast.makeText(getContext(), "Open profile", Toast.LENGTH_LONG).show();
                                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                        profileIntent.putExtra("user_id", message.getFrom());
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
        mChatsList.setAdapter(messagesRecyclerViewAdapter);
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MessagesViewHolder(View itemView) {
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
        public void setMessageSeen(String messageSeen){
            ImageView userSeenView = (ImageView) mView.findViewById(R.id.appointmet_single_seen_icon);
            if(messageSeen.equals("yes")){
                userSeenView.setVisibility(View.VISIBLE);
            } else {
                userSeenView.setVisibility(View.INVISIBLE);
            }
        }
        public void setMessageSendReceived(String messageSendReceived){
            ImageView userSendView = (ImageView) mView.findViewById(R.id.appointmet_single_send_icon);
            ImageView userReceivedView = (ImageView) mView.findViewById(R.id.appointmet_single_received_icon);
            if(messageSendReceived.equals("send")){
                userSendView.setVisibility(View.VISIBLE);
                userReceivedView.setVisibility(View.INVISIBLE);
            } else {
                userSendView.setVisibility(View.INVISIBLE);
                userReceivedView.setVisibility(View.VISIBLE);
            }
        }

    }
}
