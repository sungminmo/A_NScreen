package com.stvn.nscreen.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SwipeListView;

import java.util.ArrayList;

public class MyPurchaseListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	private Context mContext;
	private View.OnClickListener mClicklitener;
	private ArrayList<String> mList;

	public View.OnClickListener getmClicklitener() {
		return mClicklitener;
	}

	public void setmClicklitener(View.OnClickListener mClicklitener) {
		this.mClicklitener = mClicklitener;
	}

	public MyPurchaseListAdapter(Context context, ArrayList<String> items)
	{
		// TODO Auto-generated constructor stub
		mList = items;
		mContext = context;
		mInflater = LayoutInflater.from(context);
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
			convertView = mInflater.inflate(R.layout.row_mycnm_purchase, null);
			holder = new ViewHolder();
			holder.rowdate = (TextView)convertView.findViewById(R.id.row_date);
			holder.rowtime = (TextView)convertView.findViewById(R.id.row_time);
			holder.coupontype = (ImageView)convertView.findViewById(R.id.row_coupontype);
			holder.rowname = (TextView)convertView.findViewById(R.id.row_name);
			holder.rowpay = (TextView)convertView.findViewById(R.id.row_pay);
			holder.rowterm = (TextView)convertView.findViewById(R.id.row_term);
			holder.btn = (Button)convertView.findViewById(R.id.btn1);

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
		holder.rowpay.setText("5000원");
		holder.rowterm.setText("2일남음");


		return convertView;
	}
	
	class ViewHolder {

		TextView rowdate;
		TextView rowtime;
		ImageView coupontype;
		TextView rowname;
		TextView rowpay;
		TextView rowterm;
		Button btn;
	}
	
}
