package untitledhorton.com.archive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ReminderActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private EditText etTitle, etMessage;
    private TextView txtDateTime;
    private int day, month, year, hour, minute, finalDay, finalMonth, finalYear, finalHour, finalMinute;
    private String title, message, date, time;
    DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etMessage = (EditText)findViewById(R.id.etMessage);
        txtDateTime = (TextView)findViewById(R.id.txtDateTime);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reminders").child(user.getUid());

    }

    public void pickDateTime(View v){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderActivity.this, ReminderActivity.this, year, month, day);
        datePickerDialog.show();
    }

    public void saveReminder(View v){
        title = etTitle.getText().toString();
        message = etMessage.getText().toString();

        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("message", message);
        result.put("date", date);
        result.put("time", time);

        if(TextUtils.isEmpty(title)) {
            etTitle.setError("Enter title");
        }else if(TextUtils.isEmpty(message)){
            etMessage.setError("Enter message");
        }else{
            mDatabase.push().setValue(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        etTitle.setText("");
                        etMessage.setText("");

                        Intent intent = new Intent(ReminderActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Report failed to be sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                new TimePickerDialog(ReminderActivity.this, ReminderActivity.this, hour, minute, DateFormat.is24HourFormat(this));

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
