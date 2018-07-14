package com.appromobile.hotel.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.AreaSettingActivity;
import com.appromobile.hotel.activity.ChooseAreaActivity;
import com.appromobile.hotel.activity.HotelDetailActivity;
import com.appromobile.hotel.activity.IntentTemp;
import com.appromobile.hotel.activity.LoginActivity;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.adapter.AdapterFlashSale;
import com.appromobile.hotel.adapter.AdvertAdapter;
import com.appromobile.hotel.adapter.HomeFavoriteAdapter;
import com.appromobile.hotel.adapter.HotelListAdapter;
import com.appromobile.hotel.adapter.PointViewAdapter;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApiList;
import com.appromobile.hotel.api.controllerApi.CallbackPromotionInfoForm;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.SortType;
import com.appromobile.hotel.model.view.BannerForm;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.TimerUtils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by xuan on 6/24/2016.
 */
public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, TimerUtils.OnTimeListener {
    private ListView lvHotel;
    private HotelListAdapter adapter;
    private TextViewSFRegular tvLocation;
    private TextView tvNoResult;
    private TextViewSFRegular tvMyFavorite;
    private LinearLayout dropdownAreaSetting;
    private TextView tvChooseArea;
    private TextViewSFRegular tvAreaSelected;
    private TextView tvNearby;
    private ListView lvFavorite;
    private int offset = 0;
    boolean isEndList = false;
    private ImageView imgDropDownArea;
    private int scrollStateCurrent;
    private List<HotelForm> hotelForms = new ArrayList<>();
    private ViewPager vpAdvert;
    private RecyclerView lvPoint;
    private RelativeLayout boxAdvert;
    private Animation zoomIn, zoomOut;
    private int sortType;
    GridLayoutManager layoutManager;
    PointViewAdapter pointViewAdapter;
    AdvertAdapter advertAdapter; //Banner
    TimerUtils timer;

    private int positionOfFirstGeneralHotel;

    private View headerNoContract;

    //Flash Sale
    private View headerFlashSale;
    private RelativeLayout containerIndicator;
    private View indicator;
    private ViewPager viewPagerFlasfSale;
    private List<HotelForm> listFlashSale;
    private AdapterFlashSale adapterFlashSale;

    private int pageScrollStateFlashSale = 0;
    private int currentFlashSale = 0;
    private int totalFlashSale = 0;

    private int currentBanner = 0;
    private int totalBanner = 0;

    private boolean isRefresh = true;

    public HomeFragment() {
        setScreenName("SHome");
    }


    private HomeFavoriteAdapter homeFavoriteAdaper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        lvHotel = rootView.findViewById(R.id.lvHotel);
        vpAdvert = rootView.findViewById(R.id.vpAdvert);
        zoomIn = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.zoom_in);
        zoomOut = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.zoom_out);
        boxAdvert = rootView.findViewById(R.id.boxAdvert);
        lvPoint = rootView.findViewById(R.id.lvPoint);
        imgDropDownArea = rootView.findViewById(R.id.imgDropDownArea);
        tvLocation = rootView.findViewById(R.id.tvLocation);
        lvHotel.setOnItemClickListener(this);
        tvNoResult = rootView.findViewById(R.id.tvNoResult);
        tvAreaSelected = rootView.findViewById(R.id.tvAreaSelected);
        dropdownAreaSetting = rootView.findViewById(R.id.dropdownAreaSetting);
        tvMyFavorite = rootView.findViewById(R.id.tvMyFavorite);
        tvChooseArea = rootView.findViewById(R.id.tvChooseArea);
        tvNearby = rootView.findViewById(R.id.tvNearby);
        lvFavorite = rootView.findViewById(R.id.lvFavorite);
        tvAreaSelected.setOnClickListener(this);
        tvMyFavorite.setOnClickListener(this);
        tvChooseArea.setOnClickListener(this);
        tvNearby.setOnClickListener(this);

        if (getActivity() != null) {
            //No Contract
            headerNoContract = getActivity().getLayoutInflater().inflate(R.layout.no_contract_hotel_item, null);

            //Flash Sale
            headerFlashSale = getActivity().getLayoutInflater().inflate(R.layout.flash_sale, null);

        }

        containerIndicator = headerFlashSale.findViewById(R.id.container_indicator_flash_sale);
        indicator = headerFlashSale.findViewById(R.id.indicator_flash_sale);
        viewPagerFlasfSale = headerFlashSale.findViewById(R.id.my_viewPager_flash_sale);
        listFlashSale = new ArrayList<>();

        /*
        / GET LIMIT DISTANCE
        */

        createLoading();

        return rootView;
    }


    private void createLoading() {

        positionOfFirstGeneralHotel = 1;
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);

        lvPoint.setLayoutManager(layoutManager);
        lvFavorite.setOnItemClickListener(favoriteAreaItemClick);
        initAreaFavorite(false);
        dropdownAreaSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropdownAreaSetting.setVisibility(View.GONE);
                imgDropDownArea.setImageResource(R.drawable.combobox_down);
            }
        });


        lvHotel.setFriction(ViewConfiguration.getScrollFriction() * 4);

        //Scroll Load More List Hotel ---> Terminal ---> Don't Delete
//        lvHotel.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                scrollStateCurrent = scrollState;
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (scrollStateCurrent == SCROLL_STATE_IDLE) {
//                    if (adapter != null) {
//                        if (visibleItemCount != 0 && totalItemCount != 0) {
//                            MyLog.writeLog(firstVisibleItem + ":" + visibleItemCount + ":" + (firstVisibleItem + visibleItemCount + 13) + "------:" + totalItemCount);
//                            if ((firstVisibleItem + visibleItemCount + 13 >= totalItemCount) && !isEndList) {
//                                initData();
//                            }
//                        }
//                    }
//                }
//            }
//        });

        //ChooseAreaDropDwon
        imgDropDownArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDropdownVisible();
            }
        });

        //Banner
        vpAdvert.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pointViewAdapter.setSelected(position);
                currentBanner = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //showAddress();
        initAdvertising();
    }

    /*
    * Show Banner
    */
    @Override
    public void initAdvertising() {
        super.initAdvertising();

        HotelApplication.serviceApi.findAllBannerList(PreferenceUtils.getToken(getActivity()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<BannerForm>>() {
            @Override
            public void onResponse(Call<List<BannerForm>> call, Response<List<BannerForm>> response) {
                if (response.isSuccessful()) {
                    final List<BannerForm> promotionForms = response.body();
                    if (promotionForms != null && promotionForms.size() > 0) {
                        boxAdvert.setVisibility(View.VISIBLE);
                        totalBanner = promotionForms.size();
                        // if the number of banner > 1. Display a point to switch banner
                        if (promotionForms.size() > 1) {
                            pointViewAdapter = new PointViewAdapter(promotionForms.size());
                            lvPoint.setAdapter(pointViewAdapter);
                        }
                        // the number of banner is 1. So we don't need a switch point
                        else {
                            lvPoint.setVisibility(View.GONE);
                        }
                        advertAdapter = new AdvertAdapter(getActivity(), promotionForms);

                        vpAdvert.setAdapter(advertAdapter);

                    } else {
                        boxAdvert.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BannerForm>> call, Throwable t) {
                boxAdvert.setVisibility(View.GONE);
            }
        });


    }

    private void showAddress() {
        //Show Current Address
        String address = PreferenceUtils.getCurrentAddress(getContext());
        MyLog.writeLog("My Address:---->" + address);
        tvLocation.setText(address);
    }

    private AdapterView.OnItemClickListener favoriteAreaItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getActivity() != null) {

                Intent intent = new Intent(getContext(), IntentTemp.class);
                intent.setAction(ParamConstants.ACTION_CHOOSE_AREA_FAVORITE);
                intent.putExtra("positionFavorite", position);
                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHOOSE_AREA_FAVORITE);

                setDropdownVisible();

                //setFavorite();
            }
        }
    };

    private void setFavorite(int position) {
        if (getActivity() != null) {

            UserAreaFavoriteForm userAreaFavoriteForm = HotelApplication.userAreaFavoriteForms.get(position);
            ((MainActivity) getActivity()).getHomeHotelRequest().setDistrictSn(Integer.toString(userAreaFavoriteForm.getDistrictSn()));

            offset = 0;
            if (hotelForms != null) {
                hotelForms.clear();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            isEndList = false;
            tvAreaSelected.setText(userAreaFavoriteForm.getDistrictName());
            DialogUtils.showLoadingProgress(getContext(), false);
            MyLog.writeLog("SetFavarite:----> Run");

            //initDataMyPageFragment();

            //enable load ListView
            isRefresh = true;
        }
    }

    private void initAreaFavorite(final boolean isShowDrowdown) {
        try {
            if (HotelApplication.userAreaFavoriteForms != null) {
                HotelApplication.userAreaFavoriteForms.clear();
            }
            HotelApplication.serviceApi.findAllFavoriteArea(PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserAreaFavoriteForm>>() {
                @Override
                public void onResponse(Call<List<UserAreaFavoriteForm>> call, Response<List<UserAreaFavoriteForm>> response) {
                    if (response.isSuccessful()) {
                        HotelApplication.userAreaFavoriteForms = response.body();
                        if (isShowDrowdown) {
                            dropdownAreaSetting.setVisibility(View.GONE);
                            setDropdownVisible();
                        }
                    } else if (response.code() == 401) {
                        //
                        MyLog.writeLog("");
                    }
                }

                @Override
                public void onFailure(Call<List<UserAreaFavoriteForm>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("initAreaFavorite----------------------------->" + e);
        }

    }

    private void startAnimation() {
        imgDropDownArea.startAnimation(zoomIn);
        tvChooseArea.setTextColor(getResources().getColor(R.color.org));
        tvChooseArea.setTypeface(tvChooseArea.getTypeface(), Typeface.BOLD);
    }

    private void stopAnimation() {
        imgDropDownArea.clearAnimation();
        tvChooseArea.setTextColor(getResources().getColor(R.color.bk));
        tvChooseArea.setTypeface(tvChooseArea.getTypeface(), Typeface.NORMAL);
    }

    private void initData() {
        if (getActivity() != null) {
            MyLog.writeLog("HomeFragment InitData:----> Run");
            isEndList = true;

            try {
                if (lvHotel.getHeaderViewsCount() > 0 || lvHotel.getCount() > 0) {
                    lvHotel.removeHeaderView(headerNoContract);
                    stopAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((MainActivity) getActivity()).getHomeHotelRequest().setOffset(offset);
            sortType = ((MainActivity) getActivity()).getHomeHotelRequest().getSort();


            final String ditrictSn = ((MainActivity) getActivity()).getHomeHotelRequest().getDistrictSn();


            //Get Promotion Info Form
            if (HotelApplication.mapPromotionInfoForm == null || HotelApplication.mapPromotionInfoForm.size() == 0) {
                ControllerApi.getmInstance().findPromotionInformation(getActivity(), new CallbackPromotionInfoForm() {
                    @Override
                    public void map(Map<String, PromotionInfoForm> map) {

                            HotelApplication.mapPromotionInfoForm = map;

                        if ((sortType == SortType.DISTANCE.getType() || sortType == SortType.DEEPLINK_DISTRICT.getType()) && ditrictSn != null) {
                            MyLog.writeLog("--||||-->Get All Hotel List In District<--||||--");
                            getAllHotelListInDistrict(ditrictSn);
                        } else {
                            MyLog.writeLog("--||||-->Get Hotel List<--||||--");
                            //getNearbyHotelList();
                            getAllContractTrialHotel();
                        }

                    }
                });
            }else {

                if ((sortType == SortType.DISTANCE.getType() || sortType == SortType.DEEPLINK_DISTRICT.getType()) && ditrictSn != null) {
                    MyLog.writeLog("--||||-->Get All Hotel List In District<--||||--");
                    getAllHotelListInDistrict(ditrictSn);
                } else {
                    MyLog.writeLog("--||||-->Get Hotel List<--||||--");
                    //getNearbyHotelList();
                    getAllContractTrialHotel();
                }

            }

            //Flash Sale
            findFlashSaleHotelList();

        }
    }

    /*
    / Get Flash Sale Hotel
    */
    @SuppressWarnings("unchecked")
    private void findFlashSaleHotelList() {
        ControllerApi.getmInstance().findFlashSaleHotelList(getActivity(), 200, 0, new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {

                listFlashSale = (List<HotelForm>) (Object) list;
                if (listFlashSale.size() > 0) {
                    totalFlashSale = listFlashSale.size();
                    adapterFlashSale = new AdapterFlashSale(listFlashSale, getFragmentManager());
                    viewPagerFlasfSale.setAdapter(adapterFlashSale);
                    viewPagerFlasfSale.setOffscreenPageLimit(3);
                    createStatusbarFlashSale();
                    //adapterFlashSale.notifyDataSetChanged();

                    //Add FlashSale in Header
                    if (lvHotel.getHeaderViewsCount() <= 0) {
                        lvHotel.addHeaderView(headerFlashSale, null, false);
                        AbsListView.LayoutParams headerViewParams = new AbsListView.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.hotel_list_height) + 70); //70 = height text Flash Sale
                        headerFlashSale.setLayoutParams(headerViewParams);
                    }
                } else {
                    //Non Flash Sale
                    if (lvHotel.getHeaderViewsCount() > 0) {
                        lvHotel.removeHeaderView(headerFlashSale);
                    }
                }
            }
        });
    }


    //Status bar for Flash Sale
    private void createStatusbarFlashSale() {
        if (viewPagerFlasfSale.getAdapter() != null && viewPagerFlasfSale.getAdapter().getCount() > 1) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.dp_12); // For margin
            final int widthBar = screenWidth / viewPagerFlasfSale.getAdapter().getCount();
            indicator.getLayoutParams().width = widthBar + 12;
            indicator.requestLayout();

            //listener Scroll
            viewPagerFlasfSale.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(final int position) {
                    if (totalFlashSale > 1) {
                        if (pageScrollStateFlashSale == ViewPager.SCROLL_STATE_SETTLING) {
                            TranslateAnimation animation = null;
                            if (currentFlashSale < position) {
                                animation = new TranslateAnimation(0, 1, 0, 0);
                            } else if (currentFlashSale > position) {
                                animation = new TranslateAnimation(0, -1, 0, 0);
                            }
                            if (animation != null) {
                                animation.setDuration(150);
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
                                        currentFlashSale = position;
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
                    pageScrollStateFlashSale = state;
                }
            });
        } else {
            //list only 1
            containerIndicator.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void setDistrictName(String districtName) {
        super.setDistrictName(districtName);

        if (districtName != null) {
            //Set Position Home
            tvAreaSelected.setText(districtName);
        }

    }

    /**
     * Get all Hotel list in district
     */

    private void getAllHotelListInDistrict(String districtSn) {
        HotelApplication.serviceApi.getAllHotelListInDistrict(districtSn, PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {

                    List<HotelForm> list = response.body();

                    if (list != null && list.size() > 0) {

                        try {
                            hotelForms.addAll(list);
                            isEndList = true;

                            /*
                            * Sort distance
                            */

                            Collections.sort(hotelForms, new Comparator<HotelForm>() {
                                @Override
                                public int compare(HotelForm hotelForm, HotelForm t1) {
                                    return Double.compare(hotelForm.getDistance(getContext()), t1.getDistance(getContext()));
                                }
                            });

                            /*
                            *   Check Contact or Trail
                            */

                            int index = 0;
                            for (int i = 0; i < hotelForms.size(); i++) {
                                MyLog.writeLog("HotelStatus------------->" + hotelForms.get(i).getHotelStatus() + " Distanse----------->" + hotelForms.get(i).getDistance(getContext()));
                                /*
                                * Sort Hotel contact and trail
                                */
                                // 3 = contracted
                                // 4 = trail
                                if (hotelForms.get(i).getHotelStatus() == ContractType.CONTRACT.getType() || hotelForms.get(i).getHotelStatus() == ContractType.TRIAL.getType()) {
                                    HotelForm temp = hotelForms.remove(i);
                                    hotelForms.add(index, temp);
                                    index++;
                                }
                            }

                            /*
                            * Check Go2Joy or List
                            */
                            checkListItem();

                            /*
                            / Filter Flash Sale
                            */
                            //sortHotelListByFlashsale();

                            /*
                            * Set Image No Contact
                            */
                            if (positionOfFirstGeneralHotel == 0) {

                                //Show Image No Contract
                                showImageNoContract();

                            }

                            /*
                            * Set Listview
                             */
                            if (getActivity() != null) {
                                if (adapter == null) {
                                    adapter = new HotelListAdapter(getActivity(), hotelForms, ((MainActivity) getActivity()).getHomeHotelRequest().getTypeSearch());
                                    lvHotel.setAdapter(adapter);
                                    MyLog.writeLog("------>SetApdapter ListView HomeFragment");
                                } else {

                                    adapter.updateData(hotelForms, ((MainActivity) getActivity()).getHomeHotelRequest().getSort());
                                    MyLog.writeLog("------>UpdateApdapter ListView HomeFragment");
                                }
                            }
                        } catch (Exception e) {
                            MyLog.writeLog("GetAllHotellist------------------------------------>" + e);
                        }
                    } else {
                        lvHotel.setEmptyView(tvNoResult);
                        MyLog.writeLog("GetAllHotelListInDistrict-------->No result");
                    }

                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(getActivity(), new DialogCallback() {
                        @Override
                        public void finished() {
                            if (getActivity() != null) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_RESET_NEARBY);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                lvHotel.setEmptyView(tvNoResult);
            }
        });
    }

    /*
    * Get Nearby Hotel FragmentHome
    */
    private void getNearbyHotelList() {
        if (getActivity() != null) {
            MyLog.writeLog("getHotellist----+++++++----->" + ((MainActivity) getActivity()).getHomeHotelRequest().getMap());
            HotelApplication.serviceApi.getHotelList(((MainActivity) getActivity()).getHomeHotelRequest().getMap(), PreferenceUtils.getToken(getContext()), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
                @Override
                public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                    DialogUtils.hideLoadingProgress();

                    List<HotelForm> list = response.body();

                    if (response.isSuccessful()) {
                        MyLog.writeLog("-----> GetHotelList");
                        try {
                            if (list == null || list.size() == 0) {
                                MyLog.writeLog("-----> GetHotelList ----->body null");
                                isEndList = true;
                                if (hotelForms.size() == 0) {
                                    lvHotel.setEmptyView(tvNoResult);
                                    return;
                                }
                            }

                            if (list != null && list.size() > 0) {
                                isEndList = list.size() < HotelApplication.LIMIT_REQUEST;
                                MyLog.writeLog("-----> GetHotelList ----->body.size" + list.size());
                                hotelForms.addAll(list);


                            } else {
                                // server stop returning data
                                isEndList = true;
                                // if list don't have data, display no hotel
                                if (hotelForms.size() == 0) {
                                    lvHotel.setEmptyView(tvNoResult);
                                    return;
                                }
                            /*
                            * Set Image No Contact
                            */
                                // list have data
                                // trường hợp dữ liệu ít
                                if (positionOfFirstGeneralHotel == 0 && lvHotel.getHeaderViewsCount() > 0) {

                                    //Show Image No Contract
                                    showImageNoContract();

                                }
                                return;
                            }
                        /*
                         * Check Go2Joy or List
                         */
                            checkListItem();
                        /*
                         /*
                         * Set Image No Contact
                         */
                            if (positionOfFirstGeneralHotel == 0 && offset == 0) {

                                //Show Image No Contract
                                showImageNoContract();

                            }
                        /*
                         * Set ListView
                         */
                            if (getActivity() != null) {
                                if (adapter == null) {
                                    adapter = new HotelListAdapter(getActivity(), hotelForms, ((MainActivity) getActivity()).getHomeHotelRequest().getTypeSearch());
                                    lvHotel.setAdapter(adapter);
                                } else {

                                    adapter.updateData(hotelForms, ((MainActivity) getActivity()).getHomeHotelRequest().getSort());

                                }
                            }


                            if (list.size() > 0) {
                                offset = adapter.getCount();
                            }
                            MyLog.writeLog("-----> load offset ----->" + offset);

                        } catch (Exception e) {
                            MyLog.writeLog("-----> getNearbyHotelList Exception ----->" + e);
                        }
                    } else if (response.code() == 401) {
                        DialogUtils.showExpiredDialog(getActivity(), new DialogCallback() {
                            @Override
                            public void finished() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_RESET_NEARBY);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    t.printStackTrace();
                    lvHotel.setEmptyView(tvNoResult);
                }
            });
        }
    }
    /*
    * Get all Contracted and Trial hotels
    * */

    @SuppressWarnings("unchecked")
    private void getAllContractTrialHotel() {
        DialogUtils.hideLoadingProgress();
        ControllerApi.getmInstance().findAllHotelContractTrialList(getActivity(), new ResultApiList() {
            @Override
            public void resultApilist(List<Object> list) {
                if (getActivity() != null && list.size() > 0) {

                    hotelForms = (List<HotelForm>) (Object) list;
                    Collections.sort(hotelForms, new Comparator<HotelForm>() {
                        @Override
                        public int compare(HotelForm hotelForm, HotelForm t1) {
                            return Double.compare(hotelForm.getDistance(getContext()), t1.getDistance(getContext()));
                        }
                    });
                    hotelForms.get(0).setCategory(true); //**//
                    if (adapter == null) {
                        adapter = new HotelListAdapter(getActivity(), hotelForms, ((MainActivity) getActivity()).getHomeHotelRequest().getTypeSearch());
                        lvHotel.setAdapter(adapter);
                        MyLog.writeLog("------>SetApdapter ListView HomeFragment");
                    } else {
                        adapter.updateData(hotelForms, ((MainActivity) getActivity()).getHomeHotelRequest().getSort());
                        MyLog.writeLog("------>UpdateApdapter ListView HomeFragment");
                    }
                } else {
                    lvHotel.setEmptyView(tvNoResult);
                }
            }
        });
    }

    /*
     * Check Go2Joy List or List
     */
    private void checkListItem() {
        if (hotelForms != null && hotelForms.size() > 0) {

            /*
            * Check Hotel contact (setText GO2JOY HOTELS)
            */
            //Set position 0 is Go2Joy
            hotelForms.get(0).setCategory(true);

            //Check Hotel Genaral
            if (hotelForms.get(0).getHotelStatus() == ContractType.GENERAL.getType() || hotelForms.get(0).getHotelStatus() == ContractType.TERMINAL.getType() & sortType != SortType.ALPHABET.getType()) {
                positionOfFirstGeneralHotel = 0;
            } else {
                positionOfFirstGeneralHotel = 1;
            }

            /*
            * Check Hotel none contact (setText List)
            */
            for (int i = 0; i < hotelForms.size() - 1; i++) {
                if (hotelForms.get(i).getHotelStatus() != hotelForms.get(i + 1).getHotelStatus() && hotelForms.get(i + 1).getHotelStatus() == ContractType.GENERAL.getType() || hotelForms.get(i + 1).getHotelStatus() == ContractType.TERMINAL.getType()) {
                    //Set Position i is List
                    hotelForms.get(i + 1).setCategory(true);
                    return;
                }
            }
        }
    }

    /*
    /Show Image No Contract
    */
    private void showImageNoContract() {
        ImageView img = headerNoContract.findViewById(R.id.imgNoContractHotel);

        if (HotelApplication.isEnglish) {
            GlideApp.with(img.getContext()).load(R.drawable.no_hotel_english).placeholder(R.drawable.no_hotel_english).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(img);
        } else {
            GlideApp.with(img.getContext()).load(R.drawable.no_hotel_vn).placeholder(R.drawable.no_hotel_vn).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(img);
        }

        lvHotel.addHeaderView(headerNoContract);
        startAnimation();
    }

    /*
    * Goto HotelDetailActivity
    */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() != null && hotelForms.size() > 0) {
            int countHeader = lvHotel.getHeaderViewsCount();

            if (countHeader > 0) {
                position = position - countHeader;
            }
            if (position >= 0) {
                Intent intent = new Intent(getActivity(), HotelDetailActivity.class);
                intent.putExtra("sn", hotelForms.get(position).getSn());
                intent.putExtra("RoomAvailable", hotelForms.get(position).getRoomAvailable());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        }
    }

    @Override
    public void onRefreshFavorite(int position) {
        super.onRefreshFavorite(position);

        if (position != -1) {
            setFavorite(position);
        }

    }

    @Override
    public void onRefreshNearby() {
        super.onRefreshNearby();
        resetNearby();
    }

    @Override
    public void onRefreshData() {
        super.onRefreshData();
        DialogUtils.showLoadingProgress(getContext(), false);
        offset = 0;
        if (hotelForms != null) {
            hotelForms.clear();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        positionOfFirstGeneralHotel = 1;
        isEndList = false;
        MyLog.writeLog("onRefreshData:----> Run");
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAreaSelected:
                setDropdownVisible();
                break;
            case R.id.tvMyFavorite:
                myFavoriteClick();
                break;
            case R.id.tvChooseArea:
                dropdownAreaSetting.setVisibility(View.GONE);
                imgDropDownArea.setImageResource(R.drawable.combobox_down);
                chooseArea();
                break;
            case R.id.tvNearby:
                if (getActivity() != null) {
                    //resetNearby();
                    setDropdownVisible();  //Show list nearby
                    Intent intent = new Intent(getContext(), IntentTemp.class);
                    intent.setAction(ParamConstants.ACTION_RESET_NEARBY);
                    getActivity().startActivityForResult(intent, ParamConstants.REQUEST_RESET_NEARBY);
                }
                break;
        }
    }

    private void resetNearby() {
        if (getActivity() != null) {
            tvAreaSelected.setText(getString(R.string.near_by));

            //setDropdownVisible();  //Show list nearby
            offset = 0;
            ((MainActivity) getActivity()).getHomeHotelRequest().setDistrictSn(null);
            if (hotelForms != null) {
                hotelForms.clear();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            MyLog.writeLog("ResetNearby:-----> Run");
            isRefresh = true;
        }
    }

    private void chooseArea() {
        if (getActivity() != null) {
            Intent intent = new Intent(getContext(), ChooseAreaActivity.class);
            getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHOOSE_AREA_HOME);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    private void myFavoriteClick() {
        if (getActivity() != null) {
            if (!PreferenceUtils.getToken(getContext()).equals("")) {
                Intent intent = new Intent(getActivity(), AreaSettingActivity.class);
                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_CHOOSE_FAVORITE_AREA_HOME);
                getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_AREA_SETTING_HOME);
            }
            setDropdownVisible();
        }
    }

    private void setDropdownVisible() {
        if (dropdownAreaSetting.getVisibility() == View.VISIBLE) {
            dropdownAreaSetting.setVisibility(View.GONE);
            imgDropDownArea.setImageResource(R.drawable.combobox_down);

        } else {
            imgDropDownArea.setImageResource(R.drawable.combobox_up);
            dropdownAreaSetting.setVisibility(View.VISIBLE);
            if (PreferenceUtils.getToken(getContext()).equals("")) {
                tvMyFavorite.setVisibility(View.VISIBLE);
                tvMyFavorite.setText(getString(R.string.set_my_favorite_area));
                lvFavorite.setVisibility(View.GONE);
            } else {
                lvFavorite.setVisibility(View.VISIBLE);
                // check if have any my favorites
                if (HotelApplication.userAreaFavoriteForms == null || (HotelApplication.userAreaFavoriteForms != null && HotelApplication.userAreaFavoriteForms.size() == 0)) {
                    tvMyFavorite.setText(getString(R.string.set_my_favorite_area));
                    lvFavorite.setVisibility(View.GONE);
                    tvMyFavorite.setVisibility(View.VISIBLE);
                } else {
//                    tvMyFavorite.setVisibility(View.GONE);
                    homeFavoriteAdaper = new HomeFavoriteAdapter(getContext(), HotelApplication.userAreaFavoriteForms);
                    lvFavorite.setAdapter(homeFavoriteAdaper);
                    tvMyFavorite.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void requestAreaSetting(boolean isShowDropdown) {
        super.requestAreaSetting(isShowDropdown);

        initAreaFavorite(isShowDropdown);
    }

    public void chooseArea(int districtSn, String districName) {
        if (getActivity() != null) {

            ((MainActivity) getActivity()).getHomeHotelRequest().setDistrictSn(Integer.toString(districtSn));
            // setDropdownVisible();
            dropdownAreaSetting.setVisibility(View.GONE);
            offset = 0;
            positionOfFirstGeneralHotel = 1;
            if (hotelForms != null) {
                hotelForms.clear();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            isEndList = false;
            tvAreaSelected.setText(districName);
            DialogUtils.showLoadingProgress(getContext(), false);
            adapter = null;
            MyLog.writeLog("Choose Area:-----> Run");

            //Callback from district
            //initDataMyPageFragment();

            //enable load ListView
            isRefresh = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (imgDropDownArea != null && imgDropDownArea.getAnimation() != null) {
            imgDropDownArea.clearAnimation();
        }
        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }
        isRefresh = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.writeLog("-----\\\\----->HomeFragment<----////----- Resume");
        Animation animation = imgDropDownArea.getAnimation();
        if (positionOfFirstGeneralHotel == 0 && animation == null) {
            imgDropDownArea.startAnimation(zoomIn);
        }

        if (isRefresh) {
            if (hotelForms != null) {
                hotelForms.clear();
                offset = 0;
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    //Load View
                }
                MyLog.writeLog("CheckLocationChange:-----> Run");
                initData();
            }
        }

        showAddress();
        initAreaFavorite(false);

        if (HotelApplication.isFlashSaleChange && isAdded()) {
            HotelApplication.isFlashSaleChange = false;

            findFlashSaleHotelList();
        }

        // start auto scroll
        if (timer == null) {
            timer = new TimerUtils(2500, TimerUtils.TYPE_HOME_FRAGMENT, this);
        }
        timer.startTimer();
    }

    @Override
    public void onUpdateLocation() {
        super.onUpdateLocation();
        resetNearby();
        showAddress();
        initData();
    }

    @Override
    public void setOnTimeListener(int type) {
        scrollFlashSale();
        scrollBanner();
    }

    private void scrollFlashSale() {
        if (viewPagerFlasfSale != null && viewPagerFlasfSale.getAdapter() != null) {
            if (totalFlashSale > 1 && pageScrollStateFlashSale == ViewPager.SCROLL_STATE_IDLE) {
                if (currentFlashSale < totalFlashSale - 1) {
                    viewPagerFlasfSale.setCurrentItem(currentFlashSale + 1, true);
                } else {
                    viewPagerFlasfSale.setCurrentItem(0, true);
                }
            }
        }
    }

    private void scrollBanner() {
        if (vpAdvert != null && vpAdvert.getAdapter() != null) {
            if (totalBanner > 1) {
                if (currentBanner < totalBanner - 1) {
                    vpAdvert.setCurrentItem(currentBanner + 1, true);
                } else {
                    vpAdvert.setCurrentItem(0, true);
                }
            }
        }
    }
}


