package com.tinf.qmobile.fragment.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.FragmentLoginWelcomeBinding;

public class WelcomeLoginFragment extends Fragment {
    private FragmentLoginWelcomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_welcome, container, false);
        binding = FragmentLoginWelcomeBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.studentBtn.setOnClickListener(view1 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment, new CampusLoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.teacherBtn.setOnClickListener(view1 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment, new UnavailableLoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.otherBtn.setOnClickListener(view1 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment, new UnavailableLoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.privacy.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/qmobileapp/política-de-privacidade"));
            startActivity(intent);
        });
    }

}
