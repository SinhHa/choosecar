package com.hasbrain.chooseyourcar.datastore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.hasbrain.chooseyourcar.model.Car;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/16/15.
 */
public class AssetBasedCarDatastoreImpl implements CarDatastore {
    private Context context;
    private String dataFilename;
    private Gson gson;

    public AssetBasedCarDatastoreImpl(Context context, String dataFilename, Gson gson) {
        this.context = context;
        this.dataFilename = dataFilename;
        this.gson = gson;
    }

    @Override
    public void getCarList(OnCarReceivedListener onCarReceivedListener) {
        if (onCarReceivedListener != null) {
            Type type = new TypeToken<List<Car>>(){}.getType();
            InputStream is = null;
            try {
                is = context.getAssets().open(dataFilename);
                List<Car> cars = gson.fromJson(new InputStreamReader(is), type);
                for (Car car : cars) {
                    car.setImageUrl("android.resource://com.hasbrain.chooseyourcar/raw/" + car.getImageUrl());
                }
                //Duplicate data.
                for (int i = 0; i < 10; i++) {
                    cars.addAll(new ArrayList<>(cars));
                }

                onCarReceivedListener.onCarReceived(cars, null);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
