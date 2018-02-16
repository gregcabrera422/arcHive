package untitledhorton.com.archive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReminderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderFragment extends Fragment implements  View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    DatabaseReference mDatabase, insidemDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FloatingActionButton fab;
    ArrayList<Reminder> reminders;
    SwipeMenuListView lvReminders;
    CustomReminderAdapter reminderAdapter;
    EditText etTitle, etMessage;
    Button btnDateTime, btnDone, btnClose;
    TextView txtDateTime, lblTitle, lblMessage, lblDateTime, lblDialogTitle;
    private int day, month, year, hour, minute, finalDay, finalMonth, finalYear, finalHour, finalMinute;
    private String date, time;

    public ReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderFragment newInstance(String param1, String param2) {
        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);

        lvReminders = v.findViewById(R.id.lvReminders);
        reminders = new ArrayList<Reminder>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reminders").child(user.getUid());
        fab = v.findViewById(R.id.fab);
        fab.attachToListView(lvReminders);
        reminderAdapter = new CustomReminderAdapter(getActivity(), reminders);

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
                        getActivity());
                editItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0x66,
                        0xff)));
                editItem.setWidth(200);
                editItem.setTitle("EDIT");
                editItem.setTitleSize(18);
                editItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());

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
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_reminder, null);
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

                                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), ReminderFragment.this, year, month, day);
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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_reminder, null);
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
                Intent intent = new Intent(getActivity(), ReminderActivity.class);
                startActivity(intent);
            }
        });

        return v;
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
                new TimePickerDialog(getActivity(), ReminderFragment.this, hour, minute, DateFormat.is24HourFormat(getActivity()));

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

    @Override
    public void onClick(View v){

        startActivity(new Intent(getActivity(), ReminderActivity.class));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
