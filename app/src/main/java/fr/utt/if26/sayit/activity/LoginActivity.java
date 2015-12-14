package fr.utt.if26.sayit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.fragment.SignInFragment;
import fr.utt.if26.sayit.fragment.SignUpFragment;

public class LoginActivity extends FragmentActivity implements
        SignInFragment.SignUpButtonClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        navigateToSignInScreen();
    }

    private void navigateToSignInScreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SignInFragment signInFragment = new SignInFragment();
        fragmentTransaction.replace(R.id.loginWrapperLayout, signInFragment);
        fragmentTransaction.commit();
    }

    private void navigateToSignUpScreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SignUpFragment signUpFragment = new SignUpFragment();
        fragmentTransaction.replace(R.id.loginWrapperLayout, signUpFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSignUpButtonClicked() {
        navigateToSignUpScreen();
    }
}

