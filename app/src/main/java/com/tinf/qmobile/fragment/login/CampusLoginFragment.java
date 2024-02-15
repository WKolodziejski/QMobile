package com.tinf.qmobile.fragment.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.LoginCampusAdapter;
import com.tinf.qmobile.databinding.FragmentLoginCampusBinding;
import com.tinf.qmobile.network.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CampusLoginFragment extends Fragment {
  private static final String TAG = "CampusLoginFragment";
  private FragmentLoginCampusBinding binding;
  private FirebaseRemoteConfig remoteConfig;
  private Map<String, String> urls;

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    remoteConfig = FirebaseRemoteConfig.getInstance();
  }

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_login_campus, container, false);
    binding = FragmentLoginCampusBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    remoteConfig.setDefaultsAsync(R.xml.urls_map).addOnSuccessListener(t -> {
      List<String> campus = new ArrayList<>();

      LoginCampusAdapter adapter = new LoginCampusAdapter(getContext(), i -> {
        Client.get().setURL(urls.get(campus.get(i)));
        Log.d(TAG, Client.get().getURL());

        Bundle bundle = new Bundle();
        bundle.putInt("I", i);
        Fragment fragment = new CredentialsLoginFragment();
        fragment.setArguments(bundle);

        getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.login_fragment, fragment)
            .addToBackStack(null)
            .commit();
      });

      urls = new Gson().fromJson(remoteConfig.getString("urls"),
                                 new TypeToken<Map<String, String>>() {
                                 }.getType());

      campus.addAll(urls.keySet());
      adapter.onUpdate(campus);

      binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
      binding.recycler.setHasFixedSize(false);
      binding.recycler.setNestedScrollingEnabled(false);
      binding.recycler.setAdapter(adapter);
    }).addOnFailureListener(e -> {
      Toast.makeText(getContext(), getResources()
          .getString(R.string.toast_firebase_fail), Toast.LENGTH_LONG).show();
      FirebaseCrashlytics.getInstance().recordException(e);
    });
  }
}
