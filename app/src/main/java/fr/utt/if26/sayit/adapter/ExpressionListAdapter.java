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
import fr.utt.if26.sayit.bean.ExpressionItem;

public class ExpressionListAdapter extends ArrayAdapter<ExpressionItem> {

    Context mContext;
    int layoutResourceId;
    List<ExpressionItem> data = null;

    public ExpressionListAdapter(Context mContext, int layoutResourceId, List<ExpressionItem> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        ExpressionItem expressionItem = data.get(position);
        TextView textViewItem = (TextView) convertView.findViewById(R.id.expressionListTextViewItem);
        ImageView imageViewItem = (ImageView) convertView.findViewById(R.id.expressionListImageViewItem);
        imageViewItem.setImageDrawable(mContext.getResources().getDrawable(expressionItem.getCountry().getDrawableResource()));
        textViewItem.setText(expressionItem.getItemName());

        return convertView;
    }
}