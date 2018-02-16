package untitledhorton.com.archive;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class TaskActivity extends AppCompatActivity {

    private TasksPageAdapter mTasksPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mTasksPageAdapter = new TasksPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        TasksPageAdapter adapter = new TasksPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SchoolTabFragment(), "SCHOOL TASKS");
        adapter.addFragment(new PersonalTabFragment(), "PERSONAL TASKS");
        viewPager.setAdapter(adapter);
    }
}
