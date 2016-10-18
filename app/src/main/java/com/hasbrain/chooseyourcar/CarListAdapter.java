package com.hasbrain.chooseyourcar;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasbrain.chooseyourcar.model.Car;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by sinhhx on 10/13/16.
 */
public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.ViewHolder> {
    Context context;
    List<Car> cars;
    Bitmap carbitmap = null;
    private LruCache<String, Bitmap> mMemoryCache;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 8;



    public CarListAdapter(Context context, List<Car> cars) {
        this.cars = cars;
        this.context = context;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView carname;
        public ImageView carimage;

        public ViewHolder(View itemView) {
            super(itemView);
            carname = (TextView) itemView.findViewById(R.id.carname);
            carimage = (ImageView) itemView.findViewById(R.id.carimage);

        }
    }

    public CarListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.displaycar, parent, false);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };


        ViewHolder vh = new ViewHolder(v);

        return vh;
    }



    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Bitmap carbitmap = null;
        Bitmap bitmap = null;
        Car newcar = cars.get(position);
        bitmap = getBitmapFromMemCache(String.valueOf(position));
        if (bitmap != null) {
            holder.carimage.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(position, holder.carimage)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(holder.carimage, holder.carname, position);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(context.getResources(), carbitmap, task);
                holder.carimage.setImageDrawable(asyncDrawable);
                task.execute(position);
            }
            holder.carimage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }


    }


    @Override
    public int getItemCount() {
        return cars.size();
    }


    public Bitmap getResizedBitmap(Bitmap image) {
        Bitmap scale = Bitmap.createScaledBitmap(image, 1280, 960, true);

        return scale;

    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private final TextView textview;
        private  final int position;
        public BitmapWorkerTask(ImageView imageView, TextView textview, int position) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.textview = textview;
            this.position =position;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(cars.get(params[0]).getImageUrl()));
                addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
                carbitmap = getResizedBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return carbitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    textview.setText(cars.get(position).getBrand()+" "+cars.get(position).getName());
                }

            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
