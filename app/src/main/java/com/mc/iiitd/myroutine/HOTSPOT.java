package com.mc.iiitd.myroutine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;


public class HOTSPOT extends AppCompatActivity {
		public static final String KEY_ROWID="_id";
		public static final String KEY_DATE="date";
		public static final String KEY_NAME="name";
		public static final String KEY_STARTTIME="starter";
		public static final String KEY_FINISHTIME="finisher";
		public static final String KEY_LOCATION="location";
				
		private static final String DATABASE_NAME="dateitdb";
		private static final String DATABASE_TABLE="dater";
		
		private static final int DATABASE_VERSION=1;
		
		private DbHelper ourHelper;
		private final Context ourContext;
		private SQLiteDatabase ourDatabase;
		private static class DbHelper extends SQLiteOpenHelper
		{

			public DbHelper(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
				// TODO Auto-generated constructor stub
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				db.execSQL("CREATE TABLE "+ DATABASE_TABLE+" ("+
						KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
						KEY_DATE+" INTEGER, "+
						KEY_NAME+" TEXT, "+
						KEY_LOCATION+" TEXT, "+
						KEY_STARTTIME+" TEXT, "+
						KEY_FINISHTIME+" TEXT);"
						);

			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				// TODO Auto-generated method stub
				db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE);
				onCreate(db);
			}
			
		} 
		public HOTSPOT(Context c){
			ourContext=c;
		}
		public HOTSPOT open() throws SQLException 
		{
			ourHelper=new DbHelper(ourContext);
			ourDatabase=ourHelper.getWritableDatabase();
			 return this;
			 
		}
		public void close()
		{
			ourHelper.close();
			
		}

		public long createEntry(int date,String name,String starttime,String finishtime,String location )
		{
			ContentValues cv=new ContentValues();
			cv.put(KEY_DATE, date);
			cv.put(KEY_NAME, name);
			cv.put(KEY_LOCATION,location);
			cv.put(KEY_STARTTIME, starttime);
			cv.put(KEY_FINISHTIME, finishtime);


			return(ourDatabase.insert(DATABASE_TABLE, null, cv));
			  	
		}
		public String reader(int currdate,String name)
		{


			String[] columns=new String[]{KEY_DATE,KEY_NAME,KEY_LOCATION,KEY_STARTTIME,KEY_FINISHTIME};
			Cursor c=ourDatabase.query(DATABASE_TABLE, columns, KEY_DATE +"="+currdate, null, null, null, null);
			int date=c.getColumnIndex(KEY_DATE);
			int nameer=c.getColumnIndex(KEY_NAME);
			int startt=c.getColumnIndex(KEY_STARTTIME);
			int finisht=c.getColumnIndex(KEY_FINISHTIME);
			int location=c.getColumnIndex(KEY_LOCATION);
			String result="";
			//int locations=0;
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
			{
				result=result+"DATE:"+c.getString(date)+"name:"+c.getString(nameer)+" starttime:"+ c.getString(startt)+" finisht:"+c.getString(finisht)+" locationt:"+c.getString(location);
				//sumofsteps=Integer.parseInt(c.getString(count))+sumofsteps;
			
			}
				//return (result+" sumofSteps:"+Integer.toString(sumofsteps));
			return(result);
		}

				

	}
