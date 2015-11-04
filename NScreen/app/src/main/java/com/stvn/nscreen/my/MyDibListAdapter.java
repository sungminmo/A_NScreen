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

import java.util.ArrayList;

public class MyDibListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	private Context mContext;
	private View.OnClickListener mClicklitener;
	ArrayList<String> mList = new ArrayList<String>();

	public View.OnClickListener getmClicklitener() {
		return mClicklitener;
	}

	public void setmClicklitener(View.OnClickListener mClicklitener) {
		this.mClicklitener = mClicklitener;
	}

	public MyDibListAdapter(Context context, ArrayList<String> items)
	{
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mList = items;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public String getItem(int position) {
		return mList.get(position);
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
			convertView = mInflater.inflate(R.layout.row_mycnm_watch, null);
			holder = new ViewHolder();
			holder.rowdate = (TextView)convertView.findViewById(R.id.row_date);
			holder.rowtime = (TextView)convertView.findViewById(R.id.row_time);
			holder.rowname = (TextView)convertView.findViewById(R.id.row_name);
			holder.btn = (Button)convertView.findViewById(R.id.btn1);
			holder.btn.setText("찜해제");
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		((SwipeListView)parent).recycle(convertView, position);
		String item = getItem(position);
		holder.btn.setTag(position);
		holder.btn.setOnClickListener(mClicklitener);

		holder.rowdate.setText("08.28 (금)");
		holder.rowtime.setText("16:15");

		holder.rowname.setText("뉴스파이터");


		return convertView;
	}
	
	class ViewHolder {

		TextView rowdate;
		TextView rowtime;
		TextView rowname;
		Button btn;
	}
	
}
