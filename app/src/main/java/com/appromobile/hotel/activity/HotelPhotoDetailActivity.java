package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.FullImageDetailAdapter;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.view.FacilityForm;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.RoomView;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PictureUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;

import java.util.List;

/**
 * Created by xuan on 7/8/2016.
 */
public class HotelPhotoDetailActivity extends BaseActivity {
    public static Activity hotelPhotoDetailActivity;
    public static final int CALL_BOOKING = 1002;
    private HotelDetailForm hotelDetailForm;
    private FullImageDetailAdapter fullImageDetailAdapter;
    int roomTypeIndex = -1;

    private ViewPager vpImage;
    private TextView tvHotelTitle;
    private ImageView imgNoImage;

    private LinearLayout container;
    private LinearLayout boxDescription;
    private TextView tvRoomName, tvRoomSquare, tvRoomBed, tvRoomView, tvRoomDescription, tvRoomPromotion;
    private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvernightNormal, tvPriceOvernightDiscount, tvPriceDailyNormal, tvPriceDailyDiscount;
    private LinearLayout boxHourly, boxDaily;
    private RelativeLayout containerIndicator;
    private View indicator;
    private int widthBar = 0;
    private int pageScrollState = 0;
    private int current = 0;

    @Override
    protected void onStart() {
        super.onStart();
        PictureUtils.getInstance().clearCache(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hotelPhotoDetailActivity = this;
        super.onCreate(savedInstanceState);
        setScreenName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bk));
        }
        setContentView(R.layout.hotel_photo_detail_activity);

        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            hotelDetailForm = bundle.getParcelable("HotelDetailForm");

            if (hotelDetailForm != null) {

                setupFacility();

                //Set Hotel Name
                tvHotelTitle.setText(hotelDetailForm.getName());

                //Init Adapter ViewPager
                if (hotelDetailForm.getHotelImageList() != null && hotelDetailForm.getHotelImageList().size() > 0) {
                    fullImageDetailAdapter = new FullImageDetailAdapter(HotelPhotoDetailActivity.this, hotelDetailForm.getHotelImageList(), hotelDetailForm.getRoomTypeList());
                    vpImage.setAdapter(fullImageDetailAdapter);
                    createStatusbar();
                } else {
                    //No Image
                    vpImage.setVisibility(View.INVISIBLE);
                    imgNoImage.setVisibility(View.VISIBLE);

                    String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + 0 + "&fileName=" + "default_image";
                    PictureUtils.getInstance().load(
                            url,
                            getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                            getResources().getDimensionPixelSize(R.dimen.hotel_detail_image_height),
                            R.drawable.loading_big,
                            imgNoImage
                    );
                }

                //ViewPager Change
                vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        updateRoomTypeInfo(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                //Button Close Click
                findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                    }
                });

                //Button Book Click
                findViewById(R.id.btnBook).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!PreferenceUtils.getToken(HotelPhotoDetailActivity.this).equals("")) {

                            if (roomTypeIndex > -1) {
                                if (hotelDetailForm.getRoomTypeList().get(roomTypeIndex).isLocked()) {
                                    Toast.makeText(HotelPhotoDetailActivity.this, getString(R.string.msg_3_1_soldout_room), Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent = new Intent(HotelPhotoDetailActivity.this, ReservationActivity.class);
                                    intent.putExtra("HotelDetailForm", hotelDetailForm);
                                    intent.putExtra("RoomTypeIndex", roomTypeIndex);
                                    startActivityForResult(intent, CALL_BOOKING);
                                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                                }
                            } else {
                                Intent intent = new Intent(HotelPhotoDetailActivity.this, ReservationActivity.class);
                                intent.putExtra("HotelDetailForm", hotelDetailForm);
                                intent.putExtra("RoomTypeIndex", 0);
                                startActivityForResult(intent, CALL_BOOKING);
                                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                            }

                        } else {
                            Intent intent = new Intent(HotelPhotoDetailActivity.this, LoginActivity.class);
                            startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
                        }
                    }
                });

                //Check For Button Book
                if (hotelDetailForm.getRoomTypeList() == null || (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() == 0)) {
                    findViewById(R.id.btnBook).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.btnBook).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void updateRoomTypeInfo(int position) {
        boxDescription.setVisibility(View.GONE);
        if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
            HotelImageForm hotelImageForm = hotelDetailForm.getHotelImageList().get(position);
            int count = 0;
            for (int i = 0; i < hotelDetailForm.getRoomTypeList().size(); i++) {
                RoomTypeForm roomTypeForm = hotelDetailForm.getRoomTypeList().get(i);
                if (roomTypeForm != null) {
                    if (hotelImageForm.getRoomTypeSn() == roomTypeForm.getSn()) {

                        boxDescription.setVisibility(View.VISIBLE);

                        // Set Room Name
                        tvRoomName.setText(roomTypeForm.getName());

                        //Set Square
                        StringBuilder stringBuilder = new StringBuilder().append(getString(R.string.txt_3_2_square)).append(" ");
                        if (roomTypeForm.getSquare() <= 0) {
                            stringBuilder.append("N/A");
                        } else {
                            stringBuilder.append(String.valueOf(roomTypeForm.getSquare())).append("m2");
                        }
                        tvRoomSquare.setText(stringBuilder);

                        //Set Beb
                        tvRoomBed.setText(new StringBuilder().append(getString(R.string.txt_3_2_bed)).append(" ").append(roomTypeForm.getRoomBed(roomTypeForm.getBedType())));

                        //Set View
                        List<RoomView> roomViewList = roomTypeForm.getRoomViewList();
                        stringBuilder = new StringBuilder().append(getString(R.string.txt_3_2_views)).append(" ");
                        if (roomViewList != null && roomViewList.size() > 0) {
                            for (int j = 0; j < roomViewList.size(); j++) {
                                if (HotelApplication.isEnglish) {
                                    stringBuilder.append(roomViewList.get(j).getNameEn()).append(", ");
                                } else {
                                    stringBuilder.append(roomViewList.get(j).getName()).append(", ");
                                }
                            }
                        } else {
                            stringBuilder.append("N/A, ");
                        }
                        tvRoomView.setText(stringBuilder.substring(0, stringBuilder.length() - 2));

                        //Set Description
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            tvRoomDescription.setText(new StringBuilder().append(getString(R.string.txt_3_2_description)).append(" ").append(Html.fromHtml(roomTypeForm.getMemo(), Html.FROM_HTML_MODE_COMPACT)));
                        }else {
                            tvRoomDescription.setText(new StringBuilder().append(getString(R.string.txt_3_2_description)).append(" ").append(Html.fromHtml(roomTypeForm.getMemo())));
                        }

                        //Set Other Promotion
                        //tvRoomPromotion.setText("");

                        //Check Flash Sale
                        if (roomTypeForm.isFlashSale()) {

                            int rooms = roomTypeForm.getAvailableRoom();
                            String s;
                            if (rooms > 0) {
                                s = String.format(getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                            } else {
                                s = getString(R.string.txt_2_flashsale_sold_out);
                            }
                            //Set Price Status
                            tvPriceStatus.setVisibility(View.VISIBLE);
                            tvPriceStatus.setText(s);

                            boxHourly.setVisibility(View.GONE);
                            boxDaily.setVisibility(View.GONE);

                            //Set Price Overnight Normal
                            tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                            tvPriceOvernightNormal.setText(Utils.formatCurrency(hotelDetailForm.getLowestPriceOvernight()));
                            //StrikeThrough
                            tvPriceOvernightNormal.setPaintFlags(tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            //Set Price Overnight Discount
                            tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

                        } else {

                            boxHourly.setVisibility(View.VISIBLE);
                            boxDaily.setVisibility(View.VISIBLE);

                            //--------------Set Price------------
                            int[] discount = Utils.getPromotionInfoForm(roomTypeForm.getHotelSn());

                            if (discount[0] > 0 || discount[1] > 0 || discount[2] > 0) {

                                //Set Price Status
                                tvPriceStatus.setText(getString(R.string.txt_2_coupon_applied));

                                //Hourly
                                if (discount[0] > 0) {
                                    int priceHourlyDiscount = roomTypeForm.getPriceFirstHours() - discount[0];

                                    if (priceHourlyDiscount < 0) {
                                        priceHourlyDiscount = 0;
                                    }

                                    //Set Price Hourly Normal
                                    tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                                    tvPriceHourlyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));
                                    //StrikeThrough
                                    tvPriceHourlyNormal.setPaintFlags(tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    //Set Price Hourly Discount
                                    tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                                } else {

                                    //Set Price Hourly Normal
                                    tvPriceHourlyNormal.setVisibility(View.GONE);
                                    //Set Price Hourly Discount
                                    tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));

                                }

                                //Overnight
                                if (discount[1] > 0) {
                                    int priceOvernightDiscount = roomTypeForm.getPriceOvernight() - discount[1];

                                    if (priceOvernightDiscount < 0) {
                                        priceOvernightDiscount = 0;
                                    }

                                    //Set Price Overnight Normal
                                    tvPriceOvernightNormal.setVisibility(View.VISIBLE);
                                    tvPriceOvernightNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                                    //StrikeThrough
                                    tvPriceOvernightNormal.setPaintFlags(tvPriceOvernightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    //Set Price Overnight Discount
                                    tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                                } else {
                                    //Set Price Overnight Normal
                                    tvPriceOvernightNormal.setVisibility(View.GONE);

                                    //Set Price Overnight Discount
                                    tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));
                                }

                                //Daily
                                if (discount[2] > 0) {
                                    int priceDailyDiscount = roomTypeForm.getPriceOneDay() - discount[2];

                                    if (priceDailyDiscount < 0) {
                                        priceDailyDiscount = 0;
                                    }

                                    //Set Price Overnight Normal
                                    tvPriceDailyNormal.setVisibility(View.VISIBLE);
                                    tvPriceDailyNormal.setText(Utils.formatCurrency(roomTypeForm.getPriceOneDay()));
                                    //StrikeThrough
                                    tvPriceDailyNormal.setPaintFlags(tvPriceDailyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    //Set Price Overnight Discount
                                    tvPriceDailyDiscount.setText(Utils.formatCurrency(priceDailyDiscount));
                                } else {
                                    //Set Price Overnight Normal
                                    tvPriceDailyNormal.setVisibility(View.GONE);

                                    //Set Price Overnight Discount
                                    tvPriceDailyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOneDay()));
                                }

                            } else {

                                //Set Price Status
                                tvPriceStatus.setVisibility(View.GONE);

                                //Set Price Hourly Normal
                                tvPriceHourlyNormal.setVisibility(View.GONE);
                                //Set Price Hourly Discount
                                tvPriceHourlyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceFirstHours()));

                                //Set Price Overnight Normal
                                tvPriceOvernightNormal.setVisibility(View.GONE);

                                //Set Price Overnight Discount
                                tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

                                //Set Price Daily Normal
                                tvPriceDailyNormal.setVisibility(View.GONE);

                                //Set Price Daily Discount
                                tvPriceDailyDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOneDay()));

                            }

                        }

                        roomTypeIndex = i;
                        break;
                    }
                }
                count++;
            }

            if (count >= hotelDetailForm.getRoomTypeList().size()) {
                roomTypeIndex = 0;
            }
        }
    }


    private void setupFacility() {
        if (hotelDetailForm.getFacilityFormList() != null) {
            MyLog.writeLog("AddFacilitySize: " + hotelDetailForm.getFacilityFormList().size());

            if (hotelDetailForm.getFacilityFormList().size() == 0) {

                container.setVisibility(View.GONE);

            } else {

                container.setVisibility(View.VISIBLE);
                // set chiều ngang cho item in facilities list = 1/4 màn hình
                int width = (int) Utils.getScreenWidthPixel();
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width / 4, ViewGroup.LayoutParams.WRAP_CONTENT, 0);

                for (int i = 0; i < hotelDetailForm.getFacilityFormList().size(); i++) {
                    View imageLayout = getLayoutInflater().inflate(R.layout.facility_item, null);
                    ImageView imgVIew = imageLayout.findViewById(R.id.imgItem);
                    TextView txtView = imageLayout.findViewById(R.id.tvName);

                    FacilityForm facilityForm = hotelDetailForm.getFacilityFormList().get(i);


                    String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadFacilityImage?facilitySn=" + facilityForm.getSn() + "&fileName=" + facilityForm.getCustomizePath();

                    GlideApp
                            .with(imgVIew.getContext())
                            .load(url)
                            .override(getResources().getDimensionPixelSize(R.dimen.facility_width), getResources().getDimensionPixelSize(R.dimen.facility_height))
                            .into(imgVIew);
                    txtView.setText(facilityForm.getName());
                    txtView.setTextColor(getResources().getColor(R.color.wh));

                    container.addView(imageLayout, itemParams);
                }

            }

        } else {
            MyLog.writeLog("HotelDetailActivity-------->facilities null");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_BOOKING) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SHotelPhoto";
    }

    private void initView() {
        vpImage = findViewById(R.id.vpImage);
        tvHotelTitle = findViewById(R.id.tvHotelTitle);
        imgNoImage = findViewById(R.id.imgNoImage);

        container = findViewById(R.id.container);

        boxDescription = findViewById(R.id.boxDescription);
        boxDescription.setVisibility(View.GONE);

        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomSquare = findViewById(R.id.tvRoomSquare);
        tvRoomBed = findViewById(R.id.tvRoomBed);
        tvRoomView = findViewById(R.id.tvRoomView);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvRoomPromotion = findViewById(R.id.tvRoomPromotion);
        tvRoomPromotion.setVisibility(View.GONE);

        tvPriceStatus = findViewById(R.id.tvPriceStatus);
        tvPriceHourlyNormal = findViewById(R.id.tvPriceHourlyNormal);
        tvPriceHourlyDiscount = findViewById(R.id.tvPriceHourlyDiscount);
        tvPriceOvernightNormal = findViewById(R.id.tvPriceOvernightNormal);
        tvPriceOvernightDiscount = findViewById(R.id.tvPriceOvernightDiscount);
        tvPriceDailyNormal = findViewById(R.id.tvPriceDailyNormal);
        tvPriceDailyDiscount = findViewById(R.id.tvPriceDailyDiscount);

        boxHourly = findViewById(R.id.boxHourly);
        boxDaily = findViewById(R.id.boxDaily);

        containerIndicator = findViewById(R.id.container_indicator);
        indicator = findViewById(R.id.indicator);

    }

    //    Stautus bar
    private void createStatusbar() {

        if (vpImage.getAdapter() != null && vpImage.getAdapter().getCount() > 1) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.dp_12);
            widthBar = screenWidth / vpImage.getAdapter().getCount();
            indicator.getLayoutParams().width = widthBar + 10;
            indicator.requestLayout();

            //listener Scroll
            vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(final int position) {


                    if (vpImage.getAdapter().getCount() > 1) {
                        if (pageScrollState == ViewPager.SCROLL_STATE_SETTLING) {
                            TranslateAnimation animation = null;
                            if (current < position) {
                                animation = new TranslateAnimation(0, 1, 0, 0);
                            } else if (current > position) {
                                animation = new TranslateAnimation(0, -1, 0, 0);
                            }
                            if (animation != null) {
                                animation.setDuration(200);
                                animation.setFillAfter(false);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        RelativeLayout.LayoutParams layout_description = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
                                        layout_description.leftMargin = widthBar * position;
                                        indicator.setLayoutParams(layout_description);
                                        current = position;
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                indicator.startAnimation(animation);

                            }
                        }

                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    pageScrollState = state;
                }
            });
        } else {
            //list only 1
            containerIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
