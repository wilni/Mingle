package com.example.mingle;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class arrayAdapter extends android.widget.ArrayAdapter<Cards> {

    Context context;

    public arrayAdapter(Context context, int resourceId, List<Cards> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Cards cardItem = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.cardName);
        ImageView image = (ImageView) convertView.findViewById(R.id.cardImage);

        name.setText(cardItem.getName());
        switch(cardItem.getProfilePicUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.default_icon).into(image);
                break;
            default:
                Glide.with(convertView.getContext()).load(cardItem.getProfilePicUrl()).into(image);
                break;
        }


        return convertView;
    }

}
