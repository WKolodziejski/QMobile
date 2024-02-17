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
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.LoginCampusAdapter;
import com.tinf.qmobile.databinding.FragmentLoginCampusBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class CampusLoginFragment extends Fragment {
  private static final String TAG = "CampusLoginFragment";
  private FragmentLoginCampusBinding binding;
  private FirebaseRemoteConfig remoteConfig;

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
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
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

    remoteConfig.setDefaultsAsync(R.xml.webapp_map)
                .addOnSuccessListener(runnable -> {
                  remoteConfig.setDefaultsAsync(R.xml.urls_map)
                              .addOnSuccessListener(this::renderList)
                              .addOnFailureListener(this::onFailure);
                })
                .addOnFailureListener(this::onFailure);
  }

  private void renderList(Void t) {
    List<String> campusList = new ArrayList<>(UserUtils.getUrlMap().keySet());

    LoginCampusAdapter adapter = new LoginCampusAdapter(getContext(), i -> {
      // Seleciona o campus clicado
      String campus = campusList.get(i);
      // Busca a url do campus
      String url = UserUtils.getUrlMap().get(campus);
      // Define se o campus deve usar versão webapp
      boolean webapp = UserUtils.getWebappList().contains(campus);

      // Salva dados
      UserUtils.setURL(url);
      UserUtils.setCampus(campus);
      UserUtils.setWebapp(webapp);

      Client.get().setURL(url);

      Log.d(TAG, campus + ": " + url);
      Log.d(TAG, "WEBAPP: " + webapp);

      getParentFragmentManager()
          .beginTransaction()
          .replace(R.id.login_fragment, new CredentialsLoginFragment())
          .commitNow();
    });

    adapter.onUpdate(campusList);

    binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.recycler.setHasFixedSize(false);
    binding.recycler.setNestedScrollingEnabled(false);
    binding.recycler.setAdapter(adapter);
  }

  private void onFailure(Exception e) {
    Toast.makeText(getContext(), getResources()
             .getString(R.string.toast_firebase_fail), Toast.LENGTH_LONG)
         .show();
    FirebaseCrashlytics.getInstance()
                       .recordException(e);
  }

}
