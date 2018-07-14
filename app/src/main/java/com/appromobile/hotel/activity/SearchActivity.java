package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.SearchHistoryAdapter;
import com.appromobile.hotel.db.search.SearchHistoryDao;
import com.appromobile.hotel.model.view.SearchHistoryEntry;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xuan on 7/6/2016.
 */
public class SearchActivity extends BaseActivity implements SearchHistoryAdapter.DeleteItemListener {
    private ListView lvHistory;
    SearchHistoryAdapter searchHistoryAdapter;
    SearchHistoryDao searchHistoryDao;
    EditTextSFRegular txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        }catch (Exception e){}

        setContentView(R.layout.search_activity);
        lvHistory =  findViewById(R.id.lvHistory);
        txtSearch =  findViewById(R.id.txtSearch);
        searchHistoryDao = new SearchHistoryDao();

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    searchCallback((String) searchHistoryAdapter.getItem(position));
                } catch (Exception e) {
                }
            }
        });
        initData();

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSearch.getText().toString().equals("")) {
                    return;
                } else {
                    searchHistoryDao.insertDownload(txtSearch.getText().toString());
                    searchCallback(txtSearch.getText().toString());
                    Utils.hideKeyboard(SearchActivity.this);
                }
            }
        });
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Utils.hideKeyboard(SearchActivity.this);
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!txtSearch.getText().toString().equals("")) {
                        searchHistoryDao.insertDownload(txtSearch.getText().toString());
                        searchCallback(txtSearch.getText().toString());
                        Utils.hideKeyboard(SearchActivity.this);
                    }

                    handled = true;
                }
                return handled;
            }
        });
    }

    private void searchCallback(String textSearch) {
        Intent intent = new Intent(this, HotelSearchResultActivity.class);
        intent.putExtra("SearchText", textSearch);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
//        setResult(RESULT_OK, intent);
//        finish();
    }

    private void initData() {
        List<SearchHistoryEntry> historyEntries = searchHistoryDao.getListAll();
        searchHistoryAdapter = new SearchHistoryAdapter(this, historyEntries, this, searchHistoryDao);
        lvHistory.setAdapter(searchHistoryAdapter);
    }

    @Override
    public void onDeleted() {
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName="SSearch";
    }
}
