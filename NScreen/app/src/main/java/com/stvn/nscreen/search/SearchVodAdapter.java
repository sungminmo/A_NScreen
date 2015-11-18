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
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SearchVodDataObject;

import java.util.List;

public class SearchVodAdapter extends ArrayAdapter<SearchVodDataObject> {

	LayoutInflater mInflater;
	private Context mContext;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;


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
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		SearchVodDataObject item = getItem(position);
		holder.poster.setImageUrl(item.smallImageFileName, mImageLoader);

		holder.event.setVisibility(View.VISIBLE);
		int imgresource = R.mipmap.vod_01;
		if ("0".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_01;
		} else if ("11".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_09;
		} else if ("12".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_07;
		} else if ("13".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_03;
		} else if ("14".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_08;
		} else if ("15".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_04;
		} else if ("16".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_02;
		} else if ("17".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_05;
		} else if ("18".equals(item.promotionSticker)) {
			imgresource = R.mipmap.vod_06;
		} else {
			holder.event.setVisibility(View.INVISIBLE);
		}
		holder.event.setImageResource(imgresource);

		holder.programname.setText(item.title);

		return convertView;
	}
	
	class ViewHolder {
		TextView programname;
		NetworkImageView poster;
		ImageView event;
	}
	
}
