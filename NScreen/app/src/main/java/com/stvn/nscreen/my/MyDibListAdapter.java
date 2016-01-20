package com.stvn.nscreen.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jjiya.android.common.CMDateUtil;
import com.jjiya.android.common.ListViewDataObject;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SwipeListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class MyDibListAdapter extends ArrayAdapter<ListViewDataObject> {

	LayoutInflater mInflater;
	private Context mContext;

	public MyDibListAdapter(Context context, ArrayList<ListViewDataObject> items) {
		super(context, 0, items);
		mContext = context;
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
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_mycnm_watch, null);
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

		ListViewDataObject info = getItem(position);

		try {

			JSONObject jsonObj = new JSONObject(info.sJson);
			JSONObject assetObj = jsonObj.getJSONObject("asset");

			String title = assetObj.getString("title");
			holder.rowname.setText(title);

			String addTime = jsonObj.getString("addTime");
			String pDay = CMDateUtil.findDateWithFormat(addTime, "MM.dd") +"("+ CMDateUtil.getDayOfWeek(addTime)+")";
			holder.rowdate.setText(pDay);

			String pTime = CMDateUtil.findDateWithFormat(addTime, "HH:mm");
			holder.rowtime.setText(pTime);

		} catch (Exception e) {
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
