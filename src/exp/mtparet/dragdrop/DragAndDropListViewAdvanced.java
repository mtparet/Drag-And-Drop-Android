package exp.mtparet.dragdrop;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import exp.mtparet.dragdrop.adapter.ItemAdapter;
import exp.mtparet.dragdrop.adapter.ReceverAdapter;
import exp.mtparet.dragdrop.data.OneItem;
import exp.mtparet.dragdrop.view.HorizontalListView;
import exp.mtparet.dragdrop.view.ListViewDragDrop;

public class DragAndDropListViewAdvanced extends Activity{
	
	private RelativeLayout mainRelativeLayout;
	private ListViewDragDrop lvPicture;
	private ListViewDragDrop lvRecever;
	private ItemAdapter itemAdapter;
	private ReceverAdapter receverAdapter;
	private ImageView imageDrag;
	private OneItem oneItemSelected;
	private Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        context = this;
        
        mainRelativeLayout = (RelativeLayout)RelativeLayout.inflate(context, R.layout.main_list, null);
        lvPicture = (ListViewDragDrop)mainRelativeLayout.findViewById(R.id.listView1);
		lvRecever = (ListViewDragDrop)mainRelativeLayout.findViewById(R.id.listView2);
		imageDrag = (ImageView)mainRelativeLayout.findViewById(R.id.imageView1);
		
		itemAdapter = new ItemAdapter(context, constructList());
		receverAdapter = new ReceverAdapter(context, new ArrayList<OneItem>());
		
		lvPicture.setAdapter(itemAdapter);
		lvRecever.setAdapter(receverAdapter);
		
		lvPicture.setOnItemSelectedListener(mOnItemSelectedListener);
		//lvPicture.setOnItemLongClickListener(listenerRemoveItem2);
		lvPicture.setOnItemMoveListener(mOnItemMoveListener);
		lvPicture.setOnItemUpOutListener(mOnItemUpOutListener);
		lvPicture.setOnItemReceiverListener(listenerReceivePicture2);
		
		lvRecever.setOnItemReceiverListener(listenerReceivePicture);
		//lvRecever.setOnItemLongClickListener(listenerRemoveItem);
		lvRecever.setOnItemUpOutListener(mOnItemUpOutListener2);
		lvRecever.setOnItemMoveListener(mOnItemMoveListener);
		//lvRecever.setOnItemSelectedListener(mOnItemSelectedListener);
		
		setContentView(mainRelativeLayout);
    }
    
    /**
     * Save selected item
     */
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			/**
			 * retrieve selected item from adapterview
			 */
			oneItemSelected = (OneItem) arg0.getItemAtPosition(arg2);
			imageDrag.setImageDrawable(context.getResources().getDrawable(oneItemSelected.getId()));
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

	};
	
	private OnTouchListener mOnItemUpOutListener = new  OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
		
		return lvRecever.onUpReceive(event);
		}
	};
	
	private OnTouchListener mOnItemUpOutListener2 = new  OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
		
		return lvPicture.onUpReceive(event);
		}
	};


	private OnItemClickListener listenerReceivePicture = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(oneItemSelected != null)
				receverAdapter.addPicture(oneItemSelected, arg2);

		}

	};
	
	private OnItemClickListener listenerReceivePicture2 = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(oneItemSelected != null)
				itemAdapter.addPicture(oneItemSelected, arg2);

		}

	};

	private OnItemLongClickListener listenerRemoveItem = new OnItemLongClickListener() {


		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			receverAdapter.removeItem(arg2);
			return false;
		}

	};
	
	private OnItemLongClickListener listenerRemoveItem2 = new OnItemLongClickListener() {


		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			itemAdapter.removeItem(arg2);
			return false;
		}

	};



	private OnTouchListener mOnItemMoveListener = new  OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) imageDrag.getLayoutParams();
			imageDrag.setVisibility(ImageView.VISIBLE);
			
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageDrag.bringToFront();
				return true;
			}
			
			if (event.getAction()==MotionEvent.ACTION_MOVE) {
				layout.leftMargin = (int) event.getX() - imageDrag.getWidth()/2 + v.getLeft();   		
				layout.topMargin = (int) event.getY()- imageDrag.getHeight()/2 + v.getTop();
			}
			
			if (event.getAction()==MotionEvent.ACTION_UP) {
				imageDrag.setVisibility(View.GONE);
			}
			
			imageDrag.setLayoutParams(layout);
			
			return true;
		}

	};
	
	private ArrayList<OneItem> constructList(){
		ArrayList<OneItem> al = new ArrayList<OneItem>();
		
		OneItem op = new OneItem(R.drawable.maison, "maison");
		al.add(op);
		
		OneItem op2 = new OneItem(R.drawable.dans, "dans");
		al.add(op2);
		
		OneItem op3 = new OneItem(R.drawable.dort, "dort");
		al.add(op3);
		
		OneItem op4 = new OneItem(R.drawable.garcon, "gar�on");
		al.add(op4);
		
		OneItem op5 = new OneItem(R.drawable.le, "le");
		al.add(op5);
	
	return al;
	}


}
