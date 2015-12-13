package fr.utt.if26.sayit.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.utt.if26.sayit.R;

public class PublishFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        TextView publishButton = (TextView) view.findViewById(R.id.publishButton);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lighthouse.ttf");
        publishButton.setTypeface(font);
        return view;
    }
}
