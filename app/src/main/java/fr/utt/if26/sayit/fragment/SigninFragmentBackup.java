package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.activity.MainActivity;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class SigninFragmentBackup extends Fragment {

    private TextView usernameView;
    private TextView passwordView;
    private ProgressBar progressBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        usernameView = (TextView) view.findViewById(R.id.loginUsername);
        passwordView = (TextView) view.findViewById(R.id.loginPassword);
        progressBarView = (ProgressBar) view.findViewById(R.id.loginProgressBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button signInButton = (Button) view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();

                progressBarView.setVisibility(View.VISIBLE);

                // Call the SignIn endpoint of ItSays API
                ItSaysEndpoints.UserEndpoint.signin(username, password, new ApiHttpClient.ApiCallFinished() {
                    @Override
                    public void onApiCallFinished(JSONObject jsonObject) {
                        progressBarView.setVisibility(View.GONE);
                        try {
                            if (jsonObject.getBoolean("success")) {
                                SharedPreferences sharedPreferences = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(SharedPreferencesManager.PERMANENT_TOKEN, jsonObject.getString("token"));
                                editor.apply();
                                Intent openMainActivity = new Intent(getContext(), MainActivity.class);
                                openMainActivity.setFlags(openMainActivity.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(openMainActivity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}