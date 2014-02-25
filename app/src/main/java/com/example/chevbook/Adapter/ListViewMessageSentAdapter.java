package com.example.chevbook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chevbook.Class.Messages;
import com.example.chevbook.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Ugho on 24/02/14.
 */
public class ListViewMessageSentAdapter extends BaseAdapter {

    private final List<Messages> list;
    private final Context _c;

    private final ImageLoader imageLoader = ImageLoader.getInstance();

    public ListViewMessageSentAdapter(Context context) {

        this._c = context;
        this.list = null;
    }

    public ListViewMessageSentAdapter(Context context, List<Messages> ListMessages) {

        this._c = context;
        this.list = ListMessages;

    }

    private static class ViewHolder {
        public ImageView picture;
        public TextView title;
        public TextView userName;
        public TextView date;
        public TextView message;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //return list.size();
        return 7;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_item_messages_sent, null);
            holder = new ViewHolder();
            holder.picture = (ImageView) v.findViewById(R.id.imageViewRowItemMessagesSentPicture);
            holder.title = (TextView) v.findViewById(R.id.textViewRowItemMessagesSentTitleAnnounce);
            holder.userName = (TextView) v.findViewById(R.id.textViewRowItemMessagesSentUserName);
            holder.message = (TextView) v.findViewById(R.id.textViewRowItemMessagesSentMessage);
            holder.date = (TextView) v.findViewById(R.id.textViewRowItemMessagesSentDate);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        //todo

        String url_image = "https://fbcdn-sphotos-e-a.akamaihd.net/hphotos-ak-prn2/t1/1546110_10202187125312508_9323923_n.jpg";
        imageLoader.displayImage(url_image, holder.picture);

        return v;
    }
}
