package com.hasbrain.chooseyourcar.datastore;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 10/16/15.
 */
public interface CarDatastore {
    void getCarList(OnCarReceivedListener onCarReceivedListener);
}
