package com.appromobile.hotel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.HotelDetailActivity;
import com.appromobile.hotel.activity.HotelPhotoRoomTypeDetailActivity;
import com.appromobile.hotel.activity.LoginActivity;
import com.appromobile.hotel.activity.Panorama;
import com.appromobile.hotel.activity.ReservationActivity;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PictureUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by xuan on 7/4/2016.
 */
public class RoomTypeListAdapter extends BaseAdapter {
    private HotelDetailForm data;
    private Context context;
    private ViewHolder viewHolder;

    public RoomTypeListAdapter(final Context context, HotelDetailForm data) {
        this.data = data;
        this.context = context;
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
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.romtype_item, parent, false);
            bindViewNormal(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final RoomTypeForm roomTypeForm = data.getRoomTypeList().get(position);
        if (roomTypeForm != null) {

            //Set Room Name
            viewHolder.tvRoomName.setText(roomTypeForm.getName());

            //Set Other Promotion
            viewHolder.tvOtherPromotion.setText("");


            if (roomTypeForm.isFlashSale()) {

                int rooms = roomTypeForm.getAvailableRoom();
                String s;
                if (rooms > 0) {
                    s = String.format(context.getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                } else {
                    s = context.getString(R.string.txt_2_flashsale_sold_out);
                }
                //Set Price Status
                viewHolder.tvPriceStatus.setText(s);

                //Set Label Flash Sale
                viewHolder.imgIconPromotion1.setVisibility(View.VISIBLE);
                viewHolder.imgIconPromotion1.setImageResource(R.drawable.icon_sale);

                viewHolder.boxHourly.setVisibility(View.GONE);

                //Set Price Overnight Normal
                viewHolder.tvPriceOvernightNormal.setText(Utils.formatCurrency(data.getLowestPriceOvernight()));
                //StrikeThrough
                viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                //Set Price Overnight Discount
                viewHolder.tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));


            } else {
                //--------------Set Price------------
                int[] discount = Utils.getPromotionInfoForm(roomTypeForm.getHotelSn());

                if (discount[0] > 0 || discount[1] > 0) {

                    //Set Price Status
                    viewHolder.tvPriceStatus.setVisibility(View.VISIBLE);
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
                        viewHolder.tvPriceOvernightNormal.setPaintFlags(viewHolder.tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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

            //Set Image Room
            final String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + data.getRoomTypeList().get(position).getHomeImageSn() + "&fileName=" + data.getRoomTypeList().get(position).getHomeImageSn();
            if (context != null) {
                PictureUtils.getInstance().load(
                        url,
                        context.getResources().getDimensionPixelSize(R.dimen.roomtype_height),
                        context.getResources().getDimensionPixelSize(R.dimen.roomtype_height),
                        R.drawable.loading_small,
                        viewHolder.imgRoom
                );
            }

            /*
            * Check Room type Locked
            */
            if (roomTypeForm.isLocked()) {
                viewHolder.txtLocked.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txtLocked.setVisibility(View.GONE);
            }
        }

        //Event Fabric
        if (HotelApplication.isRelease) {
            if (roomTypeForm != null && roomTypeForm.isFlashSale()) {
                Answers.getInstance().logCustom(new CustomEvent("FlashSale Hotel").putCustomAttribute("hotelName", data.getName()));
            }
        }

        /*
        * Check Image 360
        */
        if (roomTypeForm != null && roomTypeForm.getCountExifImage() > 0) {
            viewHolder.img360.setVisibility(View.VISIBLE);
        } else {
            viewHolder.img360.setVisibility(View.GONE);
        }

        viewHolder.img360.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Panorama.class);
                intent.setAction("HotelDetailActivity");
                intent.putExtra("HotelDetailForm", data);
                intent.putExtra("SelectedRoomType", position);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);

            }
        });

        /*
        * goto ReservationActivity
        */
        viewHolder.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.getToken(context).equals("")) {
                    if (roomTypeForm != null) {
                        if (roomTypeForm.isFlashSaleRoomAvailable() && roomTypeForm.getAvailableRoom() <= 0) {
                            Toast.makeText(context, context.getString(R.string.msg_3_9_flashsale_soldout), Toast.LENGTH_LONG).show();
                        } else if (roomTypeForm.isLocked()) {
                            Toast.makeText(context, context.getString(R.string.msg_3_1_soldout_room), Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, ReservationActivity.class);
                            intent.putExtra("HotelDetailForm", data);
                            intent.putExtra("RoomTypeIndex", position);
                            ((Activity) context).startActivityForResult(intent, HotelDetailActivity.CALL_BOOKING);
                            ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                        }
                    }
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    ((Activity) context).startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
                }
            }
        });


        viewHolder.itemRoomType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HotelPhotoRoomTypeDetailActivity.class);
                intent.putExtra("roomTypeIndex", position);
                intent.putExtra("HotelDetailForm", data);
                intent.putExtra("RoomTypeForm", data.getRoomTypeList().get(position));
                intent.putExtra("HotelName", data.getName());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView imgRoom;
        private Button btnBook;
        private TextView tvRoomName;
        private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount;
        private TextView tvOtherPromotion;
        private ImageView img360;
        private TextView txtLocked;
        private ImageView imgIconPromotion1;//, imgIconPromotion2,imgIconPromotion3,imgIconPromotion4,;
        private LinearLayout boxHourly;
        private LinearLayout itemRoomType;
    }

    private void bindViewNormal(View convertView) {
        viewHolder = new ViewHolder();

        viewHolder.tvRoomName = convertView.findViewById(R.id.tvRoomName);

        viewHolder.imgRoom = convertView.findViewById(R.id.imgRoom);
        viewHolder.btnBook = convertView.findViewById(R.id.btnBook);
        viewHolder.img360 = convertView.findViewById(R.id.item_room_type_hotel_icon_360);
        viewHolder.txtLocked = convertView.findViewById(R.id.roomtype_locked);
        viewHolder.tvOtherPromotion = convertView.findViewById(R.id.tvOtherPromotion);

        viewHolder.tvPriceStatus = convertView.findViewById(R.id.tvPriceStatus);
        viewHolder.tvPriceHourlyNormal = convertView.findViewById(R.id.tvPriceHourlyNormal);
        viewHolder.tvPriceHourlyDiscount = convertView.findViewById(R.id.tvPriceHourlyDiscount);
        viewHolder.tvPriceOvernightNormal = convertView.findViewById(R.id.tvPriceOvernightNormal);
        viewHolder.tvPriceOvernightDiscount = convertView.findViewById(R.id.tvPriceOvernightDiscount);

        viewHolder.imgIconPromotion1 = convertView.findViewById(R.id.imgIconPromotion1);

        viewHolder.boxHourly = convertView.findViewById(R.id.boxHourly);

        viewHolder.itemRoomType = convertView.findViewById(R.id.item_room_type);

        convertView.setTag(viewHolder);
    }
}
