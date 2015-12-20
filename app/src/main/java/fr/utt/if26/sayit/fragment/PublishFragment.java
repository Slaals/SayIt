package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.adapter.languageSpinnerAdapter;
import fr.utt.if26.sayit.bean.Country;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class PublishFragment extends Fragment {

    private TextView publishButtonView;
    private TextView expressionFieldView;
    private Spinner langageSpinnerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        publishButtonView = (TextView) view.findViewById(R.id.publishButton);
        expressionFieldView = (TextView) view.findViewById(R.id.expressionField);
        langageSpinnerView = (Spinner) view.findViewById(R.id.langageSpinner);
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

        ArrayList<Country> countryList = new ArrayList<>();
        Collections.addAll(countryList, Country.values());
        langageSpinnerView.setAdapter(new languageSpinnerAdapter(getContext(), R.layout.spinner_langage_item, countryList));

        return view;
    }
}
