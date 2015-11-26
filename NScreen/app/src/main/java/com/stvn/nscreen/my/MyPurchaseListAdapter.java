package com.stvn.nscreen.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjiya.android.common.CMDateUtil;
import com.jjiya.android.common.ListViewDataObject;
import com.jjiya.android.common.UiUtil;
import com.stvn.nscreen.R;
import com.stvn.nscreen.common.SwipeListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MyPurchaseListAdapter extends ArrayAdapter<ListViewDataObject> {

	LayoutInflater mInflater;
	private Context mContext;
	private Date mCompareDate;
	private View.OnClickListener mClicklitener;
	public View.OnClickListener getmClicklitener() {
		return mClicklitener;
	}

	public void setmClicklitener(View.OnClickListener mClicklitener) {
		this.mClicklitener = mClicklitener;
	}

	public MyPurchaseListAdapter(Context context, ArrayList<ListViewDataObject> items) {
		super(context, 0, items);
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mCompareDate = new Date();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
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
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		((SwipeListView)parent).recycle(convertView, position);

		ListViewDataObject info = getItem(position);

		try {

			JSONObject jsonObj = new JSONObject(info.sJson);
			String productName = jsonObj.getString("productName");
			holder.rowname.setText(productName);

			String price = jsonObj.getString("price");
			holder.rowpay.setText(UiUtil.stringParserCommafy(price) + "원");

			String purchasedTime = jsonObj.getString("purchasedTime");
			String pDay = CMDateUtil.findDateWithFormat(purchasedTime, "MM.dd") +"("+ CMDateUtil.getDayOfWeek(purchasedTime)+")";
			holder.rowdate.setText(pDay);

			String pTime = CMDateUtil.findDateWithFormat(purchasedTime, "HH:mm");
			holder.rowtime.setText(pTime);

			String licenseEnd = jsonObj.getString("licenseEnd");
			String remainLicense = CMDateUtil.getLicenseRemainDate(licenseEnd, this.mCompareDate);

			//TODO:평생소장 확인 데이터가 없어 임시로 타이틀에서 확인해 비교함
			if (productName.indexOf("(평생소장)") > -1) {
				remainLicense = "무제한 시청";
			}
			holder.rowterm.setText(remainLicense);


			String paymentType = jsonObj.getString("paymentType");
			if ("normal".equals(paymentType)) {
				holder.coupontype.setImageResource(R.mipmap.icon_nor); // 일반결제
			} else if ("coupon".equals(paymentType)) {
				holder.coupontype.setImageResource(R.mipmap.icon_coupon); // 쿠폰결제
			} else if ("point".equals(paymentType)) {
				holder.coupontype.setImageResource(R.mipmap.icon_point); // TV포인트결제
			} else if ("complex".equals(paymentType)) {
				holder.coupontype.setImageResource(R.mipmap.icon_complex); // 복합결제
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.btn.setTag(position);
		holder.btn.setOnClickListener(mClicklitener);
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
