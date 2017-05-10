package com.tfx.slidingremove;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class SlidingRemoveView extends ViewGroup {

	private View contentView;
	private View removeView;
	private int removeView_width;
	private ViewDragHelper mViewDragHelper;

	public SlidingRemoveView(Context context) {
		this(context, null);
	}
	
	public SlidingRemoveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//创建拖拽帮助类实例
		mViewDragHelper = ViewDragHelper.create(this, new MyCallback());
	}

	//滑动事件的处理
	private class MyCallback extends Callback{
		//对子控件进行分析   那些view可以进行滑动操作
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			//给需要滑动的view 返回true
			//当contentView在滑动时  child==contentview == true 
			return child == contentView || child == removeView;
		}
		
		//决定拖拽的view在水平上面移动到的位置  越界处理
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//left:子控件左上点的横坐标
			//判断拖动的控件是哪个
			if(child == contentView){
				//越界判断
				if(left > 0){
					return 0;
				}else if(left < -removeView.getMeasuredWidth()){
					return -removeView.getMeasuredWidth();
				}
			}else if(child == removeView){
				//越界判断
				if(left > contentView.getMeasuredWidth()){
					return contentView.getMeasuredWidth();
				}else if(left < contentView.getMeasuredWidth() - removeView.getMeasuredWidth()){
					return contentView.getMeasuredWidth() - removeView.getMeasuredWidth();
				}
			}
			
			return left;//左上点的x坐标
		}
		
		//当view的位置改变时回调
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			//判断当前位置改变的view
			if(changedView == contentView){
				//让removeView跟着改变
				int removeLeft = left + contentView.getMeasuredWidth();
				int removeTop = 0;
				int removeRight = removeLeft + removeView.getMeasuredWidth();
				int removeBottom = removeView.getMeasuredHeight();
				removeView.layout(removeLeft,removeTop, removeRight, removeBottom);
			}else if(changedView == removeView){
				//让contentView跟着改变
				int contentLeft = left - contentView.getMeasuredWidth();
				int contentTop = 0;
				int contentRight = left;
				int contentBottom = contentView.getMeasuredHeight();
				contentView.layout(contentLeft, contentTop, contentRight, contentBottom);
			}
		}
		
		//事件松开 手抬起时
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			//判断removeView是否超过了一半
			int left = contentView.getLeft();
			if(-left >= removeView.getMeasuredWidth() / 2){
				//显示removeView
				showRemoveView();
			}
			else if(-left < removeView.getMeasuredWidth() / 2){
				//隐藏removeVIew  
				dismissRemoveView();
			}
		}
	}
	
	public void dismissRemoveView(){
		mViewDragHelper.smoothSlideViewTo(contentView, 0, 0);
		mViewDragHelper.smoothSlideViewTo(removeView,contentView.getMeasuredWidth(), 0);
		invalidate(); //刷新界面
		/*
		//没超过一半 隐藏removeView
		int removeLeft = contentView.getMeasuredWidth();
		int removeTop = 0;
		int removeRight = removeLeft + removeView.getMeasuredWidth();
		int removeBottom = removeView.getMeasuredHeight();
		removeView.layout(removeLeft,removeTop, removeRight, removeBottom);
		
		int contentLeft = 0;
		int contentTop = 0;
		int contentRight = contentView.getMeasuredWidth();
		int contentBottom = contentView.getMeasuredHeight();
		contentView.layout(contentLeft, contentTop, contentRight, contentBottom);
		*/
	}
	
	public void showRemoveView(){
		//smoothSlideViewTo 光滑的滑动的效果
		mViewDragHelper.smoothSlideViewTo(contentView, -removeView.getMeasuredWidth(), 0);
		mViewDragHelper.smoothSlideViewTo(removeView, contentView.getMeasuredWidth() - removeView.getMeasuredWidth(), 0);
		invalidate(); //刷新界面
		/*
		//超过了一半  显示removeView
		int removeLeft = contentView.getMeasuredWidth() - removeView.getMeasuredWidth();
		int removeTop = 0;
		int removeRight = contentView.getMeasuredWidth();
		int removeBottom = removeView.getMeasuredHeight();
		removeView.layout(removeLeft,removeTop, removeRight, removeBottom);
		
		int contentLeft = 0 - removeView.getMeasuredWidth();
		int contentTop = 0;
		int contentRight = contentView.getMeasuredWidth() - removeView.getMeasuredWidth();
		int contentBottom = contentView.getMeasuredHeight();
		contentView.layout(contentLeft, contentTop, contentRight, contentBottom);
		*/
	}
	
	//使用smoothSlideViewTo 必须实现这个方法  计算
	@Override
	public void computeScroll() {
		if(mViewDragHelper.continueSettling(true)){
			invalidate();
		}
	}
	
	//事件绑定给mViewDragHelper
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//将事件给mViewDragHelper处理
		mViewDragHelper.processTouchEvent(event);
		return true; //自己消费
	}

	//布局解析完成
	@Override
	protected void onFinishInflate() {
		//获得子孩子
		contentView = getChildAt(0);
		removeView = getChildAt(1);
		
		//删除view布局宽度
		removeView_width = removeView.getLayoutParams().width;
	}
	
	//测量
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//测量内容view  宽高填充父窗体
		contentView.measure(widthMeasureSpec, heightMeasureSpec);
		
		//测量删除view  高填充父窗体  宽精确测量
		int removeViewWidthmakeMeasureSpec = MeasureSpec.makeMeasureSpec(removeView_width,MeasureSpec.EXACTLY);
		removeView.measure(removeViewWidthmakeMeasureSpec, heightMeasureSpec);
		
		//测量组件大小 
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}
	
	//布局孩子
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int dx = 0;
		
		//布局内容view
		int contentLeft = 0 - dx;
		int contentTop = 0;
		int contentRight = contentView.getMeasuredWidth() - dx;
		int contentBottom = contentView.getMeasuredHeight();
		contentView.layout(contentLeft, contentTop, contentRight, contentBottom);
		
		//布局删除view
		int removeLeft = contentView.getMeasuredWidth() - dx;
		int removeTop = 0;
		int removeRight = contentView.getMeasuredWidth() + removeView.getMeasuredWidth() - dx;
		int removeBottom = removeView.getMeasuredHeight();
		removeView.layout(removeLeft,removeTop, removeRight, removeBottom);
	}

}
