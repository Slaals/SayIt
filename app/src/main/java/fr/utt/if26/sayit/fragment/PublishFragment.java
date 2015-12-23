package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.adapter.LanguageSpinnerAdapter;
import fr.utt.if26.sayit.bean.Country;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class PublishFragment extends Fragment {

    private final int charLimitation = 500;

    private TextView publishButtonView;
    private EditText expressionFieldView;
    private Spinner langageSpinnerView;
    private TextView charLimitationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface PublishButtonOnClickListener {
        void onPublishButtonClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        publishButtonView = (TextView) view.findViewById(R.id.publishButton);
        expressionFieldView = (EditText) view.findViewById(R.id.expressionField);
        langageSpinnerView = (Spinner) view.findViewById(R.id.langageSpinner);
        charLimitationView = (TextView) view.findViewById(R.id.charLimitationView);

        charLimitationView.setText(charLimitation + "");

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lighthouse.ttf");
        publishButtonView.setTypeface(font);
        publishButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                publishButtonView.setEnabled(false);

                SharedPreferences sharedPreferences = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
                String token = sharedPreferences.getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

                // Call the SignIn endpoint of ItSays API
                ItSaysEndpoints.PublicationEndpoints.publication(token, expressionFieldView.getText().toString(), ((Country) langageSpinnerView.getSelectedItem()).getIsoCode(), getContext(), new ApiHttpClient.ApiCallFinished() {
                    @Override
                    public void onApiCallSucceeded(JSONObject response) {
                        // Show a prompt indicating the publish has succeeded
                        Toast toast = Toast.makeText(getContext(), R.string.publicationsPublishedSuccessfully, Toast.LENGTH_LONG);
                        toast.show();

                        if (getActivity() instanceof PublishButtonOnClickListener) {
                            ((PublishButtonOnClickListener) getActivity()).onPublishButtonClick();
                        } else {
                            throw new IllegalStateException("The activity " + getActivity().getClass().getSimpleName() + " (" + SignInFragment.class.getSimpleName() + " parent's) must implement " + PublishButtonOnClickListener.class.getSimpleName());
                        }
                    }

                    @Override
                    public void onApiCallCompleted() {
                        publishButtonView.setEnabled(true);
                    }

                    @Override
                    public void onApiCallFailed(JSONObject response) {
                    }
                });
            }
        });

        expressionFieldView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int diff = charLimitation - charSequence.length();
                if (diff < 0) {
                    expressionFieldView.setText(charSequence.subSequence(0, (charLimitation - 1)));
                    expressionFieldView.setSelection(charLimitation - 1);
                } else {
                    charLimitationView.setText("" + diff);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ArrayList<Country> countryList = new ArrayList<>();
        Collections.addAll(countryList, Country.values());
        langageSpinnerView.setAdapter(new LanguageSpinnerAdapter(getContext(), R.layout.spinner_langage_item, countryList));

        return view;
    }
}
