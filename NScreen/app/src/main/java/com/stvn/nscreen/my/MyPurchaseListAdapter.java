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

import java.util.Date;

public class MyPurchaseListAdapter extends ArrayAdapter<ListViewDataObject> {

	LayoutInflater mInflater;
	private Context mContext;
	private Date mCompareDate;

	public MyPurchaseListAdapter(Context context) {
		super(context, 0);
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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

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

			String viewablePeriodState = jsonObj.getString("viewablePeriodState");

			String remainLicense = "";
			if ("1".equals(viewablePeriodState)) {
				remainLicense = "무제한 시청";
			} else {
				String licenseEnd = jsonObj.getString("licenseEnd");
				remainLicense = CMDateUtil.getLicenseRemainDate(licenseEnd, this.mCompareDate);
				if (info.remainTime < 0) {
					remainLicense = "기간 만료";
				} else {

					long remains = 0;
					long diffDay = info.remainTime / (24 * 60 * 60); // 일자
					remains = info.remainTime % (24 * 60 * 60); // 일자

					long diffTime = remains / (60 * 60); // 시간
					remains = remains % (60 * 60); // 시간

					long diffMinute = remains / (60); // 분
					remains = remains % (60); // 분

					long diffSecond = remains;


					if (diffDay > 0) {
						remainLicense = diffDay +"일 남음";
					} else if (diffTime > 0) {
						remainLicense = diffTime +"시간 남음";
					} else if (diffTime == 0 && (diffMinute > 0 || diffSecond > 0)) {
						remainLicense = "1시간 남음";
					}
				}
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

		return convertView;
	}
	
	class ViewHolder {
		TextView rowdate;
		TextView rowtime;
		ImageView coupontype;
		TextView rowname;
		TextView rowpay;
		TextView rowterm;
	}
	
}
