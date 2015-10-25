package com.stvn.nscreen.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchDataObject;

import java.util.List;

public class SearchVodAdapter extends ArrayAdapter<SearchDataObject> {

	LayoutInflater mInflater;
	private Context mContext;


	public SearchVodAdapter(Context context, List<SearchDataObject> items)
	{
		super(context, 0, items);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.row_search_vod, null);
			holder = new ViewHolder();
			holder.programname = (TextView)convertView.findViewById(R.id.programname);
			holder.poster = (ImageView)convertView.findViewById(R.id.poster);
			holder.event = (ImageView)convertView.findViewById(R.id.event_icon);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		SearchDataObject item = getItem(position);
		holder.poster.setImageResource(R.mipmap.postersample);
		int imgresource = R.mipmap.vod_01;
		switch (position%8)
		{
			case 0:
				imgresource = R.mipmap.vod_01;
				break;
			case 1:
				imgresource = R.mipmap.vod_02;
				break;
			case 2:
				imgresource = R.mipmap.vod_03;
				break;
			case 3:
				imgresource = R.mipmap.vod_04;
				break;
			case 4:
				imgresource = R.mipmap.vod_05;
				break;
			case 5:
				imgresource = R.mipmap.vod_06;
				break;
			case 6:
				imgresource = R.mipmap.vod_07;
				break;
			case 7:
				imgresource = R.mipmap.vod_08;
				break;
		}
		holder.event.setImageResource(imgresource);
		holder.programname.setText("건축학개론");

		return convertView;
	}
	
	class ViewHolder {
		TextView programname;
		ImageView poster;
		ImageView event;
	}
	
}
