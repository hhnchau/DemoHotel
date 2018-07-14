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

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.RoomTypeForm;

import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.Utils;


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
        PictureGlide.getInstance().clearCache(context);
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
            viewHolder.boxOvernight = convertView.findViewById(R.id.boxOvernight);

            viewHolder.imgFlashSale = convertView.findViewById(R.id.roomty_flashsale);

            viewHolder.txtLabelPriceHourly = convertView.findViewById(R.id.label_price_hourly);

            viewHolder.tvSupperSaleNormal = convertView.findViewById(R.id.tvSupperSaleNormal);

            viewHolder.tvSupperSaleDiscount = convertView.findViewById(R.id.tvSupperSaleDiscount);

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
                String s = "";

                int superSale = roomTypeForm.getGo2joyFlashSaleDiscount();
                int priceOvernightDiscount = roomTypeForm.getPriceOvernight();
                if (superSale > 0) {
                    priceOvernightDiscount = priceOvernightDiscount - superSale;
                    if (rooms > 0) {
                        //if (rooms <= 5)
                        s = context.getString(R.string.txt_2_super_flashsale_room_left, String.valueOf(rooms));
                    } else {
                        s = context.getString(R.string.txt_2_super_flashsale_sold_out);
                    }
                } else { // normal
                    if (rooms > 0) {
                        if (rooms <= 5) {
                            s = String.format(context.getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                        }
                    } else {
                        s = context.getString(R.string.txt_2_flashsale_sold_out);
                    }
                }
                //Set Price Status
                viewHolder.tvPriceStatus.setVisibility(View.VISIBLE);
                viewHolder.tvPriceStatus.setText(s);

                viewHolder.boxHourly.setVisibility(View.GONE);

                if (!Utils.checkRoomTypeDiscount(data.getRoomApplyPromotion(), roomTypeForm.getSn(), ParamConstants.ROOM_TYPE_FLASH_SALE)) {
                /*
                 * NO DISCOUNT
                 */

                    //Set Price Overnight Normal
                    viewHolder.tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                    viewHolder.tvPriceOvernightNormal.setText(Utils.formatCurrency(data.getLowestPriceOvernight()));
                    //StrikeThrough
                    viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    //Set Price Overnight Discount
                    viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                } else {

                /*
                 *  DISCOUNT
                 */


                    //Set Price Overnight Normal
                    viewHolder.tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                    viewHolder.tvPriceOvernightNormal.setText(Utils.formatCurrency(data.getLowestPriceOvernight()));
                    //StrikeThrough
                    viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    //Set Price Overnight Discount
                    viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));

                }

                //Feature92
                if (superSale > 0){

                    viewHolder.tvSupperSaleNormal.setVisibility(View.VISIBLE);
                    viewHolder.tvSupperSaleNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                    viewHolder.tvSupperSaleNormal.setPaintFlags(viewHolder.tvSupperSaleNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    viewHolder.tvSupperSaleDiscount.setVisibility(View.VISIBLE);
                    viewHolder.tvSupperSaleDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));

                    viewHolder.tvPriceOvernightDiscount.setVisibility(View.GONE);

                }

            } else {

                viewHolder.tvSupperSaleDiscount.setVisibility(View.GONE);
                viewHolder.tvSupperSaleNormal.setVisibility(View.GONE);
                viewHolder.tvPriceOvernightDiscount.setVisibility(View.VISIBLE);
                viewHolder.imgFlashSale.setVisibility(View.GONE);
                viewHolder.boxHourly.setVisibility(View.VISIBLE);

                //--------------Set Price------------

                if (roomTypeForm.isCinema()) {
                    viewHolder.boxOvernight.setVisibility(View.GONE);
                } else {
                    viewHolder.boxOvernight.setVisibility(View.VISIBLE);
                }

                int[] discount = Utils.getPromotionInfoForm(
                        roomTypeForm.getHotelSn(),
                        roomTypeForm.getPriceFirstHours(),
                        roomTypeForm.getPriceOvernight(),
                        roomTypeForm.getPriceOneDay(),
                        roomTypeForm.getBonusFirstHours());

                //-------------Set Label Hourly---------------
                String s = context.getString(R.string.txt_2_flashsale_hourly_price, String.valueOf(roomTypeForm.getFirstHours()));
                viewHolder.txtLabelPriceHourly.setText(s);

                if (discount[0] > 0 || discount[1] > 0) {

                    //Set Price Status
                    viewHolder.tvPriceStatus.setText(context.getString(R.string.txt_2_coupon_applied));
                    boolean isPromotion = Utils.checkRoomTypeDiscount(data.getRoomApplyPromotion(), roomTypeForm.getSn(), ParamConstants.ROOM_TYPE_NORMAL);
                    if (isPromotion){
                        viewHolder.tvPriceStatus.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.tvPriceStatus.setVisibility(View.GONE);
                    }

                    //Hourly
                    if (discount[0] > 0 && isPromotion) {
                        int priceHourlyDiscount = roomTypeForm.getPriceFirstHours() - discount[0];
                        if (roomTypeForm.isCinema()) {
                            priceHourlyDiscount = roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours() - discount[3];
                        }
                        if (priceHourlyDiscount < 0) {
                            priceHourlyDiscount = 0;
                        }

                        //Set Price Hourly Normal
                        viewHolder.tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                        if (roomTypeForm.isCinema()) {
                            viewHolder.tvPriceHourlyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours()));
                            if (discount[3] <= 0)
                                viewHolder.tvPriceHourlyNormal.setVisibility(View.GONE);
                        } else {
                            viewHolder.tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                            viewHolder.tvPriceHourlyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));
                        }
                        //StrikeThrough
                        viewHolder.tvPriceHourlyNormal.setPaintFlags(viewHolder.tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        //Set Price Hourly Discount
                        viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                    } else {

                        //Set Price Hourly Normal
                        viewHolder.tvPriceHourlyNormal.setVisibility(View.GONE);
                        if (roomTypeForm.isCinema()) {
                            //Set Price Hourly Discount
                            viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours()));
                        } else {
                            //Set Price Hourly Discount
                            viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));
                        }

                    }

                    //Overnight
                    if (discount[1] > 0 && isPromotion) {
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
                    if (roomTypeForm.isCinema()) {
                        viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours()));
                    } else {
                        viewHolder.tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));
                    }

                    //Set Price Overnight Normal
                    viewHolder.tvPriceOvernightNormal.setVisibility(View.GONE);

                    //Set Price Overnight Discount
                    viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

                }

            }

            //final String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + data.getRoomTypeList().get(position).getHomeImageSn() + "&fileName=" + data.getRoomTypeList().get(position).getHomeImageSn();
            String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" +data.getRoomTypeList().get(position).getImageKey();

            PictureGlide.getInstance().show(
                    url,
                    context.getResources().getDimensionPixelSize(R.dimen.roomtype_height),
                    context.getResources().getDimensionPixelSize(R.dimen.roomtype_height),
                    R.drawable.loading_big,
                    viewHolder.imgRoom
            );


            if (this.selectedIndex == position) {
                viewHolder.imgChecked.setImageResource(R.drawable.checkbox_selected);
            } else {
                viewHolder.imgChecked.setImageResource(R.drawable.checkbox);
            }


            /*
            * Check Room type Locked
            */
            if (roomTypeForm.getStatus() == ParamConstants.LOCK_TODAY) {
                viewHolder.txtLocked.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txtLocked.setVisibility(View.GONE);
            }
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
        private LinearLayout boxOvernight;
        private ImageView imgFlashSale;
        private TextView txtLabelPriceHourly;
        private TextView tvSupperSaleNormal;
        private TextView tvSupperSaleDiscount;

    }

}
