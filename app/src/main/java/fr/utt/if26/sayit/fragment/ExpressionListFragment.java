package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.adapter.ExpressionListAdapter;
import fr.utt.if26.sayit.bean.Country;
import fr.utt.if26.sayit.bean.ExpressionItem;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class ExpressionListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_expression_list, container, false);

        String accessToken = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE)
                .getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

        ItSaysEndpoints.PublicationEndpoint.publications(accessToken, new ApiHttpClient.ApiCallFinished() {
            @Override
            public void onApiCallFinished(JSONObject response) {
                System.out.println(response);
                ArrayList<ExpressionItem> publicationList = new ArrayList<>();
                try {
                    JSONArray publicationArray = response.getJSONArray("publications");
                    for (int i = 0; i < publicationArray.length(); i++) {
                        JSONObject currentPublicationNode = publicationArray.getJSONObject(i);
                        publicationList.add(new ExpressionItem(currentPublicationNode.getString("text"), Country.getByIsoCode(currentPublicationNode.getString("langage"))));
                    }
                    ListView listView = (ListView) view.findViewById(R.id.expressionListListView);
                    listView.setAdapter(new ExpressionListAdapter(getActivity(), R.layout.item_expression_list, publicationList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
