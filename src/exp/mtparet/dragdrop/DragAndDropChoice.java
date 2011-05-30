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
