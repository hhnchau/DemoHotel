package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.appromobile.hotel.R;
import com.appromobile.hotel.model.view.GoogleAddressSearchEntry;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.util.List;

/**
 * Created by xuan on 9/9/2016.
 */
public class GeoAutoCompleteAdapter extends ArrayAdapter<GoogleAddressSearchEntry> {
    private List<GoogleAddressSearchEntry> words;
    private Context context;

    public GeoAutoCompleteAdapter(Context context, List<GoogleAddressSearchEntry> words) {
        super(context, -1, words);
        this.context = context;
        this.words = words;
    }

    @Override
    public int getCount() {
        if (words != null) {
            return words.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.geo_search_result_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvName =  convertView.findViewById(R.id.tvName);
            viewHolder.tvAddress =  convertView.findViewById(R.id.tvAddress);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(words.get(position).getMain_text());
        viewHolder.tvAddress.setText(words.get(position).getSecondary_text());
        return convertView;
    }

    private class ViewHolder {
        TextViewSFRegular tvName;
        TextViewSFRegular tvAddress;
    }
}
