package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.activity.MainActivity;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class SignInFragment extends Fragment {

    public interface SignUpButtonClickListener {
        void onSignUpButtonClicked();
    }

    private TextView usernameView;
    private TextView passwordView;
    private LinearLayout signInButtonView;
    private LinearLayout signUpButtonView;
    private TextView signInButtonLabelView;
    private ProgressBar progressBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        usernameView = (TextView) view.findViewById(R.id.signUpUsername);
        passwordView = (TextView) view.findViewById(R.id.loginPassword);
        progressBarView = (ProgressBar) view.findViewById(R.id.loginProgressBar);
        signUpButtonView = (LinearLayout) view.findViewById(R.id.signUpButton);
        signInButtonView = (LinearLayout) view.findViewById(R.id.signInButton);
        signInButtonLabelView = (TextView) view.findViewById(R.id.signInButtonLabel);

        // Start logo animation
        Animation bounceAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.findViewById(R.id.logoSayItLogin).startAnimation(bounceAnimation);

        signInButtonView = (LinearLayout) view.findViewById(R.id.signInButton);
        signInButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();

                progressBarView.setVisibility(View.VISIBLE);
                signInButtonView.setEnabled(false);
                signInButtonLabelView.setText(R.string.action_sign_in_loading);

                // Call the SignIn endpoint of ItSays API
                ItSaysEndpoints.UserEndpoints.signin(username, password, getContext(), new ApiHttpClient.ApiCallFinished() {
                    @Override
                    public void onApiCallCompleted() {
                        progressBarView.setVisibility(View.GONE);
                        signInButtonView.setEnabled(true);
                        signInButtonLabelView.setText(R.string.action_sign_in);
                    }

                    @Override
                    public void onApiCallSucceeded(JSONObject jsonObject) {
                        try {
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Save permanent token and username into shared preferences
                            editor.putString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, jsonObject.getString("token"));
                            editor.putString(SharedPreferencesManager.USER_PREFERENCES_USERNAME, jsonObject.getString("username"));

                            editor.apply();

                            // Show a prompt indicating the user has successfully signed up and signed in
                            Toast toast = Toast.makeText(getContext(), R.string.signInSucceeded, Toast.LENGTH_LONG);
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
        });

        signUpButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof SignUpButtonClickListener) {
                    ((SignUpButtonClickListener) getActivity()).onSignUpButtonClicked();
                } else {
                    throw new IllegalStateException("The activity " + getActivity().getClass().getSimpleName() + " (" + SignInFragment.class.getSimpleName() + " parent's) must implement " + SignUpButtonClickListener.class.getSimpleName());
                }
            }
        });

        return view;
    }
}