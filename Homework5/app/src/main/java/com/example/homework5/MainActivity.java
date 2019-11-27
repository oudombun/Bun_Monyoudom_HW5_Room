package com.example.homework5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.homework5.fragment.UpdateFragment;
import com.example.homework5.recycler.Book;
import com.example.homework5.recycler.BookAdapter;
import com.example.homework5.fragment.AddFragment;
import com.example.homework5.room.AppData;
import com.example.homework5.room.Category;
import com.example.homework5.room.MyBook;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddFragment.OnAddDiaologListener, UpdateFragment.OnUpdateDiaologListener , BookAdapter.OnBookChangeListener {

    FloatingActionButton btnShowAddFrag;
    AddFragment addFragment;
    UpdateFragment updateFragment;
    BookAdapter adapter;
    List<Book> list;
    List<MyBook> myBookList=null;
    RecyclerView recyclerView;
    Integer adapterPosition;
    AppData db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = Room.databaseBuilder(
                getApplicationContext(),
                AppData.class,
                "book_4")
                .allowMainThreadQueries()
                .build();

        /* Initalize view */

        btnShowAddFrag= findViewById(R.id.btnShowAddFrag);
        recyclerView  = findViewById(R.id.recyclerView);

        /* End Initalize view */

        /* action */
        btnShowAddFrag.setOnClickListener(v->{
            addFragment = new AddFragment(this,this);
            addFragment.setCancelable(false);
            addFragment.show(getSupportFragmentManager(),"Add");
        });


        /* recycler view */
        list = new ArrayList<>();
        myBookList = new ArrayList<>();
        adapter= new BookAdapter(this,list,this);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                2, LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        initilizeListBook();
        readBook();
    }

    @Override
    public void sendData(String title, String category, int page, float price, Uri uri) {
        Book book = new Book(title,page,category,price,uri);
        MyBook mBook = new MyBook(title,
                category,
                page,
                price,
                uri.toString());
        db.daoAPI().insertBook(mBook);
        list.add(book);
        adapter.notifyDataSetChanged();
    }

    private void addBook(Book book){


    }
    private List<MyBook> readBook(){
        List<MyBook> listFromdb = new ArrayList<>();
        listFromdb= db.daoAPI().getAllBook();

        for(int i=0;i<listFromdb.size();i++){
            Book book = new Book(
                    listFromdb.get(i).getTitle(),
                    listFromdb.get(i).getPage(),
                    listFromdb.get(i).getCategory(),
                    listFromdb.get(i).getPrice(),
                    Uri.parse(listFromdb.get(i).getImage()));
            list.add(book);
            adapter.notifyDataSetChanged();
        }
        return listFromdb;
    }

    private static final String TAG = "MainActivity";

    private void initilizeListBook(){
        if(db.daoAPI().getAllBook()==null || db.daoAPI().getAllBook().toString().equals("[]")){
            Bitmap icon[] = {
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.b1),
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.b2),
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.b3),
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.b4)
            };
            Uri uriDefualt;
            for(int i=0;i<icon.length;i++){
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon[i].compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), icon[i], "Titlew", null);
                uriDefualt= Uri.parse(path);
                db.daoAPI().insertBook(new MyBook("title"+(i+1),"Sale Book",50,3.5,uriDefualt.toString()));
            }
        }
        if(db.daoAPI().getAllCategory()==null || db.daoAPI().getAllCategory().toString().equals("[]")){
            db.daoAPI().insertCate(new Category("Sale Book"));
            db.daoAPI().insertCate(new Category("Donate Book"));
        }
    }
    @Override
    public void updateData(String title, String category, int page, float price, Uri image) {
        if(adapterPosition!=null){
            myBookList = db.daoAPI().getAllBook();
            MyBook mbook = myBookList.get(adapterPosition);


            Book book = list.get(adapterPosition);
            book.setTitle(title);
            book.setCategory(category);
            book.setPage(page);
            book.setPrice(price);
            book.setImage(image);

            mbook.setTitle(title);
            mbook.setCategory(category);
            mbook.setPage(page);
            mbook.setPrice(price);
            mbook.setImage(image.toString());

            db.daoAPI().updateBook(mbook);
            list.set(adapterPosition,book);
            adapter.notifyDataSetChanged();
        }
    }

    public Book editBook(int position){
        return list.get(position);
    }
    private void deleteBook(int position) {
        myBookList = db.daoAPI().getAllBook();
        MyBook book = myBookList.get(position);
        db.daoAPI().deleteBook(book);

        list.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,list.size());
    }

    @Override
    public void onOption3dotClick(int position,View view) {
        adapterPosition = position;
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.more_actions, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.pop_view:
                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    myBookList = db.daoAPI().getAllBook();
                    MyBook book = myBookList.get(position);
                    intent.putExtra("TITLE",book.getTitle());
                    intent.putExtra("CATE",book.getCategory());
                    intent.putExtra("PAGE",book.getPage()+"");
                    intent.putExtra("PRICE",book.getPrice()+"");
                    intent.putExtra("IMG",book.getImage());
                    startActivity(intent);
                    break;
                case R.id.pop_edit:
                    updateFragment = new UpdateFragment(MainActivity.this,editBook(position),MainActivity.this);
                    updateFragment.setCancelable(false);
                    updateFragment.show(getSupportFragmentManager(),"Update");
                    break;
                case R.id.pop_delete:
                    AlertDialog dialog=new AlertDialog.Builder(this)
                            .setTitle("Delete")
                            .setMessage("do you want to delete this item?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteBook(position);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                    break;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusearch, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}
