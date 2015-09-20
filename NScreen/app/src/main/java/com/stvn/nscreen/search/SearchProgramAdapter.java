package com.stvn.nscreen.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stvn.nscreen.R;

import java.util.List;

public class SearchProgramAdapter extends ArrayAdapter<Integer> {

	LayoutInflater mInflater;
	private Context mContext;


	public SearchProgramAdapter(Context context, List<Integer> items)
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
			convertView = mInflater.inflate(R.layout.row_search_program, null);
			holder = new ViewHolder();
			holder.favoriteimg = (ImageView)convertView.findViewById(R.id.favorite_img);
			holder.chaaneltext = (TextView)convertView.findViewById(R.id.channel_text);
			holder.programname = (TextView)convertView.findViewById(R.id.programname);
			holder.chlogo = (ImageView)convertView.findViewById(R.id.ch_logo);
			holder.chtimetext = (TextView)convertView.findViewById(R.id.ch_time_text);
			holder.channeltext2 = (TextView)convertView.findViewById(R.id.ch_text2);
			holder.chage = (ImageView)convertView.findViewById(R.id.ch_age);
			holder.chhd = (ImageView)convertView.findViewById(R.id.ch_hd);
			holder.progress = (ProgressBar)convertView.findViewById(R.id.ch_progress);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		int value = getItem(position);
		if(value%2 == 0)
		{
			holder.favoriteimg.setSelected(true);
			holder.channeltext2.setVisibility(View.GONE);
		}
		else{
			holder.favoriteimg.setSelected(false);
			holder.channeltext2.setVisibility(View.VISIBLE);
		}

		holder.chaaneltext.setText("0"+value);
		holder.programname.setText("뉴스파이터");
		holder.chtimetext.setText("15:00 ~ 16:00");
		holder.channeltext2.setText("2015.09.08 방송예정");
		holder.chage.setImageResource(R.mipmap.btn_age_15);
		holder.chhd.setImageResource(R.mipmap.btn_size_hd);
		holder.progress.setProgress(value);

		return convertView;
	}
	
	class ViewHolder {
		ImageView favoriteimg;
		TextView chaaneltext;
		TextView programname;
		ImageView chlogo;
		TextView chtimetext;
		TextView channeltext2;
		ImageView chage;
		ImageView chhd;
		ProgressBar progress;
	}
	
}
