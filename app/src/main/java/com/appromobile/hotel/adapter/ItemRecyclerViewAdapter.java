package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.HotelDetailActivity;
import com.appromobile.hotel.dialog.DialogSuspend;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.view.SearchHistoryForm;

import java.util.List;

/**
 * Created by appro on 11/05/2018.
 */

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder> {
    private Context context;
    private List<SearchHistoryForm> searchList;

    public ItemRecyclerViewAdapter(Context context, List<SearchHistoryForm> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view_holder, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final SearchHistoryForm searchForm = searchList.get(position);
        if (searchForm != null) {
            holder.name.setText(searchForm.getHotelName());
            holder.address.setText(searchForm.getHotelAddress());

            if (searchForm.isFlashsale()){
                if (searchForm.getKeyword() != null){
                    holder.img.setImageResource(R.drawable.ic_history_red);
                }else {
                    holder.img.setImageResource(R.drawable.ic_promotion_red);
                }
//            }else if (searchForm.isPromotion()){
//                holder.img.setImageResource(R.drawable.ic_history_green);
            }else {
                holder.img.setImageResource(R.drawable.ic_history_grey);
            }

            holder.boxHistorySearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (searchForm.getHotelStatus() == ContractType.SUSPEND.getType()) {
                            //Suspended
                            showDialogSuspended();
                        } else {
                            Intent intent = new Intent(context, HotelDetailActivity.class);
                            intent.putExtra("sn", searchForm.getSelectedHotelSn());
                            intent.putExtra("RoomAvailable", 0);
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView name, address;
        private RelativeLayout boxHistorySearch;

        ItemViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            boxHistorySearch = itemView.findViewById(R.id.boxHistorySearch);

        }
    }

    private void showDialogSuspended() {
        DialogSuspend.getInstance().show(context);
    }

}
