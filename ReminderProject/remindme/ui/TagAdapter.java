package com.appsrox.remindme.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appsrox.remindme.R;


public class TagAdapter extends ArrayAdapter<String> {
	
	private TypedArray icons;

	public TagAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		icons = context.getResources().obtainTypedArray(R.array.icon_arr);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = (TextView) super.getView(position, convertView, parent);
//		tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag, 0, 0, 0);
		if (position == 0) {
			tv.setText("");
		}
		return tv;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
		if (position == 0) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			tv.setText("None");
		} else {
			tv.setCompoundDrawablesWithIntrinsicBounds(icons.getResourceId(position, -1), 0, 0, 0);
			tv.setCompoundDrawablePadding(5);	
		}
		return tv;
	}
	
	
}
