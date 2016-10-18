package com.hasbrain.chooseyourcar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasbrain.chooseyourcar.datastore.AssetBasedCarDatastoreImpl;
import com.hasbrain.chooseyourcar.datastore.CarDatastore;
import com.hasbrain.chooseyourcar.datastore.OnCarReceivedListener;
import com.hasbrain.chooseyourcar.model.Car;

import java.io.IOException;
import java.util.List;

/**
 * Created by sinhhx on 10/17/16.
 */
public class CarDetailFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragmentcardetail, container, false);
        final ImageView cardetail = (ImageView) rootView.findViewById(R.id.carpic);
        final TextView cardiscript = (TextView) rootView.findViewById(R.id.cardescription);
        Gson gson = new GsonBuilder().create();
        final Bundle bundle = this.getArguments();

        final CarDatastore carDatastore = new AssetBasedCarDatastoreImpl(getContext(), "car_data.json", gson);
        carDatastore.getCarList(new OnCarReceivedListener() {
            @Override
            public void onCarReceived(List<Car> cars, Exception ex) {
                Bitmap bitmap = null;
                Car car =cars.get(bundle.getInt("position"));
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(car.getImageUrl()));
                    cardetail.setImageBitmap(bitmap);

                    String str = car.getName()+"\n"+car.getBrand();
                    SpannableString ss1=  new SpannableString(str);
                    ss1.setSpan(new RelativeSizeSpan(1.7f), 0,car.getName().length(), 0);
                    cardiscript.setText(ss1);




                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        return rootView;
    }
}
