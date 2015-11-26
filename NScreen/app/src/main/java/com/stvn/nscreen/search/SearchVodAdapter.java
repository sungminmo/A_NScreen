package com.stvn.nscreen.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jjiya.android.common.JYSharedPreferences;
import com.jjiya.android.common.UiUtil;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchVodDataObject;

import java.util.List;

public class SearchVodAdapter extends ArrayAdapter<SearchVodDataObject> {

	LayoutInflater mInflater;
	private Context mContext;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private JYSharedPreferences mPref;

	public SearchVodAdapter(Context context, List<SearchVodDataObject> items)
	{
		super(context, 0, items);
		// TODO Auto-generated constructor stub
		this.mContext = context;
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
		mPref = new JYSharedPreferences(context);
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
			holder.poster = (NetworkImageView)convertView.findViewById(R.id.poster);
			holder.event = (ImageView)convertView.findViewById(R.id.event_icon);
			holder.adultDim = (ImageView)convertView.findViewById(R.id.adult_dim);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		SearchVodDataObject item = getItem(position);
		holder.poster.setImageUrl(item.smallImageFileName, mImageLoader);
		holder.event.setVisibility(View.VISIBLE);

		boolean isNew = Boolean.getBoolean(item.isNew);
		boolean hot = Boolean.getBoolean(item.hot);
		UiUtil.setPromotionSticker(item.promotionSticker, isNew, hot, item.assetNew, item.assetHot, holder.event);

		holder.programname.setText(item.title);

		if (item.rating.startsWith("19") && mPref.isAdultVerification() == false) {
			holder.adultDim.setVisibility(View.VISIBLE);
		} else {
			holder.adultDim.setVisibility(View.GONE);
		}

		return convertView;
	}
	
	class ViewHolder {
		TextView programname;
		NetworkImageView poster;
		ImageView event;
		ImageView adultDim;
	}
	
}
