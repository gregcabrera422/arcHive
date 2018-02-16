package untitledhorton.com.archive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomPersonalTaskAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<PersonalTask> tasks;
    LayoutInflater inflater;

    public CustomPersonalTaskAdapter(Context mContext, ArrayList<PersonalTask> tasks) {
      this.mContext = mContext;
      this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View v = View.inflate(mContext, R.layout.custom_reminder_row, null);

        if(inflater==null)
        {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.custom_reminder_row,parent,false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        ImageView ivNotification = (ImageView) convertView.findViewById(R.id.ivNotification);

        tvTitle.setText(tasks.get(position).getTitle());
        ivNotification.setImageResource(R.drawable.ic_notifications_black_24dp);
        notifyDataSetChanged();
        return convertView;
    }

}
