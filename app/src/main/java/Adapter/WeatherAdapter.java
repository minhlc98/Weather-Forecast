package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.R;

import java.util.ArrayList;

import Model.Weather;

public class WeatherAdapter extends BaseAdapter {
    private ArrayList<Weather> arr;

    public WeatherAdapter(ArrayList<Weather> arr, Context context) {
        this.arr = arr;
        this.context = context;
    }

    private Context context;

    @Override
    public int getCount() {
        return arr.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_each_day, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = convertView.findViewById(R.id.tvdate);
            viewHolder.tvStatus = convertView.findViewById(R.id.tvStatus_list);
            viewHolder.tvTemp = convertView.findViewById(R.id.tvTemp);
            viewHolder.imgWeather = convertView.findViewById(R.id.imgWeather_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvDate.setText(arr.get(position).getDate());
        viewHolder.tvStatus.setText(arr.get(position).getStatus());
        viewHolder.tvTemp.setText(arr.get(position).getTemp());
        Glide.with(context).load(arr.get(position).getImgae()).into(viewHolder.imgWeather);
        return convertView;
    }

    public class ViewHolder {
        TextView tvDate, tvStatus, tvTemp;
        ImageView imgWeather;
    }
}
