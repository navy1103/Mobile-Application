package com.appsrox.remindme.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.appsrox.remindme.R;

public class NavAdapter extends ArrayAdapter<String> {
	
	private LayoutInflater mInflater;
	private int layoutResourceId;
	private TypedArray icons;

	public NavAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		
		layoutResourceId = resource;
		mInflater = ((Activity) context).getLayoutInflater();
		icons = context.getResources().obtainTypedArray(R.array.icon_arr);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView iv;
		if (convertView == null) {
			 iv = (ImageView) mInflater.inflate(layoutResourceId, parent, false);
		} else {
			iv = (ImageView) convertView;
		}
		iv.setImageResource(icons.getResourceId(position, -1));
		
		return iv;
	}

}

