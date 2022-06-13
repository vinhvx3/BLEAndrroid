package com.example.myfirstapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BLEDeviceAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<BLEDevice> bleDeviceList;
    private OnItemClickListener itemClickListener;

    public BLEDeviceAdapter(Context context, int layout, List<BLEDevice> bleDeviceList) {
        this.context = context;
        this.layout = layout;
        this.bleDeviceList = bleDeviceList;
    }

    void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    protected class ViewHolder {
        TextView txtName, txtDescription;
        ImageView ivInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(layout, null);

            holder = new ViewHolder();

            holder.txtName = (TextView) convertView.findViewById(R.id.textviewname);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.textviewdescription);
            holder.ivInfo = convertView.findViewById(R.id.imageviewicon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        BLEDevice device = bleDeviceList.get(position);
        holder.txtName.setText(device.getName());
        holder.txtDescription.setText(device.getDescription());

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position, v);
            }
        });

        holder.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position, v);
            }
        });

        return convertView;
    }
}
