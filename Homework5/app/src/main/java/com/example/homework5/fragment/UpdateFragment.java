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
import com.example.homework5.recycler.Book;
import com.example.homework5.room.AppData;
import com.example.homework5.room.Category;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateFragment extends DialogFragment  implements EasyPermissions.PermissionCallbacks {
    public static final int IMG_REQ = 2;
    public static final int OPEN_GALLERY_REQ_CODE = 1;

    Context context;
    Button btnChoose, btnUpdate,btnCancle;
    ImageView imageView;
    EditText mTitle,mPage,mPrice;
    Spinner mCategory;
    Bitmap bitmap;
    Book oldBook;
    Uri uri=null;
    String title,category;
    int page;
    float price;

    public UpdateFragment(Context context, Book oldBook, OnUpdateDiaologListener onUpdateDiaologListener) {
        this.context = context;
        this.oldBook = oldBook;
        this.onUpdateDiaologListener = onUpdateDiaologListener;
    }

    public interface OnUpdateDiaologListener {
        void updateData(String title, String category, int page, float price, Uri image);
    }

    public OnUpdateDiaologListener onUpdateDiaologListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onUpdateDiaologListener = (OnUpdateDiaologListener) getActivity();
        }catch (ClassCastException e ){

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_fragment,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);

        /*initialize view */

        btnChoose = view.findViewById(R.id.up_frag_btnChoose);
        imageView = view.findViewById(R.id.up_frag_myImg);
        btnUpdate = view.findViewById(R.id.up_frag_btnUpdate);
        btnCancle = view.findViewById(R.id.up_frag_btnCancel);
        mTitle = view.findViewById(R.id.up_frag_myTitle);
        mPage = view.findViewById(R.id.up_frag_myPage);
        mPrice = view.findViewById(R.id.up_frag_myPrice);
        mCategory = view.findViewById(R.id.up_frag_myCate);
        category ="";

        mTitle.setText(oldBook.getTitle());
        mPage.setText(oldBook.getPage()+"");
        mPrice.setText(oldBook.getPrice()+"");
        Picasso.get().load(oldBook.getImage()).fit().centerCrop().into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


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

        btnUpdate.setOnClickListener(v -> {
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
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), icon, "Title", null);
                    Uri uriDefualt= Uri.parse(path);

                    onUpdateDiaologListener.updateData(title,category,page,price,uriDefualt);
                }else {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
                        Uri uriChoose= Uri.parse(path);
                        onUpdateDiaologListener.updateData(title,category,page,price,uriChoose);
                    } catch (FileNotFoundException e) {
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

    private static final String TAG = "UpdateFragment";
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
            if(uri==null){
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.placeholder_book);
                imageView.setImageBitmap(icon);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else{
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
