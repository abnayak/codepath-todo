package com.codeconnect.todo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;

public class TodoActivity extends Activity {
	ArrayList<String> items;
	ArrayAdapter<String> itemsAdaptor;
	ListView lvItems;
	private final int REQUEST_CODE = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		
		//get the list view and add the adaptop
		lvItems = (ListView) findViewById(R.id.lvItems);
		items = new ArrayList<String>();
		
		readItems();
		//configure the adaptor here
		itemsAdaptor = new ArrayAdapter<String>(this, 
					android.R.layout.simple_list_item_1,
					items
				);
		lvItems.setAdapter(itemsAdaptor);

		setupListViewListener();
	}

	private void setupListViewListener(){
		//lvItems.setLongClickable(true);
		lvItems.setOnItemLongClickListener( new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick ( AdapterView<?> parent, 
							View view, int position, long rowId){
				items.remove(position);
				itemsAdaptor.notifyDataSetChanged();
				writeItems();
				return true;
			}
		});
		
		lvItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowID){
				//open the edit window
				Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
				i.putExtra("position", position);
				i.putExtra("action", items.get(position));
				//startActivity(i);
				startActivityForResult(i, REQUEST_CODE);
			}
		});
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if ( requestCode == REQUEST_CODE && resultCode == RESULT_OK){
			String action = data.getExtras().getString("action");
			int position = data.getExtras().getInt("position", 0);
			//Toast.makeText(this, action + position, Toast.LENGTH_SHORT).show();
			items.set(position, action);
			itemsAdaptor.notifyDataSetChanged();
			writeItems();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addTodoItem(View v){
		EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
		itemsAdaptor.add(etNewItem.getText().toString());
		etNewItem.setText("");
		writeItems();
	}
	
	private void readItems(){
		File filesDir = getFilesDir();
		File todoFile = new File(filesDir, "todo.txt");
		try{
			items = new ArrayList<String>(FileUtils.readLines(todoFile));
		}catch(IOException e){
			items = new ArrayList<String>();
			e.printStackTrace();
		}
	}
	private void writeItems(){
		File filesDir = getFilesDir();
		File todoFile = new File (filesDir, "todo.txt");
		try {
			FileUtils.writeLines(todoFile, items);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
