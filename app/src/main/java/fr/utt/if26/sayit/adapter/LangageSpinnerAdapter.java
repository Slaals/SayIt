package fr.utt.if26.sayit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.bean.Country;

public class LangageSpinnerAdapter extends ArrayAdapter<Country> {

    Context mContext;
    int layoutResourceId;
    List<Country> data = null;

    public LangageSpinnerAdapter(Context mContext, int layoutResourceId, List<Country> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.spinner_langage_row, parent, false);
        TextView textViewItem = (TextView) view.findViewById(R.id.spinnerLangageRow);
        Country countryItem = data.get(position);
        textViewItem.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(countryItem.getDrawableResource()), null, null, null);
        textViewItem.setCompoundDrawablePadding(20);
        textViewItem.setText(countryItem.getStringResource());
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        Country countryItem = data.get(position);
        TextView textViewItem = (TextView) convertView.findViewById(R.id.spinnerLangageItem);
        textViewItem.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(countryItem.getDrawableResource()), null, null, null);
        textViewItem.setCompoundDrawablePadding(20);
        textViewItem.setText(countryItem.getStringResource());
        return convertView;
    }
}