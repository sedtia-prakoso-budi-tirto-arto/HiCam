package com.example.fp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.List;

public class CustomGrid extends BaseAdapter {
    private Context mContext;
//    private final int[] Imageid;

    private  List<Uri> Imageid;
    public CustomGrid(Context c, List<Uri> imageUris) {
        mContext = c;
        this.Imageid = imageUris;
    }

    @Override
    public int getCount() {
        return Imageid.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.item_image, null);
            ImageView imageView = grid.findViewById(R.id.list_item);
//            imageView.setImageResource(Imageid[position]);
            Glide.with(mContext)
                    .load(Imageid.get(position))
                    .into(imageView);
        } else {
            grid = convertView;
        }

        return grid;
    }
}
