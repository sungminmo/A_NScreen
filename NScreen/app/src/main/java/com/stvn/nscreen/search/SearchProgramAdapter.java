package com.stvn.nscreen.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchDataObject;
import com.stvn.nscreen.util.CMUtil;

import java.util.List;

public class SearchProgramAdapter extends ArrayAdapter<SearchDataObject> {

	LayoutInflater mInflater;
	private Context mContext;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	public SearchProgramAdapter(Context context, List<SearchDataObject> items)
	{
		super(context, 0, items);
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mRequestQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);
			public void putBitmap(String url, Bitmap bitmap) {
				mCache.put(url, bitmap);
			}
			public Bitmap getBitmap(String url) {
				return mCache.get(url);
			}
		});
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
			holder.chlogo = (NetworkImageView)convertView.findViewById(R.id.ch_logo);
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
		SearchDataObject item = getItem(position);
		holder.chlogo.setImageUrl(item.getChannelLogoImg(), mImageLoader);
		holder.chaaneltext.setText(item.getChannelNumber());
		holder.programname.setText(item.getChannelProgramTitle());
		String datetime = item.getChannelProgramTime().trim();
//		2015-10-28 04:30:00

		holder.chtimetext.setText(CMUtil.getConverDateString(datetime, "yyyy-MM-ddHH:mm:ss", "HH:mm"));
		holder.channeltext2.setText(CMUtil.getConverDateString(datetime, "yyyy-MM-ddHH:mm:ss", "yyyy.MM.dd") + " 방송예정");
		holder.chhd.setVisibility(View.VISIBLE);
		if("HD".equals(item.getChannelProgramHD())){			// HD방송
			holder.chhd.setImageResource(R.mipmap.btn_size_hd);
		}else if("SD".equals(item.getChannelProgramHD())){		// SD방송
			holder.chhd.setImageResource(R.mipmap.btn_size_sd);
		}else													// 둘다 아닐때
			holder.chhd.setVisibility(View.GONE);

		// 연령제한
		if(!TextUtils.isEmpty(item.getChannelProgramGrade()))
		{
			if(item.getChannelProgramGrade().indexOf("12")>-1)
			{
				holder.chage.setImageResource(R.mipmap.btn_age_all);
			}else if(item.getChannelProgramGrade().indexOf("15")>-1){
				holder.chage.setImageResource(R.mipmap.btn_age_15);
			}else if(item.getChannelProgramGrade().indexOf("19")>-1){
				holder.chage.setImageResource(R.mipmap.btn_age_19);
			}else {
				holder.chage.setImageResource(R.mipmap.btn_age_all);
			}
		}



		holder.progress.setProgress(position);

		return convertView;
	}
	
	class ViewHolder {
		ImageView favoriteimg;
		TextView chaaneltext;
		TextView programname;
		NetworkImageView chlogo;
		TextView chtimetext;
		TextView channeltext2;
		ImageView chage;
		ImageView chhd;
		ProgressBar progress;
	}
	
}
