package com.tfx.slidingremove;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	List<String> datas = new ArrayList<String>();
	private MyAdapter myAdapter;
	{
		for (int i = 0; i < 100; i++) {
			datas.add("联系人" + i);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView lv = getListView();
		myAdapter = new MyAdapter();
		lv.setAdapter(myAdapter);
	}

	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				//没缓存
				convertView = View.inflate(getApplicationContext(), R.layout.item, null);
				holder = new ViewHolder();
				//找控件
				holder.srv = (SlidingRemoveView) convertView.findViewById(R.id.srv);
				holder.content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.remove = (TextView) convertView.findViewById(R.id.tv_remove);
				convertView.setTag(holder);
			}else{
				//复用缓存
				holder = (ViewHolder) convertView.getTag();
			}
			
			//数据赋值
			final String s = datas.get(position);
			holder.content.setText(s);
			
			final ViewHolder finalHolder = holder;
			
			//removeView的点击事件
			holder.remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finalHolder.srv.dismissRemoveView(); //隐藏removeView
					datas.remove(s); //删除当前数据
					myAdapter.notifyDataSetChanged(); //更新界面
				}
			});
			
			return convertView;
		}
	}
	
	private static class ViewHolder{
		SlidingRemoveView srv;
		TextView content;
		TextView remove;
	}
}
