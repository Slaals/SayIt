package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

    public interface ExpressionItemListener {
        void onItemClickListener(String id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final AdapterView.OnItemClickListener expressionListenner = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ExpressionItem expr = (ExpressionItem)adapterView.getItemAtPosition(i);

                if (getActivity() instanceof ExpressionItemListener) {
                    ((ExpressionItemListener) getActivity()).onItemClickListener(expr.getExprId());
                } else {
                    throw new IllegalStateException("The activity " + getActivity().getClass().getSimpleName() + " (" + SignInFragment.class.getSimpleName() + " parent's) must implement " + ExpressionItemListener.class.getSimpleName());
                }
            }
        };

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_expression_list, container, false);

        String accessToken = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE)
                .getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

        ItSaysEndpoints.PublicationEndpoints.publications(accessToken, getContext(), new ApiHttpClient.ApiCallFinished() {
            @Override
            public void onApiCallCompleted() {
            }

            @Override
            public void onApiCallSucceeded(JSONObject jsonObjectPublications) {
                ArrayList<ExpressionItem> publicationList = new ArrayList<>();
                try {
                    JSONArray publicationArray = jsonObjectPublications.getJSONArray("publications");
                    for (int i = 0; i < publicationArray.length(); i++) {
                        JSONObject currentPublicationNode = publicationArray.getJSONObject(i);
                        JSONArray audio = currentPublicationNode.getJSONArray("audio");
                        ExpressionItem exprItem = new ExpressionItem(
                                currentPublicationNode.getString("text"),
                                Country.getByIsoCode(currentPublicationNode.getString("language")),
                                audio.length(),
                                currentPublicationNode.getString("_id")
                        );
                        publicationList.add(exprItem);
                    }
                    ListView listView = (ListView) view.findViewById(R.id.expressionListListView);
                    listView.setAdapter(new ExpressionListAdapter(getActivity(), R.layout.item_expression_list, publicationList));

                    listView.setOnItemClickListener(expressionListenner);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiCallFailed(JSONObject response) {
            }
        });
        return view;
    }
}
