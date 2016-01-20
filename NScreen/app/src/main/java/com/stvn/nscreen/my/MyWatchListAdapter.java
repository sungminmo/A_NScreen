package com.stvn.nscreen.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SwipeListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyWatchListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<JSONObject> mList;

	public MyWatchListAdapter(Context context, ArrayList<JSONObject> arr)
	{
		mList     = arr;
		mContext  = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public JSONObject getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.row_mycnm_watch, parent,false);
			holder = new ViewHolder();
			holder.rowdate = (TextView)convertView.findViewById(R.id.row_date);
			holder.rowtime = (TextView)convertView.findViewById(R.id.row_time);
			holder.rowname = (TextView)convertView.findViewById(R.id.row_name);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			JSONObject jo   = getItem(position);
			String sTitle   = jo.getString("sTitle");
			String sDate    = jo.getString("sDate");
			String[] sDates = sDate.split(",");

			holder.rowdate.setText(sDates[0]);
			holder.rowtime.setText(sDates[1]);
			holder.rowname.setText(sTitle);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	class ViewHolder {

		TextView rowdate;
		TextView rowtime;
		TextView rowname;
	}
	
}
