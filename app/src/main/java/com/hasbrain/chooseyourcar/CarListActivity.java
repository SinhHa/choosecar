package com.hasbrain.chooseyourcar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.hasbrain.chooseyourcar.datastore.AssetBasedCarDatastoreImpl;
import com.hasbrain.chooseyourcar.datastore.CarDatastore;
import com.hasbrain.chooseyourcar.datastore.OnCarReceivedListener;
import com.hasbrain.chooseyourcar.loader.ImageLoader;
import com.hasbrain.chooseyourcar.model.Car;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class CarListActivity extends AppCompatActivity {
    RecyclerView carlist ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclecarelist);
        Gson gson = new GsonBuilder().create();
        final CarDatastore carDatastore = new AssetBasedCarDatastoreImpl(this, "car_data.json", gson);
        carDatastore.getCarList(new OnCarReceivedListener() {
            @Override
            public void onCarReceived(List<Car> cars, Exception ex) {
                carlist = (RecyclerView) findViewById(R.id.my_recycler_view);
                carlist.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                carlist.addItemDecoration(new VerticalSpaceItemDecoration(100));
                carlist.setLayoutManager(mLayoutManager);
                CarListAdapter adap = new CarListAdapter(CarListActivity.this, cars);
                carlist.setAdapter(adap);
            }
        });
        final GestureDetector mGestureDetector = new GestureDetector(CarListActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                return true;
            }
        });
        carlist.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childview = rv.findChildViewUnder(e.getX(), e.getY());

                int pos = carlist.getChildAdapterPosition(childview);

                    if (childview != null && mGestureDetector.onTouchEvent(e)) {


                        Intent intent = new Intent(CarListActivity.this, CarDetailActivity.class);
                        Bundle extras = new Bundle();
                        int i =pos;
                        while(i>13){
                            i=i-13;
                        }
                        extras.putInt("position",i);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }



                return false;
            }



            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mVerticalSpaceHeight;
    }}}
