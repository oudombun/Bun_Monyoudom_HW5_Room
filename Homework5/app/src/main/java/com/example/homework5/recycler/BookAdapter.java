package com.example.homework5.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homework5.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyHolder> {

    private List<Book> mList=new ArrayList<>();
    private OnBookChangeListener mListener;
    Context context;

    public BookAdapter(Context context,List<Book> mList, OnBookChangeListener mListener) {
        this.mList = mList;
        this.mListener = mListener;
        this.context = context;
    }

    public interface OnBookChangeListener{
        void onOption3dotClick(int position,View view);
}

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.book_item,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Book book = mList.get(position);
        holder.b_title.setText(book.getTitle());
        holder.b_page.setText(book.getPage()+" page");
        holder.b_price.setText("$"+book.getPrice());
        holder.b_cate.setText(book.getCategory());
        Picasso.get().load(book.getImage()).fit().centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView b_title,b_price,b_page,b_cate;
        ImageView imageView,btnOption;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            b_title = itemView.findViewById(R.id.book_title);
            b_price = itemView.findViewById(R.id.book_price);
            b_page = itemView.findViewById(R.id.book_page);
            b_cate = itemView.findViewById(R.id.book_cate);
            imageView = itemView.findViewById(R.id.book_thumbnail);
            btnOption= itemView.findViewById(R.id.btnOption);

            btnOption.setOnClickListener(v->{
                if(mListener !=null){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        mListener.onOption3dotClick(pos,v);
                    }
                }
            });
        }
    }


}
