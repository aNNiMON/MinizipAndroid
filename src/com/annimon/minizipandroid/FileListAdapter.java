package com.annimon.minizipandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for listview [icon filename].
 * @author aNNiMON
 */
public class FileListAdapter extends BaseAdapter {
    
    private LayoutInflater inflater;
    private List<String> objects;

    public FileListAdapter(Context context, List<String> objects) {
        this.objects = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public Object getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.file_item, parent, false);
        }
        
        String item = (String) getItem(position);
        // Set icon by filename type.
        int iconId = item.endsWith("/") ? R.drawable.icon_folder : R.drawable.icon_file;
        if (item.toLowerCase(Locale.getDefault()).endsWith(".zip")) iconId = R.drawable.icon_zip;
        ((ImageView) view.findViewById(R.id.file_item_icon)).setImageResource(iconId);
        // Set filename.
        ((TextView) view.findViewById(R.id.file_item_name)).setText(item);
        
        return view;
    }
    
}
