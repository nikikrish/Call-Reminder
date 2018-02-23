package com.example.nikikrish.remdemo;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;


public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder>{


    Context mContext;
    List<UserDetails> userDetailsList;
    DateTime dateTime;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    public RecycleAdapter(Context c, List<UserDetails> details){
        mContext = c;
        userDetailsList = details;
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_layout,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DbHandler db = new DbHandler(mContext);
        final UserDetails details = userDetailsList.get(position);
        if(details.getContactName().equals("unknown")){
            holder.userName.setText(details.getContactNumber());
            holder.userNumber.setVisibility(View.INVISIBLE);
        }else{
            holder.userName.setText(details.getContactName());
            holder.userNumber.setText(details.getContactNumber());
        }
        if(Long.parseLong(details.getDuration())<System.currentTimeMillis()){
            holder.snoozeIcon.setVisibility(View.GONE);
        }
        String lastCallTime = new DateTime().withMillis(Long.parseLong(details.getCallTime())).toString("HH:mm");
        holder.lastCall.setText("Last Called at "+lastCallTime);
        binderHelper.bind(holder.swipeRevealLayout,details.getCallTime());
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:"+details.getContactNumber()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        holder.snoozeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                dateTime = new DateTime().withHourOfDay(i).withMinuteOfHour(i1).withSecondOfMinute(0);
                                details.setDuration( String.valueOf(dateTime.getMillis()));
                                NotificationHelper.setNotification(AlarmReceiver.class,mContext,dateTime.getMillis());
                                db.updateReminder(details);
                                userDetailsList.clear();
                                userDetailsList = db.getAllReminders();
                                notifyDataSetChanged();
                                notifyItemChanged(position);
                                }
                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        false);

                timePickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                timePickerDialog.show();
            }
        });

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(details,holder.getAdapterPosition());
//                hasFutureActivityStarted = true;
//                hasPastActivityStarted = false;

            }
        });
    }

    @Override
    public int getItemCount() {
        return userDetailsList.size();
    }

    public class MyViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder{

        RelativeLayout view_bg;
        LinearLayout view_fg;
        TextView userName,userNumber,lastCall;
        SwipeRevealLayout swipeRevealLayout;
        AppCompatImageView editIcon,snoozeIcon,deleteIcon;
        public MyViewHolder(View itemView) {
            super(itemView);
            view_bg = itemView.findViewById(R.id.view_bg);
            view_fg = itemView.findViewById(R.id.view_fg);
            userName = itemView.findViewById(R.id.item_user_name);
            userNumber = itemView.findViewById(R.id.item_user_number);
            lastCall = itemView.findViewById(R.id.item_last_call);
            swipeRevealLayout = itemView.findViewById(R.id.swipeLayout);
            editIcon = itemView.findViewById(R.id.editIcon);
            snoozeIcon = itemView.findViewById(R.id.snoozeIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
    public void removeItem(int position) {
        userDetailsList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(UserDetails details, int position) {
        userDetailsList.add(position, details);
        // notify item added by position
        notifyItemInserted(position);
    }
    private void showDeleteDialog(final UserDetails userDetails,final int position){
        final AlertDialog.Builder builder= new AlertDialog.Builder(mContext.getApplicationContext());

        builder.setTitle("Delete Reminder")
                .setMessage("Are you sure ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DbHandler db = new DbHandler(mContext);
                        db.deleteReminder(userDetails);
                        userDetailsList.remove(position);
                        notifyItemRemoved(position);
                        userDetailsList.clear();
                        userDetailsList = db.getAllReminders();
                        notifyDataSetChanged();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.setCancelable(true);
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        dialog.show();
    }
}
