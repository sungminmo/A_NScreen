package com.stvn.nscreen.rmt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.common.ViewHolder;
import com.stvn.nscreen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by limdavid on 15. 10. 26..
 */
public class RemoteControllerListViewAdapter extends BaseAdapter {

    private static final String                        tag              = RemoteControllerListViewAdapter.class.getSimpleName();
    private Context mContext         = null;
    private              View.OnClickListener          mOnClickListener = null;
    private ArrayList<ListViewDataObject> mDatas           = new ArrayList<ListViewDataObject>();

    public RemoteControllerListViewAdapter(Context c, View.OnClickListener onClickListener) {
        super();

        this.mContext         = c;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public int getCount() { return mDatas.size(); }

    @Override
    public Object getItem(int position) { return mDatas.get(position); }

    @Override
    public long getItemId(int position) { return mDatas.get(position).iKey; }

    public void set(int position, ListViewDataObject obj) { mDatas.set(position, obj); }
    public void addItem(ListViewDataObject obj) { mDatas.add(obj); }
    public void remove(int position) { mDatas.remove(position); }
    public void clear() { mDatas.clear(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_remote_controller, parent, false);
        }

        try {
            ListViewDataObject dobj = (ListViewDataObject)getItem(position);
            JSONObject jobj = new JSONObject(dobj.sJson);

            TextView titleTextView = ViewHolder.get(convertView, R.id.remote_textview_program_title);

            titleTextView.setText(jobj.getString("programTitle"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}