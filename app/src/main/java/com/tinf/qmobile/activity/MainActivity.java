package com.tinf.qmobile.activity;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.activity.settings.SettingsActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.fragment.BoletimFragment;
import com.tinf.qmobile.fragment.JournalFragment;
import com.tinf.qmobile.fragment.HomeFragment;
import com.tinf.qmobile.fragment.MateriaisFragment;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.Jobs;
import com.tinf.qmobile.utility.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tinf.qmobile.network.Client.pos;

public class MainActivity extends AppCompatActivity implements OnResponse, OnEvent, OnUpdate, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.navigation)        public BottomNavigationView bottomNav;
    @BindView(R.id.refresh_layout)    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.fab_expand)        public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_main));

        changeFragment(new HomeFragment());

        if (Client.get().isLogging()) {
            Client.get().load(PG_DIARIOS);
            Client.get().load(PG_BOLETIM);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        /*final MenuItem menuMsgs = menu.findItem(R.id.action_messages);

        FrameLayout layout = (FrameLayout) menu.findItem(R.id.action_messages).getActionView();

        TextView badge = (TextView) layout.findViewById(R.id.messages_badge);

        int count = 3;

        if (badge != null) {
            if (count == 0) {
                if (badge.getVisibility() != View.GONE) {
                    badge.setVisibility(View.GONE);
                }
            } else {
                badge.setText(String.valueOf(Math.min(count, 99)));
                if (badge.getVisibility() != View.VISIBLE) {
                    badge.setVisibility(View.VISIBLE);
                }
            }
        }

        layout.setOnClickListener(v -> onOptionsItemSelected(menuMsgs));*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                onBackPressed();
                return true;

            case R.id.action_settings:

                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_logout:

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_quit))
                        .setMessage(R.string.dialog_quit_msg)
                        .setPositiveButton(R.string.dialog_quit, (dialog, which) -> logOut())
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;

            case R.id.action_date:

                View view = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

                final NumberPicker year = (NumberPicker) view.findViewById(R.id.year_picker);

                year.setMinValue(0);
                year.setMaxValue(User.getYears().length - 1);
                year.setValue(Client.pos);
                year.setDisplayedValues(User.getYears());
                year.setWrapSelectorWheel(false);

                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setTitle(getResources().getString(R.string.dialog_date_change))
                        .setPositiveButton(R.string.dialog_date_confirm, (dialog, which) -> {

                            Client.pos = year.getValue();
                            Client.get().requestUpdate();

                        }).setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;

            case R.id.action_grades:

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);

                if (fragment instanceof JournalFragment) {
                    return changeFragment(new BoletimFragment());

                } else if (fragment instanceof BoletimFragment) {
                    return changeFragment(new JournalFragment());

                } else return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        if (item.getItemId() != bottomNav.getSelectedItemId()) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_notas:
                    fragment = new JournalFragment();
                    putBadge(R.id.navigation_notas, 0);
                    break;

                case R.id.navigation_materiais:
                    fragment = new MateriaisFragment();
                    break;
            }
        } else {
            Client.get().requestScroll();
        }
        return changeFragment(fragment);
    }

    protected void showProgressbar() {
        refreshLayout.setRefreshing(true);
    }

    public void dismissProgressbar() {
        refreshLayout.setRefreshing(false);
    }

    private void logOut() {
        Client.get().logOut();
        Jobs.cancellAllJobs();
        ((App) getApplication()).logOut();
        User.clearInfos();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private boolean changeFragment(Fragment fragment) {
        if (fragment != null) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

            if (fragment instanceof HomeFragment) {
                setTitle(User.getName());
            } else {
                setTitle(User.getYears()[pos]);
            }

            transaction.replace(R.id.main_fragment, fragment).commit();

            fab.hide();
            return true;
        }
        return false;
    }

    private void reload() {
        if (Client.isConnected()) {
            switch (bottomNav.getSelectedItemId()) {
                case R.id.navigation_home: Client.get().login();
                    break;
                case R.id.navigation_notas: Client.get().load(PG_DIARIOS);
                                            Client.get().load(PG_BOLETIM);
                    break;
                case R.id.navigation_materiais: Client.get().load(PG_MATERIAIS);
                    break;
            }
        } else {
            dismissProgressbar();
        }
    }

    public void putBadge(@IdRes int itemId, int count) {
        BottomNavigationItemView itemView = bottomNav.findViewById(itemId);
        View badge = LayoutInflater.from(getApplicationContext()).inflate(R.layout.badge_notification, bottomNav, false);

        if (count > 0) {
            TextView text = badge.findViewById(R.id.badge_text);
            text.setText(String.valueOf(count));

            if (itemView.getChildCount() < 3) {
                itemView.addView(badge);
            }
        } else {
            if (itemView.getChildCount() == 3) {
                itemView.removeViewAt(2);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        //appBarLayout.addOnOffsetChangedListener(this);
        Client.get().addOnResponseListener(this);
        Client.get().addOnUpdateListener(this);
        Client.get().setOnEventListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        Client.get().removeOnResponseListener(this);
        Client.get().removeOnUpdateListener(this);
        Client.get().setOnEventListener(null);
        //appBarLayout.removeOnResponseListener(this);
        dismissProgressbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        //appBarLayout.addOnOffsetChangedListener(this);
        Client.get().addOnResponseListener(this);
        Client.get().addOnUpdateListener(this);
        Client.get().setOnEventListener(this);
        bottomNav.setOnNavigationItemSelectedListener(this);
        refreshLayout.setOnRefreshListener(this::reload);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        Client.get().removeOnResponseListener(this);
        Client.get().removeOnUpdateListener(this);
        Client.get().setOnEventListener(null);
        //appBarLayout.removeOnResponseListener(this);
        dismissProgressbar();
    }

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() != R.id.navigation_home && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart(int pg, int pos) {
        if (!refreshLayout.isRefreshing()) {
            showProgressbar();
        } /*else {
            dismissProgressbar();
        }*/
    }

    @Override
    public void onFinish(int pg, int pos) {
        dismissProgressbar();
    }

    @Override
    public void onError(int pg, String error) {
        dismissProgressbar();
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        dismissProgressbar();

        if (pg == PG_LOGIN) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(getResources().getString(R.string.dialog_access_changed))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.dialog_try_again), (dialogInterface, i) -> Client.get().login())
                    .setNegativeButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> logOut())
                    .setNeutralButton(getResources().getString(R.string.dialog_continue_offline), null)
                    .create()
                    .show();

        } else if (pg == PG_ACESSO_NEGADO) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.action_logout), (dialogInterface, i) -> logOut())
                    .create()
                    .show();

        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Client.get().requestUpdate();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_permission_denied), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*public void callOnUpdate(int pg) {
        Log.v(TAG, "Update: " + pg);

        if (bottomNav.getSelectedItemId() != R.id.navigation_home) {
            setTitle(User.getYears()[pos]);
        }

        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onUpdate(pg);
            }
        }
    }*/



    @Override
    public void onMessage(int count) {

    }

    @Override
    public void onRenewalAvailable() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.dialog_renewal_title))
                .setMessage(getResources().getString(R.string.dialog_renewal_txt))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.dialog_open_site), (dialogInterface, i) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(URL + INDEX + PG_LOGIN));
                    startActivity(browserIntent);
                })
                .setNegativeButton(getResources().getString(R.string.dialog_later), null)
                .create()
                .show();
    }

    @Override
    public void onJournal(int count) {
        putBadge(R.id.navigation_notas, count);
    }

    @Override
    public void onUpdate(int pg) {
        if (bottomNav.getSelectedItemId() != R.id.navigation_home) {
            setTitle(User.getYears()[pos]);
        }
    }

    @Override
    public void onScrollRequest() {

    }

    /*@Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(i == 0 ?
                    (4 * getResources().getDisplayMetrics().density) :
                    (0 * getResources().getDisplayMetrics().density));
        }
    }*/
}
