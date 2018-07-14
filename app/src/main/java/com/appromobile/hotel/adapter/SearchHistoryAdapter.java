package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.db.search.SearchHistoryDao;
import com.appromobile.hotel.model.view.SearchHistoryEntry;

import java.util.List;

/**
 * Created by xuan on 8/29/2016.
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<SearchHistoryEntry> searchHistoryEntries;
    private DeleteItemListener deleteItemListener;
    private SearchHistoryDao searchHistoryDao;

    public SearchHistoryAdapter(Context context, List<SearchHistoryEntry> searchHistoryEntries, DeleteItemListener deleteItemListener, SearchHistoryDao searchHistoryDao) {
        this.context = context;
        this.searchHistoryEntries = searchHistoryEntries;
        this.deleteItemListener = deleteItemListener;
        this.searchHistoryDao = searchHistoryDao;
    }

    @Override
    public int getCount() {
        if (this.searchHistoryEntries != null) {
            return this.searchHistoryEntries.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return this.searchHistoryEntries.get(position).getText();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.search_history_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvText =  convertView.findViewById(R.id.tvText);
            viewHolder.btnDelete =  convertView.findViewById(R.id.btnDelete);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvText.setText(this.searchHistoryEntries.get(position).getText());
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistoryDao.deleteItem(searchHistoryEntries.get(position).getId());
                deleteItemListener.onDeleted();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView tvText;
        ImageView btnDelete;
    }

    public interface DeleteItemListener{
        void onDeleted();
    }
}
