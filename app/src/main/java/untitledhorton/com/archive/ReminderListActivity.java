package untitledhorton.com.archive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class ReminderListActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    DatabaseReference mDatabase, insidemDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FloatingActionButton fab;
    ArrayList<Reminder> reminders;
    SwipeMenuListView lvReminders;
    CustomAdapter reminderAdapter;
    EditText etTitle, etMessage;
    Button btnDateTime, btnDone, btnClose;
    TextView txtDateTime, lblTitle, lblMessage, lblDateTime, lblDialogTitle;
    private int day, month, year, hour, minute, finalDay, finalMonth, finalYear, finalHour, finalMinute;
    private String date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);


        lvReminders = findViewById(R.id.lvReminders);
        reminders = new ArrayList<Reminder>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reminders").child(user.getUid());
        fab = findViewById(R.id.fab);
        fab.attachToListView(lvReminders);
        reminderAdapter = new CustomAdapter(getApplicationContext(), reminders);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Reminder reminder;
                for (DataSnapshot objSnapshot: snapshot.getChildren()) {
                    Object key = objSnapshot.getKey();

                    insidemDatabase = mDatabase.child(key.toString());
                    reminder = objSnapshot.getValue(Reminder.class);
                    reminder.setId(key.toString());
                    reminders.add(reminder);
                    reminderAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
            lvReminders.setAdapter(reminderAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(
                        ReminderListActivity.this);
                    editItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0x66,
                            0xff)));
                    editItem.setWidth(200);
                    editItem.setTitle("EDIT");
                    editItem.setTitleSize(18);
                    editItem.setTitleColor(Color.WHITE);
                    menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        ReminderListActivity.this);

                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));

                    deleteItem.setWidth(200);
                    deleteItem.setTitle("DELETE");
                    deleteItem.setTitleColor(Color.WHITE);
                    deleteItem.setTitleSize(18);

                 menu.addMenuItem(deleteItem);
            }
        };

        lvReminders.setMenuCreator(creator);


        lvReminders.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Reminder item = reminders.get(position);
                switch (index) {
                    case 0:
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReminderListActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_edit, null);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        lblDialogTitle = mView.findViewById(R.id.lblDialogTitle);
                        etTitle = mView.findViewById(R.id.etTitle);
                        etMessage = mView.findViewById(R.id.etMessage);
                        btnDateTime = mView.findViewById(R.id.btnDateTime);
                        btnDone = mView.findViewById(R.id.btnDone);
                        txtDateTime = mView.findViewById(R.id.txtDateTime);
                        btnClose = mView.findViewById(R.id.btnClose);

                        lblDialogTitle.setText("EDIT REMINDER");
                        btnDone.setText("EDIT");
                        etTitle.setText(item.getTitle());
                        etMessage.setText(item.getMessage());
                        txtDateTime.setText(item.getDate()+ "\n" + item.getTime());
                        date = item.getDate();
                        time = item.getTime();

                        final String id = item.getId();

                        btnDateTime.setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                Calendar cal = Calendar.getInstance();
                                year = cal.get(Calendar.YEAR);
                                month = cal.get(Calendar.MONTH);
                                day = cal.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderListActivity.this, ReminderListActivity.this, year, month, day);
                                datePickerDialog.show();
                            }
                        });

                        btnDone.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                Reminder reminder = new Reminder();
                                reminder.setTitle(etTitle.getText().toString());
                                reminder.setMessage(etMessage.getText().toString());
                                reminder.setDate(date);
                                reminder.setTime(time);

                                mDatabase.child(id).setValue(reminder);
                                reminders.clear();
                                dialog.dismiss();
                            }
                        });

                        btnClose.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        reminderAdapter.notifyDataSetChanged();
                        break;
                    case 1:

                        mDatabase.child(item.getId()).removeValue();
                        reminders.clear();
                        reminderAdapter.notifyDataSetChanged();
                        break;
                }

                return false;
            }

        });


        lvReminders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Reminder item = reminders.get(position);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReminderListActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_reminder, null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                lblTitle = mView.findViewById(R.id.lblTitle);
                lblMessage = mView.findViewById(R.id.lblMessage);
                lblDateTime = mView.findViewById(R.id.lblDateTime);
                btnClose = mView.findViewById(R.id.btnClose);

                lblTitle.setText(item.getTitle());
                lblMessage.setText(item.getMessage());
                lblDateTime.setText("Date: " + item.getDate() + "\nTime:" + item.getTime());

                btnClose.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                return false;
            }
        });

        lvReminders.setCloseInterpolator(new BounceInterpolator());

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ReminderListActivity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        finalYear = i;
        finalMonth = i1 + 1;
        finalDay = i2;

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(ReminderListActivity.this, ReminderListActivity.this, hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        finalHour = i;
        finalMinute = i1;
        date = finalMonth + "/" + finalDay + "/" + finalYear;
        time = finalHour + ":" + finalMinute;
        txtDateTime.setText(date + "\n" + time);
    }

}

