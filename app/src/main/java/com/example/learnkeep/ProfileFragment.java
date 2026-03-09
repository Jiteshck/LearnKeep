package com.example.learnkeep;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    TextView txtName, txtEmail;
    Button btnLogin, btnLogout;
    View profileLayout;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogout = view.findViewById(R.id.btnLogout);
        profileLayout = view.findViewById(R.id.profileLayout);

        SessionManager session =
                new SessionManager(requireContext());

        if(session.isLoggedIn()){

            btnLogin.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);

            txtName.setText(session.getName());
            txtEmail.setText(session.getEmail());

        }else{

            btnLogin.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(v ->
                startActivity(
                        new Intent(getContext(),
                                LoginActivity.class)
                )
        );

        btnLogout.setOnClickListener(v -> {

            session.logout();

            startActivity(
                    new Intent(getContext(),
                            LoginActivity.class)
            );
        });
        return view;
    }

}
