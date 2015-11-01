package com.stvn.nscreen.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.stvn.nscreen.R;

import java.util.List;

public class MyDibListAdapter extends ArrayAdapter<String> {

	LayoutInflater mInflater;
	private Context mContext;
	private View.OnClickListener mClicklitener;

	public View.OnClickListener getmClicklitener() {
		return mClicklitener;
	}

	public void setmClicklitener(View.OnClickListener mClicklitener) {
		this.mClicklitener = mClicklitener;
	}

	public MyDibListAdapter(Context context, List<String> items)
	{
		super(context, 0, items);
		// TODO Auto-generated constructor stub
		mContext = context;
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
			convertView = mInflater.inflate(R.layout.row_mycnm_watch, null);
			holder = new ViewHolder();
			holder.rowdate = (TextView)convertView.findViewById(R.id.row_date);
			holder.rowtime = (TextView)convertView.findViewById(R.id.row_time);
			holder.rowname = (TextView)convertView.findViewById(R.id.row_name);
			holder.btn = (Button)convertView.findViewById(R.id.btn1);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
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
