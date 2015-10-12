package com.stvn.nscreen.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stvn.nscreen.R;

import java.util.List;

public class SearchKeywordAdapter extends ArrayAdapter<String> {

	LayoutInflater mInflater;
	private Context mContext;
	private View.OnClickListener clickListener;


	public SearchKeywordAdapter(Context context, List<String> items)
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
			convertView = mInflater.inflate(R.layout.row_search_keyword, null);
			holder = new ViewHolder();
			holder.keyword = (TextView)convertView.findViewById(R.id.keyword);
			holder.close = (ImageView)convertView.findViewById(R.id.keywordclose);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		String value = getItem(position);
		holder.keyword.setTag(value);
		holder.close.setTag(value);
		holder.keyword.setText(value);
		holder.keyword.setOnClickListener(clickListener);
		holder.close.setOnClickListener(clickListener);

		return convertView;
	}

	public View.OnClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(View.OnClickListener clickListener) {
		this.clickListener = clickListener;
	}
	
	class ViewHolder {
		TextView keyword;
		ImageView close;
	}
	
}
