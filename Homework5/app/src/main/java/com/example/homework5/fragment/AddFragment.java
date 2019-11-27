package com.example.homework5.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.example.homework5.R;
import com.example.homework5.room.AppData;
import com.example.homework5.room.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddFragment extends DialogFragment  implements EasyPermissions.PermissionCallbacks {
    public static final int IMG_REQ = 2;
    public static final int OPEN_GALLERY_REQ_CODE = 1;

    Context context;
    Button btnChoose,btnInsert,btnCancle;
    ImageView imageView;
    EditText mTitle,mPage,mPrice;
    Spinner mCategory;
    Bitmap bitmap;
    Uri uri=null;

    String title,category;
    int page;
    float price;

    public AddFragment(Context context, OnAddDiaologListener onAddDiaologListener) {
        this.context = context;
        this.onAddDiaologListener = onAddDiaologListener;
    }

    public interface OnAddDiaologListener{
        void sendData(String title,String category,int page , float price,Uri image);
    }

    public OnAddDiaologListener onAddDiaologListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddDiaologListener = (OnAddDiaologListener) getActivity();
        }catch (ClassCastException e ){

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_fragment,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);

        /*initialize view */

        btnChoose = view.findViewById(R.id.btnChoose);
        imageView = view.findViewById(R.id.myImg);
        btnInsert = view.findViewById(R.id.btnInsert);
        btnCancle = view.findViewById(R.id.btnCancel);
        mTitle = view.findViewById(R.id.myTitle);
        mPage = view.findViewById(R.id.myPage);
        mPrice = view.findViewById(R.id.myPrice);
        mCategory = view.findViewById(R.id.myCate);
        category ="";

        /*init spinner*/
        AppData db = Room.databaseBuilder(
                context,
                AppData.class,
                "book_4")
                .allowMainThreadQueries()
                .build();

        List<Category> categories = db.daoAPI().getAllCategory();
        String[] cates = new String[categories.size()];
        for(int i=0;i<categories.size();i++){
            cates[i]= categories.get(i).getCateName();
        }

//        String cates[] = new String[]{"A","B"};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context,android.R.layout.simple_spinner_dropdown_item,cates
        );
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(spinnerArrayAdapter);
        mCategory.setPrompt("Category");
        category=cates[0];
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }
        });



        /*action*/
        btnChoose.setOnClickListener(v->{
            openGallery();
        });

        btnInsert.setOnClickListener(v -> {
            boolean valid= false;
            boolean isNumOk = false;
            String pageToInt = mPage.getText().toString().trim();
            String priceToFloat = mPrice.getText().toString().trim();
            String t = mTitle.getText().toString().trim();

            if(t.isEmpty()){
                valid = false;
                showMsg("please input title");
            }
            else if(priceToFloat.isEmpty()){
                valid = false;
                showMsg("please input price");
            }else if(pageToInt.isEmpty()){
                valid = false;
                showMsg("please input page");
            }
            else if(category.trim().equals("")){
                valid = false;
                showMsg("please choose category");
            }
            else {
                try {
                    page = Integer.parseInt(pageToInt);
                    price = Float.parseFloat(priceToFloat);
                } catch (NumberFormatException e) {
                    valid = false;
                    showMsg("wrong format of page or price");
                }
                valid = true;
            }
            title = mTitle.getText().toString();

            if(valid){
                if(uri==null){
                    Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.placeholder_book);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), icon, "Titless", null);
                    Uri uriDefualt= Uri.parse(path);

                    onAddDiaologListener.sendData(title,category,page,price,uriDefualt);
                }else {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Titlesss", null);
                        Uri uriChoose= Uri.parse(path);
                        onAddDiaologListener.sendData(title,category,page,price,uriChoose);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });
        btnCancle.setOnClickListener(v->{
            dismiss();
        });

        return builder.create();
    }

    private void showMsg(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    /* check permission to open gallery */

    @AfterPermissionGranted(OPEN_GALLERY_REQ_CODE)
    private void openGallery() {
        String[] perms ={Manifest.permission.READ_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(context,perms)){
            chooseImage();
        }else{
            EasyPermissions.requestPermissions(this,
                    "Please allow permission to open gallery",
                    OPEN_GALLERY_REQ_CODE,
                    perms);
        }
    }

    /* choose image function */
    public void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMG_REQ);
    }

    /* set choosed img to ImageView */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == IMG_REQ && data != null){
            uri = data.getData();
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /* request permission */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        chooseImage();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
