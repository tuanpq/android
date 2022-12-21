package io.github.tuanpq.todomvpjava;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import io.github.tuanpq.todomvpjava.base.BaseActivity;
import io.github.tuanpq.todomvpjava.data.source.TasksRepository;
import io.github.tuanpq.todomvpjava.data.source.local.TasksLocalDataSource;
import io.github.tuanpq.todomvpjava.data.source.local.ToDoDatabase;
import io.github.tuanpq.todomvpjava.databinding.ActivityMainBinding;
import io.github.tuanpq.todomvpjava.tasklist.TaskListFragment;
import io.github.tuanpq.todomvpjava.tasklist.TaskListPresenter;
import io.github.tuanpq.todomvpjava.utility.ActivityUtility;
import io.github.tuanpq.todomvpjava.utility.AppExecutors;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout mDrawerLayout;
    private TaskListPresenter taskListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(mDrawerLayout)
                .build();

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ToDoDatabase database = ToDoDatabase.getInstance(this);
        TasksRepository tasksRepository = TasksRepository.getInstance(TasksLocalDataSource.getInstance(new AppExecutors(),
                database.taskDAO()));
        TaskListFragment taskListFragment = new TaskListFragment();
        taskListPresenter = new TaskListPresenter(tasksRepository, taskListFragment);
        ActivityUtility.addFragmentToActivity(getSupportFragmentManager(), taskListFragment, R.id.fragment_container_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                // Do nothing, we're already on that screen
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}