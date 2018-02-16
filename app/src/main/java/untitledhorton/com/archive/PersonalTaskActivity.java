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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class PersonalTaskActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private RadioGroup radioGroup;
    private EditText etTitle, etMessage;
    private RadioButton radioButton;
    private int day, month, year, hour, minute, finalDay, finalMonth, finalYear, finalHour, finalMinute;
    private String title, message, date, time, priority;
    private TextView txtDateTime;

    DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_task);

        radioGroup = findViewById(R.id.radioGroup);
        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        txtDateTime = findViewById(R.id.txtDateTime);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PersonalTasks").child(user.getUid());

    }

    public void saveReminder(View v){
        title = etTitle.getText().toString();
        message = etMessage.getText().toString();

        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("message", message);
        result.put("date", date);
        result.put("time", time);
        result.put("priority", priority);

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

                        Intent intent = new Intent(PersonalTaskActivity.this, TaskActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Report failed to be sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        priority = radioButton.getText().toString();
        System.out.println(priority);

    }

    public void pickDateTime(View v){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalTaskActivity.this, PersonalTaskActivity.this, year, month, day);
        datePickerDialog.show();
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
                new TimePickerDialog(PersonalTaskActivity.this, PersonalTaskActivity.this, hour, minute, DateFormat.is24HourFormat(this));

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
