package com.example.homework5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView1,textView2,textView3,textView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView= findViewById(R.id.d_book_thumbnail);
        textView1= findViewById(R.id.d_book_title);
        textView2= findViewById(R.id.d_book_cate);
        textView3= findViewById(R.id.d_book_page);
        textView4= findViewById(R.id.d_book_price);



        Intent intent = getIntent();
        textView1.setText(intent.getStringExtra("TITLE"));
        textView2.setText(intent.getStringExtra("CATE"));
        textView3.setText(intent.getStringExtra("PAGE"));
        textView4.setText(intent.getStringExtra("PRICE"));
        Picasso.get().load(Uri.parse(intent.getStringExtra("IMG"))).fit().centerCrop().into(imageView);

    }
}
