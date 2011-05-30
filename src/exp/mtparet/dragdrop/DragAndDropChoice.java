package exp.mtparet.dragdrop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DragAndDropChoice extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setContentView(R.layout.main);
        super.onCreate(savedInstanceState);
        
        
    }
    
    public void myHandler(View id){
    	switch(id.getId()){
    	case R.id.button1: 
    		Intent intent = new Intent(DragAndDropChoice.this, DragAndDropListView.class);
            
            startActivity(intent);
            
    		break;
    	case R.id.button2:
    		Intent intent2 = new Intent(DragAndDropChoice.this, DragAndDropHorizontal.class);
            
            startActivity(intent2);
            
    		break;
    	}
    }
}
