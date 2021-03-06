package com.mmmgoodyes.PartyPal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Dashboard extends Activity {

	public String name;
	public String number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		number = intent.getStringExtra("number");
	
		
		SharedPreferences settings = getPreferences(0);
		if (!settings.getBoolean("registered", false)) {
		
		
			new RequestTask().execute("");
			new QueryTask().execute("");
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);		
		
	}
	
	
	
	class RequestTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... uri) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","add_user");
	    		data.put("fname",name);
				data.put("phonenumber", number);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
	    	String json_string = data.toString();
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost post = new HttpPost(getResources().getString(R.string.request_URL));
	        HttpResponse response;
	        String responseString = null;
	        try {
	        	List<NameValuePair> nvpairs = new ArrayList<NameValuePair>(1);
	        	nvpairs.add(new BasicNameValuePair("json",json_string));
	        	
	        	post.setEntity(new UrlEncodedFormEntity(nvpairs));
	        	
	            response = httpclient.execute(post);
	            
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	            	
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                Log.v("http response",out.toString());
	            } else{
	                //Closes the connection.
	            	Log.v("http response failed",response.toString());
	                response.getEntity().getContent().close();
	                
	            }
	        } catch (ClientProtocolException e) {
	            //nope
	        } catch (IOException e) {
	            //nuh uh
	        }
	        return responseString;
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	
	    	SharedPreferences settings = getPreferences(0);
	    	settings.edit().putBoolean("registered", true).commit();
	    	settings.edit().putString("name", name).commit();
	    	settings.edit().putString("number", number).commit();
	    	
	    	
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}
	
	class QueryTask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... uri) {
	    	
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("action","get_group");
				data.put("phonenumber", number);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
	    	String json_string = data.toString();
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost post = new HttpPost(getResources().getString(R.string.request_URL));
	        HttpResponse response;
	        String responseString = null;
	        try {
	        	List<NameValuePair> nvpairs = new ArrayList<NameValuePair>(1);
	        	nvpairs.add(new BasicNameValuePair("json",json_string));
	        	
	        	post.setEntity(new UrlEncodedFormEntity(nvpairs));
	        	
	            response = httpclient.execute(post);
	            
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	            	
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                Log.v("http response",out.toString());
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	            	Log.v("http response failed",response.toString());
	                response.getEntity().getContent().close();
	                
	            }
	        } catch (ClientProtocolException e) {
	            //nope
	        } catch (IOException e) {
	            //nuh uh
	        }
	        return responseString;
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	
	    	SharedPreferences settings = getPreferences(0);
	    	settings.edit().putString("groups", result).commit();
	    	int length;
	    	try {
				JSONArray json = new JSONArray(result);
				Log.v("json",json.length()+"");
				length = json.length();
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
	    	
	    	jump_to_viewer();
	    	
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}
	
	public void jump_to_viewer() {
		Intent intent = new Intent(this,Group_Viewer.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}
	
	public void join_group(View view) {
		//setContentView(R.layout.main);

		Button mButton=(Button)findViewById(R.id.Join_Button);
		mButton.setBackgroundColor(Color.parseColor("#F7D01A")); // custom color
		
		String groupname = ((EditText)findViewById(R.id.Group_Name)).getText().toString();
		
		Intent intent = new Intent(this,Group_Viewer.class);
		
		intent.putExtra("GroupName", groupname);
		intent.putExtra("Name", name);
		intent.putExtra("Number", number);

		startActivity(intent);
	}
	
	public void create_group(View view) {
		//setContentView(R.layout.main);

		Button mButton=(Button)findViewById(R.id.Start_Button);
		mButton.setBackgroundColor(Color.parseColor("#F7D01A")); // custom color
		
		String groupname = ((EditText)findViewById(R.id.Group_Name)).getText().toString();
		
		Intent intent = new Intent(this,Group_Viewer.class);
		
		intent.putExtra("GroupName", groupname);
		intent.putExtra("Name", name);
		intent.putExtra("Number", number);
		startActivity(intent);
	}
		

}
