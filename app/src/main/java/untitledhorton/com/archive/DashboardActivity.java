package untitledhorton.com.archive;

import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class DashboardActivity extends AppCompatActivity {

    BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noNavBarGoodness();
        mBottomBar.setItems(R.menu.bottombar_tabs);




        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user selected item number one.
                   getSupportActionBar().setTitle("Reminders");
                } else if (menuItemId == R.id.bottomBarItemTwo){
                   getSupportActionBar().setTitle("Tasks");
                } else if (menuItemId == R.id.bottomBarItemThree){
                   getSupportActionBar().setTitle("Events");
                } else if (menuItemId == R.id.bottomBarItemFour){
                    CalendarFragment f = new CalendarFragment();
                    android.app.FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.myCoordinator,f).commit();
                    getSupportActionBar().setTitle("Calendar");
                } else if (menuItemId == R.id.bottomBarItemFive){
                    getSupportActionBar().setTitle("Classes");
                } else{
                    ProfileFragment f = new ProfileFragment();
                    android.app.FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.myCoordinator,f).commit();
                    getSupportActionBar().setTitle("Profile");

                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    // The user reselected item number one, scroll your content to top.
                }else if (menuItemId == R.id.bottomBarItemTwo){

                } else if (menuItemId == R.id.bottomBarItemThree){

                } else if (menuItemId == R.id.bottomBarItemFour){

                } else if (menuItemId == R.id.bottomBarItemFive){

                } else{

                }
            }
        });

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(2, "#7B1FA2");
        mBottomBar.mapColorForTab(3, "#FF5252");
        mBottomBar.mapColorForTab(4, "#FF9800");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }
}
