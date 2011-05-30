/*
*Copyright 2011 Matthieu Paret
*
*This file is part of DragAndDrop.
*
*DragAndDrop is free software: you can redistribute it and/or modify
*it under the terms of the GNU Lesser General Public License as published by
*the Free Software Foundation, either version 3 of the License, or
*(at your option) any later version.
*
*DragAndDrop is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU Lesser General Public License
*along with DragAndDrop.  If not, see <http://www.gnu.org/licenses/>.
*/

package exp.mtparet.dragdrop.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Extends ListView, implement some additionnal listener
 * @author Matthieu Paret
 *
 */
public class ListViewDragDrop extends ListView{
	
	private OnTouchListener mOnItemOutUpListener;
	private OnTouchListener mOnItemMoveListener; //It is an hacked touchLister, in fact it is OnMoveListener
	private OnItemClickListener mOnItemReceiver;
	private boolean isMove = false;
	private View childSelected;
	private float xInit;
	private float yInit;


	public ListViewDragDrop(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ListViewDragDrop(Context context, AttributeSet attrs) {
       super(context, attrs);
  }

   public ListViewDragDrop(Context context, AttributeSet attrs, int defStyle) {
         super(context, attrs, defStyle);
   }
   
   /**
    * When an item is moved horizontally out of this position, this listener is called. Before this OnItemSelectedListener is called.
    * @param listener
    */
	public void setOnItemMoveListener(AdapterView.OnTouchListener listener){
		mOnItemMoveListener = listener;
	}

	/**
	 * When a gesture on a item is terminated out of the the listView
	 * @param listener
	 */
	public void setOnItemUpOutListener(AdapterView.OnTouchListener listener){
		this.mOnItemOutUpListener = listener;
	}

	/**
	 * When an outsider item is moved and up on this listview. Return position where to add this item.
	 * @param listener
	 */
	public void setOnItemReceiverListener(AdapterView.OnItemClickListener listener){
		this.mOnItemReceiver = listener;
	}
	

	
   @Override
   public boolean onTouchEvent(MotionEvent ev) {
	   boolean handled = false;

		if(mOnItemMoveListener != null && !handled)
			handled = onMove(ev);
		
		if(!handled)
       return super.onTouchEvent(ev);
		
	return handled;
   }

   /**
    * 
    * @param e
    * @return
    */
   public boolean onUpReceive(MotionEvent e) {

		if(e.getAction() == MotionEvent.ACTION_UP){
			
			int x = (int)e.getX();
			int y = (int)e.getY();

			for(int i=0;i<getChildCount();i++){
				Rect viewRect = new Rect();
				View child = getChildAt(i);
				int left = child.getLeft() + this.getLeft();
				int right = child.getRight() + this.getLeft();
				int top = child.getTop() + this.getTop();
				int bottom = child.getTop() + child.getHeight()/2 + this.getTop();
				viewRect.set(left, top, right, bottom);
				
				if(viewRect.contains(x,y)){
					if(getOnItemSelectedListener() != null){
						getOnItemSelectedListener().onItemSelected(ListViewDragDrop.this, child, i, getItemIdAtPosition(i));
					}
					if(mOnItemReceiver != null){
						mOnItemReceiver.onItemClick(ListViewDragDrop.this, child, i, getItemIdAtPosition(i));
					}
					return true;
				}
				
				Rect viewRect2 = new Rect();
				left = child.getLeft() + this.getLeft();
				right = child.getRight() + this.getLeft();
				top = child.getTop()  + child.getHeight()/2 + this.getTop();
				bottom = child.getBottom() + this.getTop();
				viewRect2.set(left, top, right, bottom);
				
				if(viewRect2.contains(x,y)){
					if(getOnItemSelectedListener() != null){
						getOnItemSelectedListener().onItemSelected(ListViewDragDrop.this, child, i, getItemIdAtPosition(i));
					}
					if(mOnItemReceiver != null){
						mOnItemReceiver.onItemClick(ListViewDragDrop.this, child, i + 1, getItemIdAtPosition(i));
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
					int  minY = this.getChildAt(this.getChildCount() - 1).getBottom();
					int maxY = this.getChildAt(0).getTop();

					if(y < minY){
						mOnItemReceiver.onItemClick(ListViewDragDrop.this, null, 0, 0);
					}else{
						if(y > maxY){
							mOnItemReceiver.onItemClick(ListViewDragDrop.this, null, this.getChildCount() , 0);
						}

					}

					return true;

				}else{

					if(mOnItemReceiver != null){
						mOnItemReceiver.onItemClick(ListViewDragDrop.this, null, 0, 0);
					}
					return true;
				}

			}


		}
		return false;
	}

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

					if(getOnItemSelectedListener() != null){
						getOnItemSelectedListener().onItemSelected(ListViewDragDrop.this, child, i, getItemIdAtPosition(i));
					}
					
					this.xInit = xInit1;
					this.yInit = yInit1;
					
					childSelected = child;


				}
			}
		}

		if(event.getAction() == MotionEvent.ACTION_MOVE){

			if(!isMove){

				if( Math.abs(xNow -xInit) > 0 && Math.abs(yNow - yInit) < 4){

					if(mOnItemMoveListener != null){
						mOnItemMoveListener.onTouch(ListViewDragDrop.this, event);
					}

					isMove = true;
					return true;

				}

			}else{
				mOnItemMoveListener.onTouch(ListViewDragDrop.this, event);

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
				mOnItemMoveListener.onTouch(ListViewDragDrop.this, event);
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


}
