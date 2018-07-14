package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RoomTypeForm;

import com.appromobile.hotel.model.view.RoomView;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.PictureUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;


/**
 * Created by xuan on 7/4/2016.
 */
public class ChooseRoomTypeAdapter extends BaseAdapter {
    private HotelDetailForm data;
    private Context context;
    private ListView lvParent;
    private int selectedIndex;

    public ChooseRoomTypeAdapter(final Context context, HotelDetailForm data, ListView lvParent, int selectedIndex) {
        this.data = data;
        this.context = context;
        this.lvParent = lvParent;
        this.selectedIndex = selectedIndex;
        PictureUtils.getInstance().clearCache(context);
    }

    @Override
    public int getCount() {
        if (data.getRoomTypeList() != null) {
            return data.getRoomTypeList().size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.choose_romtype_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvPriceStatus = convertView.findViewById(R.id.tvPriceStatus);
            viewHolder.tvPriceHourlyNormal = convertView.findViewById(R.id.tvPriceHourlyNormal);
            viewHolder.tvPriceHourlyDiscount = convertView.findViewById(R.id.tvPriceHourlyDiscount);
            viewHolder.tvPriceOvernightNormal = convertView.findViewById(R.id.tvPriceOvernightNormal);
            viewHolder.tvPriceOvernightDiscount = convertView.findViewById(R.id.tvPriceOvernightDiscount);

            viewHolder.tvRoomName = convertView.findViewById(R.id.tvRoomName);
            viewHolder.imgChecked = convertView.findViewById(R.id.imgChecked);
            viewHolder.imgRoom = convertView.findViewById(R.id.imgRoom);
            viewHolder.txtLocked = convertView.findViewById(R.id.roomtype_locked);

            viewHolder.boxHourly = convertView.findViewById(R.id.boxHourly);

            viewHolder.imgFlashSale = convertView.findViewById(R.id.roomty_flashsale);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        final RoomTypeForm roomTypeForm = data.getRoomTypeList().get(position);
        if (roomTypeForm != null) {
            viewHolder.tvRoomName.setText(roomTypeForm.getName());


            //Check Flash Sale
            if (roomTypeForm.isFlashSale()) {

                viewHolder.imgFlashSale.setVisibility(View.VISIBLE);
                viewHolder.imgFlashSale.setImageResource(R.drawable.icon_sale);

                int rooms = roomTypeForm.getAvailableRoom();
                String s;
                if (rooms > 0) {
                    s = String.format(context.getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                } else {
                    s = context.getString(R.string.txt_2_flashsale_sold_out);
                }
                //Set Price Status
                viewHolder.tvPriceStatus.setVisibility(View.VISIBLE);
                viewHolder.tvPriceStatus.setText(s);

                viewHolder.boxHourly.setVisibility(View.GONE);

                //Set Price Overnight Normal
                viewHolder.tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                viewHolder.tvPriceOvernightNormal.setText(Utils.formatCurrency(data.getLowestPriceOvernight()));
                //StrikeThrough
                viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                //Set Price Overnight Discount
                viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

            } else {

                viewHolder.imgFlashSale.setVisibility(View.GONE);
                viewHolder.boxHourly.setVisibility(View.VISIBLE);

                //--------------Set Price------------
                int[] discount = Utils.getPromotionInfoForm(roomTypeForm.getHotelSn());

                if (discount[0] > 0 || discount[1] > 0) {

                    //Set Price Status
                    viewHolder.tvPriceStatus.setText(context.getString(R.string.txt_2_coupon_applied));

                    //Hourly
                    if (discount[0] > 0) {
                        int priceHourlyDiscount = roomTypeForm.getPriceFirstHours() - discount[0];

                        if (priceHourlyDiscount < 0) {
                            priceHourlyDiscount = 0;
                        }

                        //Set Price Hourly Normal
                        viewHolder.tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                        viewHolder.tvPriceHourlyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));
                        //StrikeThrough
                        viewHolder.tvPriceHourlyNormal.setPaintFlags(viewHolder.tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        //Set Price Hourly Discount
                        viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                    } else {

                        //Set Price Hourly Normal
                        viewHolder.tvPriceHourlyNormal.setVisibility(View.GONE);
                        //Set Price Hourly Discount
                        viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));

                    }

                    //Overnight
                    if (discount[1] > 0) {
                        int priceOvernightDiscount = roomTypeForm.getPriceOvernight() - discount[1];

                        if (priceOvernightDiscount < 0) {
                            priceOvernightDiscount = 0;
                        }

                        //Set Price Overnight Normal
                        viewHolder.tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                        viewHolder.tvPriceOvernightNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                        //StrikeThrough
                        viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        //Set Price Overnight Discount
                        viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                    } else {
                        //Set Price Overnight Normal
                        viewHolder.tvPriceOvernightNormal.setVisibility(View.GONE);

                        //Set Price Overnight Discount
                        viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                    }

                } else {

                    //Set Price Status
                    viewHolder.tvPriceStatus.setVisibility(View.GONE);

                    //Set Price Hourly Normal
                    viewHolder.tvPriceHourlyNormal.setVisibility(View.GONE);
                    //Set Price Hourly Discount
                    viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));

                    //Set Price Overnight Normal
                    viewHolder.tvPriceOvernightNormal.setVisibility(View.GONE);

                    //Set Price Overnight Discount
                    viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

                }

            }

            final String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + data.getRoomTypeList().get(position).getHomeImageSn() + "&fileName=" + data.getRoomTypeList().get(position).getHomeImageSn();
            PictureUtils.getInstance().load(
                    url,
                    context.getResources().getDimensionPixelSize(R.dimen.roomtype_height),
                    context.getResources().getDimensionPixelSize(R.dimen.roomtype_height),
                    R.drawable.loading_big,
                    viewHolder.imgRoom
            );
        }

        if (this.selectedIndex == position) {
            viewHolder.imgChecked.setImageResource(R.drawable.checkbox_selected);
        } else {
            viewHolder.imgChecked.setImageResource(R.drawable.checkbox);
        }

        /*
        * Check Room Type Locked.
        */
        if (roomTypeForm != null && roomTypeForm.isLocked()) {
            viewHolder.txtLocked.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtLocked.setVisibility(View.GONE);

        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tvRoomName;

        private ImageView imgRoom;
        private ImageView imgChecked;
        private TextView txtLocked;

        private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount;
        private LinearLayout boxHourly;
        private ImageView imgFlashSale;

    }

}
