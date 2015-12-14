package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.SayItApplication;
import fr.utt.if26.sayit.activity.MainActivity;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class SignUpFragment extends Fragment {

    private TextView usernameView;
    private TextView password1View;
    private TextView password2View;
    private LinearLayout signUpButtonView;
    private TextView signUpButtonLabelView;
    private ProgressBar progressBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        usernameView = (TextView) view.findViewById(R.id.signUpUsername);
        password1View = (TextView) view.findViewById(R.id.signUpPassword1);
        password2View = (TextView) view.findViewById(R.id.signUpPassword2);
        progressBarView = (ProgressBar) view.findViewById(R.id.signUpButtonProgressBar);
        signUpButtonView = (LinearLayout) view.findViewById(R.id.signUpButtonFromSignup);
        signUpButtonLabelView = (TextView) view.findViewById(R.id.signUpButtonLabelFromSignup);

        signUpButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = usernameView.getText().toString();
                final String password1 = password1View.getText().toString();
                String password2 = password2View.getText().toString();

                if (!password1.equals(password2)) {
                    Toast toast = Toast.makeText(SignUpFragment.this.getContext(), R.string.signUpFailedPasswordDoesntMatch, Toast.LENGTH_LONG);
                    toast.show();
                    progressBarView.setVisibility(View.GONE);
                    signUpButtonView.setEnabled(true);
                    signUpButtonLabelView.setText(R.string.action_sign_up);
                    return;
                }

                // Call the SignIn endpoint of ItSays API
                ItSaysEndpoints.UserEndpoint.signup(username, password1, getContext(), new ApiHttpClient.ApiCallFinished() {
                    @Override
                    public void onApiCallCompleted() {
                        progressBarView.setVisibility(View.VISIBLE);
                        signUpButtonView.setEnabled(false);
                        signUpButtonLabelView.setText(R.string.action_sign_up_loading);
                    }

                    @Override
                    public void onApiCallSucceeded(JSONObject jsonObjectSignUp) {
                        /*
                        After the user has successfully signed up, the application automatically call
                        the /signin endpoint and let the user be authentified with the credentials he put
                        into the sign up fields
                        */
                        ItSaysEndpoints.UserEndpoint.signin(username, password1, getContext(), new ApiHttpClient.ApiCallFinished() {
                            @Override
                            public void onApiCallCompleted() {
                                progressBarView.setVisibility(View.GONE);
                                signUpButtonView.setEnabled(true);
                                signUpButtonLabelView.setText(R.string.action_sign_up);
                            }

                            @Override
                            public void onApiCallSucceeded(JSONObject jsonObjectSignIn) {
                                try {
                                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    // Save permanent token and username into shared preferences
                                    editor.putString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, jsonObjectSignIn.getString("token"));
                                    editor.putString(SharedPreferencesManager.USER_PREFERENCES_USERNAME, jsonObjectSignIn.getString("id"));

                                    editor.apply();

                                    // Show a prompt indicating the user has successfully signed up and signed in
                                    Toast toast = Toast.makeText(SignUpFragment.this.getContext(), R.string.signUpSucceeded, Toast.LENGTH_LONG);
                                    toast.show();

                                    Intent openMainActivity = new Intent(getContext(), MainActivity.class);
                                    startActivity(openMainActivity);
                                    getActivity().finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onApiCallFailed(JSONObject response) {
                            }
                        });
                    }

                    @Override
                    public void onApiCallFailed(JSONObject response) {
                    }
                });
            }
        });

        return view;
    }
}