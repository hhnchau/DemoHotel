package com.appromobile.hotel.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.DistrictSpinAdapter;
import com.appromobile.hotel.adapter.ProvinceSpinAdapter;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.model.request.ReportHotelDto;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.District;
import com.appromobile.hotel.model.view.Province;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/6/2016.
 */
public class ReportNewHotelActivity extends BaseActivity implements View.OnClickListener {
    private static final int CHOOSE_LOCATION = 1000;
    private static final int SELECT_PHOTO = 1001;
    private List<Province> provinces;
    private List<District> districts;
    private Spinner spinProvince, spinDistrict;
    private ProvinceSpinAdapter provinceAdapter;
    private DistrictSpinAdapter districtSpinAdapter;
    private double lat, lng;
    private String province, district;
    private String address = "";
    private EditTextSFRegular txtAddress, txtPhoneCode, txtPhoneNo, txtHotelName, txtEmail;
    private ImageView imgPhoto;
    private TextView tvUploadImage;
    private Uri selectedImage = null;
    private TextViewSFRegular tvCity, tvDistrict;
    private AppUserForm appUserForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("ReportNewHotelActivity" + e);
        }

        setContentView(R.layout.report_new_hotel_activity);
        spinDistrict = findViewById(R.id.spinDistrict);
        spinProvince = findViewById(R.id.spinProvince);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhoneCode = findViewById(R.id.txtPhoneCode);
        txtPhoneNo = findViewById(R.id.txtPhoneNo);
        txtHotelName = findViewById(R.id.txtHotelName);
        txtEmail = findViewById(R.id.txtEmail);
        imgPhoto = findViewById(R.id.imgPhoto);
        tvUploadImage = findViewById(R.id.tvUploadImage);
        tvCity = findViewById(R.id.tvCity);
        tvDistrict = findViewById(R.id.tvDistrict);
        findViewById(R.id.btnChooseLocation).setOnClickListener(this);
        findViewById(R.id.btnChange).setOnClickListener(this);
        findViewById(R.id.btnReport).setOnClickListener(this);
        findViewById(R.id.btnChoosePhoto).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        txtHotelName.requestFocus();
        Utils.showKeyboard(this);
        initData();

        spinProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> params = new HashMap<>();
                params.put("provinceSn", provinceAdapter.getItem(position));
                tvCity.setText(provinces.get(position).getName());
                txtPhoneCode.setText(provinces.get(position).getAreaCode());
                DialogUtils.showLoadingProgress(ReportNewHotelActivity.this, false);
                HotelApplication.serviceApi.findAllDistrictInProvince(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<District>>() {
                    @Override
                    public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                        DialogUtils.hideLoadingProgress();
                        if (response.isSuccessful()) {
                            districts = response.body();
                            districtSpinAdapter = new DistrictSpinAdapter(ReportNewHotelActivity.this, response.body());
                            spinDistrict.setAdapter(districtSpinAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<District>> call, Throwable t) {
                        DialogUtils.hideLoadingProgress();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvDistrict.setText(districts.get(position).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinProvince.performClick();
            }
        });

        tvDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinDistrict.performClick();
            }
        });
    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("mobileUserId", HotelApplication.DEVICE_ID);
        HotelApplication.serviceApi.findAllProvinceCity(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    //Get Provinces
                    provinces = response.body();
                    provinceAdapter = new ProvinceSpinAdapter(ReportNewHotelActivity.this, provinces);
                    spinProvince.setAdapter(provinceAdapter);
                    setupDefaultDistrict();
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });

        appUserForm = PreferenceUtils.getAppUser(this);
        if (appUserForm != null) {
            if (PreferenceUtils.getLoginType(ReportNewHotelActivity.this) == SignupType.Manual) {
                txtEmail.setText(appUserForm.getUserId());
            } else {
                txtEmail.setText(appUserForm.getEmail());
            }
        }
    }


    private void setupDefaultDistrict() {
        Address address = PreferenceUtils.getLastAddress(this);
        if (address != null) {
            String province = address.getAdminArea();
            Province provinceObject = findProvinceSn(province);

            Map<String, Object> params = new HashMap<>();
            if (provinceObject != null) {
                params.put("provinceSn", provinceObject.getSn());
                txtPhoneCode.setText(provinceObject.getAreaCode());
            } else {
                if (provinces != null && provinces.size() > 0) {
                    provinceObject = provinces.get(0);
                    params.put("provinceSn", provinceObject.getSn());
                } else {
                    params.put("provinceSn", 1);
                }
            }
            DialogUtils.showLoadingProgress(ReportNewHotelActivity.this, false);
            HotelApplication.serviceApi.findAllDistrictInProvince(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<District>>() {
                @Override
                public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                    DialogUtils.hideLoadingProgress();
                    if (response.isSuccessful()) {
                        //Get Districts
                        districts = response.body();
                        districtSpinAdapter = new DistrictSpinAdapter(ReportNewHotelActivity.this, districts);
                        spinDistrict.setAdapter(districtSpinAdapter);


                        setMyLocation();
                    }
                }

                @Override
                public void onFailure(Call<List<District>> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    Toast.makeText(ReportNewHotelActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private Province findProvinceSn(String province) {
        String tempProvince = Utils.removeAccent(province).toLowerCase();
        if (provinces != null) {
            for (int i = 0; i < provinces.size(); i++) {
                String temp = Utils.removeAccent(provinces.get(i).getName()).toLowerCase();
                if (temp.equals(tempProvince)) {
                    return provinces.get(i);
                }
            }
        }
        return null;
    }

    private int findProvinceIndex(String province) {

        String tempProvince = Utils.removeAccent(province).toLowerCase();

        if (provinces != null) {
            for (int i = 0; i < provinces.size(); i++) {
                String temp = Utils.removeAccent(provinces.get(i).getName()).toLowerCase();
                if (temp.equals(tempProvince)) {
                    return i;
                }
            }
        }
        return 0;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChooseLocation:
                openChooseLocation();
                break;
            case R.id.btnReport:
                sendReport();
                break;
            case R.id.btnChange:
                changeEmail();
                break;
            case R.id.btnChoosePhoto:

                final int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion < 23) {
                    openChoosePhotoPopup();
                } else {
                    checkPermissionCamera();
                }

                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }


    final private int REQUEST_CODE_ASK_CAMERA = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionCamera() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        //Add Permission
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add("Camera");
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("Write Sdcard");
        }
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("Read Sdcard");
        }

        //Check Permission List
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_CAMERA);
        } else {
            //Continue
            openChoosePhotoPopup();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_CAMERA: {
                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Continue
                    openChoosePhotoPopup();

                } else {
                    // Permission Denied
                    finish();
                }
            }
            break;
        }
    }


    private void openChoosePhotoPopup() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.photo_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        TextViewSFBold btnGallery = dialog.findViewById(R.id.btnGallery);
        TextViewSFBold btnTakePicture = dialog.findViewById(R.id.btnTakePicture);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, SELECT_PHOTO);
            }
        });
    }

    private void changeEmail() {
        txtEmail.setEnabled(true);
        txtEmail.requestFocus();
    }

    private void sendReport() {
        if (txtEmail.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.please_input_email), Toast.LENGTH_LONG).show();
            return;
        }
        if (txtAddress.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.please_input_hotel_address), Toast.LENGTH_LONG).show();
            return;
        }

        if (txtHotelName.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.please_input_hotel_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (txtPhoneCode.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.please_input_area_number), Toast.LENGTH_LONG).show();
            return;
        }

        if (txtPhoneNo.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.please_input_phone_number), Toast.LENGTH_LONG).show();
            return;
        }
        if (selectedImage == null) {
            Toast.makeText(this, getString(R.string.please_up_load_image), Toast.LENGTH_LONG).show();
            return;
        }


        ReportHotelDto reportHotelDto = new ReportHotelDto();
        reportHotelDto.setName(txtHotelName.getText().toString());
        reportHotelDto.setAddress(txtAddress.getText().toString());
        reportHotelDto.setAreaCode(txtPhoneCode.getText().toString());
        reportHotelDto.setPhone(txtPhoneNo.getText().toString());
        reportHotelDto.setLatitude(Double.toString(lat));
        reportHotelDto.setLongitude(Double.toString(lng));
        reportHotelDto.setEmail(txtEmail.getText().toString());
        reportHotelDto.setDistrictSn(districts.get(spinDistrict.getSelectedItemPosition()).getSn());

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.reportNewHotel(reportHotelDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {

                    try {
                        int hotelSn = restResult.getSn();
                        uploadImage(hotelSn);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ReportNewHotelActivity.this, getString(R.string.post_a_report_fail), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(ReportNewHotelActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void uploadImage(int hotelSn) {

        File file = new File(getPath(selectedImage));
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("homeFileUpload", file.getName(), requestFile);

        DialogUtils.showLoadingProgress(ReportNewHotelActivity.this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelSn);
        HotelApplication.serviceApi.createUpdateHotelHomeImage(params, body, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    if (restResult.getResult() == 1) {
                        showSuccessDialog();
                    }
                } else {
                    Toast.makeText(ReportNewHotelActivity.this, getString(R.string.post_a_report_fail), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(ReportNewHotelActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        TextView btnOK = (TextView) dialog.findViewById(R.id.btnOK);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(getString(R.string.report_is_sent_to));
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void openChooseLocation() {
        Intent intent = new Intent(this, ChooseLocationActivity.class);
        startActivityForResult(intent, CHOOSE_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_LOCATION) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    lat = bundle.getDouble("lat");
                    lng = bundle.getDouble("lng");
                    province = bundle.getString("province");
                    district = bundle.getString("district");
                    address = bundle.getString("address");

                    fillData();
                }
            }
        } else if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {

                selectedImage = data.getData();

                PictureGlide.getInstance().show(selectedImage,getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width), getResources().getDimensionPixelSize(R.dimen.hotel_list_height),R.drawable.loading_big, imgPhoto);

                tvUploadImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedImage != null) {
            outState.putString("cameraImageUri", selectedImage.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            selectedImage = Uri.parse(savedInstanceState.getString("cameraImageUri"));

            if (selectedImage != null) {
                PictureGlide.getInstance().show(selectedImage,imgPhoto);
                tvUploadImage.setVisibility(View.GONE);
            }
        }
    }


    private void fillData() {
        txtAddress.setText(address);
        int provinceIndex = findProvinceIndex(province);
        spinProvince.setSelection(provinceIndex);
        int provinceSn = provinces.get(provinceIndex).getSn();
        Map<String, Object> params = new HashMap<>();
        if (provinceSn != 0) {
            params.put("provinceSn", provinceSn);
        } else {
            if (provinces != null && provinces.size() > 0) {
                provinceSn = provinces.get(0).getSn();
            }
            params.put("provinceSn", provinceSn);
        }
        DialogUtils.showLoadingProgress(ReportNewHotelActivity.this, false);
        HotelApplication.serviceApi.findAllDistrictInProvince(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    districts = response.body();
                    districtSpinAdapter = new DistrictSpinAdapter(ReportNewHotelActivity.this, districts);
                    spinDistrict.setAdapter(districtSpinAdapter);
                    int districtIndex = findDistrictIndex();
                    spinDistrict.setSelection(districtIndex);
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(ReportNewHotelActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int findDistrictIndex() {
        String temDistrict = Utils.removeAccent(district).toLowerCase();
        System.out.println("temDistrict: " + temDistrict);
        if (provinces != null) {
            for (int i = 0; i < districts.size(); i++) {
                String temp = Utils.removeAccent(districts.get(i).getName()).toLowerCase();
                if (temp.equals(temDistrict)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void setMyLocation() {
        Address addrezz = PreferenceUtils.getLastAddress(this);

        if (addrezz != null) {
            address = PreferenceUtils.getCurrentAddress(this);
            province = addrezz.getAddressLine(3);
            district = addrezz.getAddressLine(2);

            fillData();
        }
    }


    @Override
    public void setScreenName() {
        this.screenName = "SFloatReport";
    }
}
