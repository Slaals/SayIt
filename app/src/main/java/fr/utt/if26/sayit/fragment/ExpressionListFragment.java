package fr.utt.if26.sayit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.adapter.ExpressionListAdapter;
import fr.utt.if26.sayit.bean.Country;
import fr.utt.if26.sayit.bean.ExpressionItem;

public class ExpressionListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expression_list, container, false);

        ArrayList<ExpressionItem> listCity = new ArrayList<>();
        listCity.add(new ExpressionItem("Bonjour, je suis un ours polaire à l'insu de mon plein grès", Country.FRANCE));
        listCity.add(new ExpressionItem("C'est ici le pays des coqs ?", Country.FRANCE));
        listCity.add(new ExpressionItem("Hello, where can I find George Cloney ?", Country.UNITED_STATES));
        listCity.add(new ExpressionItem("The house is bigger than the cat", Country.UNITED_STATES));
        listCity.add(new ExpressionItem("Hi brudha, what's up ?!", Country.UNITED_STATES));
        listCity.add(new ExpressionItem("I'm a scientist, and H..em.. I am wondering whether chickens had teeth ?", Country.UNITED_STATES));
        listCity.add(new ExpressionItem("Salut, je me présente, bob, ancien cosmonaute de boulevard réduit, j'aimerais savoir quelles sont les différences entre des ours polaire kozaques et des épagneuls bretons ?", Country.FRANCE));

        ListView listView = (ListView) view.findViewById(R.id.expressionListListView);
        listView.setAdapter(new ExpressionListAdapter(this.getActivity(), R.layout.item_expression_list, listCity));
        return view;
    }
}
