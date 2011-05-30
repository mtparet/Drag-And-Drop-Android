/*
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * Copyright (c) 2011 Matthieu Paret (matthieuparet69@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package exp.mtparet.dragdrop.view;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * It is an custom Horizontal ListView, it is less optimize than ListView, all function are not implemented
 * @author Matthieu Paret
 *
 */
public class HorizontalListView extends AdapterView<ListAdapter>{

	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnTouchListener mOnItemOutUpListener;
	private OnItemLongClickListener mOnItemLongClickListener;
	private OnTouchListener mOnItemMoveListener; //It is an hacked touchLister, in fact it is OnMoveListener
	private OnItemClickListener mOnItemReceiver;
	private boolean mDataChanged = false;
	private boolean isMove = false;
	private View childSelected;
	private float xInit;
	private float yInit;



	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	}

	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
		mOnItemClicked = listener;
	}

	@Override
	public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener){
		mOnItemLongClickListener = listener;
	}

	public void setOnItemMoveListener(AdapterView.OnTouchListener listener){
		mOnItemMoveListener = listener;
	}

	public void setOnItemUpOutListener(AdapterView.OnTouchListener listener){
		this.mOnItemOutUpListener = listener;
	}

	public void setOnItemReceiverListener(AdapterView.OnItemClickListener listener){
		this.mOnItemReceiver = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized(HorizontalListView.this){
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}

	};


	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		//TODO: implement
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if(mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	private synchronized void reset(){
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
		//TODO: implement
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if(mAdapter == null){
			return;
		}

		if(mDataChanged){
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if(mScroller.computeScrollOffset()){
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}

		if(mNextX < 0){
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if(mNextX > mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		mCurrentX = mNextX;

		if(!mScroller.isFinished()){
			post(new Runnable(){
				@Override
				public void run() {
					requestLayout();
				}
			});

		}
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount()-1);
		if(child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if(child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);


	}

	private void fillListRight(int rightEdge, final int dx) {
		while(rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {

			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();

			if(mRightViewIndex == mAdapter.getCount()-1){
				mMaxX = mCurrentX + rightEdge - getWidth();
			}
			mRightViewIndex++;
		}

	}

	private void fillListLeft(int leftEdge, final int dx) {
		while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while(child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);

		}

		child = getChildAt(getChildCount()-1);
		while(child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount()-1);
		}
	}

	private void positionItems(final int dx) {
		if(getChildCount() > 0){
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
				left += childWidth;
			}
		}
	}

	public synchronized void scrollTo(int x) {
		mScroller.startScroll(mNextX, 0, x - mNextX, 0);
		requestLayout();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev){
		boolean handled = false;


		if(mOnItemReceiver != null)
			handled = onUpReceive(ev);

		if(mOnItemMoveListener != null && !handled)
			handled = onMove(ev);

		if(!handled)
			handled = mGesture.onTouchEvent(ev);

		return handled;

	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		
		synchronized(HorizontalListView.this){
			mScroller.fling(mNextX, 0, (int)-velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();

		return true;
	}

	protected boolean onDown(MotionEvent e) {
		isMove = false;
		mScroller.forceFinished(true);
		return true;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			synchronized(HorizontalListView.this){
				mNextX += (int)distanceX;
			}

			requestLayout();

			return false;
		}

		@Override
		public void onLongPress(MotionEvent e){
			Rect viewRect = new Rect();
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if(viewRect.contains((int)e.getX(), (int)e.getY())){

					if(mOnItemLongClickListener != null){
						mOnItemLongClickListener.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					break;
				}
			}

		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Rect viewRect = new Rect();
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if(viewRect.contains((int)e.getX(), (int)e.getY())){
					if(mOnItemClicked != null){
						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					break;
				}

			}
			return true;
		}



	};
	
	public boolean onMove(MotionEvent event) {



		float xNow = event.getX();
		float yNow = event.getY();

		Rect viewRect = new Rect();

		if(event.getAction() == MotionEvent.ACTION_DOWN){
			float xInit1 = event.getX();
			float yInit1 = event.getY();
			
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if(viewRect.contains((int)xInit1, (int)yInit1)){

					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					
					this.xInit = xInit1;
					this.yInit = yInit1;
					
					childSelected = child;

				}
			}
		}

		if(event.getAction() == MotionEvent.ACTION_MOVE){

			if(!isMove){

				if( Math.abs(yNow -yInit) > 0 && Math.abs(xNow - xInit) < 2){



					if(mOnItemMoveListener != null){
						mOnItemMoveListener.onTouch(childSelected, event);
					}

					isMove = true;
					return true;

				}

			}else{
				Log.d("relache", "pass� in 2");
				mOnItemMoveListener.onTouch(childSelected, event);

				return true;
			}
		}

		if(event.getAction() == MotionEvent.ACTION_UP && isMove){
			int left = this.getLeft();
			int right = this.getRight();
			int top = this.getTop();
			int bottom = this.getBottom();
			Rect rect = new Rect(left, top, right, bottom);
			
			if(mOnItemMoveListener != null){
				mOnItemMoveListener.onTouch(childSelected, event);
			}

			if(!rect.contains((int)xNow, (int)yNow)){
				
				if(mOnItemOutUpListener != null){
					mOnItemOutUpListener.onTouch(this.childSelected, event);
				}

			}

			isMove = false;
			return false;
		}

		return false;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean onUpReceive(MotionEvent e) {

		if(e.getAction() == MotionEvent.ACTION_UP){
			Rect viewRect = new Rect();
			int x = (int)e.getX();
			int y = (int)e.getY();

			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop() + this.getTop();
				int bottom = child.getBottom() + this.getBottom();
				viewRect.set(left, top, right, bottom);
				if(viewRect.contains(x,y)){
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemReceiver != null){
						mOnItemReceiver.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					return true;
				}

			}

			int left = this.getLeft();
			int right = this.getRight();
			int top = this.getTop();
			int bottom = this.getBottom();
			Rect rect = new Rect(left, top, right, bottom);

			if(rect.contains(x,y)){

				if(this.getChildCount() > 0){
					int maxX = this.getChildAt(this.getChildCount() - 1).getRight();
					int minX = this.getChildAt(0).getRight();

					if(x < minX){
						mOnItemReceiver.onItemClick(HorizontalListView.this, null, 0, 0);
					}else{
						if(x > maxX){
							mOnItemReceiver.onItemClick(HorizontalListView.this, null, this.getChildCount() - 1, 0);
						}

					}

					return true;

				}else{

					if(mOnItemReceiver != null){
						mOnItemReceiver.onItemClick(HorizontalListView.this, null, 0, 0);
					}
					return true;
				}

			}


		}
		return false;
	}
}