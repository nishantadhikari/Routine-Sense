package com.mc.iiitd.myroutine;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.json.simple.*;
//import org.json.simple.JSONObject;

public class FindPeopleFragment extends Fragment implements View.OnClickListener {
    public static String USER_ID;
    public FindPeopleFragment() {
    }
    TextView textView;
    public TextView tv;
    MenuItem ref;
    private Button but;
    View rootView;
    ListView lv2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_find_people, container, false);
        tv=(TextView)rootView.findViewById(R.id.textView);
        if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("USERID","").isEmpty()){
            USER_ID=null;
            tv.setText("Can't find user info");
            return rootView;
        }
        else{
            USER_ID=PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("USERID","");
        }
        but=(Button)rootView.findViewById(R.id.buttonfr);
        but.setOnClickListener(this);
        lv2=(ListView)rootView.findViewById(R.id.list_view2);
        tv.setVisibility(View.INVISIBLE);
        DatabaseHandler db=new DatabaseHandler(getActivity().getApplicationContext());
        lv2.setAdapter(new ListAdapterfrnds(getActivity().getApplicationContext(),db.getAllstored()));
        /*
        loadClist lc=new loadClist(getActivity().getApplicationContext(),getActivity(),rootView);
        tv.setAnimation(getBlinkAnimation());
        try {
            lc.execute();
        }
        catch(Exception e){

        }
        */
        setHasOptionsMenu(true);
        return rootView;
    }
    public Animation getBlinkAnimation(){
        Animation animation = new AlphaAnimation(1, 0);         // Change alpha from fully visible to invisible
        animation.setDuration(600);                             // duration - half a second
        animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE);                            // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);             // Reverse animation at the end so the button will fade back in
        return animation;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        //menu.clear();
        Toast.makeText(getActivity().getApplicationContext(), "called", Toast.LENGTH_LONG);
        ref=menu.add("Refresh");
        ref.setIcon(R.drawable.arrowleft);
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_fragment_find_people, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.refresh_filter:
                Toast.makeText(getActivity().getApplicationContext(),"Loading",Toast.LENGTH_LONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buttonfr:
                tv.setVisibility(View.VISIBLE);
                tv.setAnimation(getBlinkAnimation());
                tv.setText("Loading...");
                loadClist lc=new loadClist(getActivity().getApplicationContext(),getActivity(),rootView);

                try {
                    lc.execute();
                }
                catch(Exception e){

                }
                lv2.setVisibility(View.VISIBLE);
                break;
        }

    }

}


class loadClist extends AsyncTask<Void, Integer, Void> {
    Context context;
    Activity activity;
    View rview;
    ListAdapterfrnds forfrnds;
    TextView tv;
    public static String URL ="http://muc.iiitd.edu.in:9060/";
    public static String Key ="activity_report";
    static List<CustomClass> lcc;
    String check;
    Void v;
    public loadClist(Context c,Activity a,View rv){
        context=c;
        activity=a;
        rview=rv;
        tv=(TextView)rview.findViewById(R.id.textView);
        lcc=new ArrayList<CustomClass>();
        check=null;
    }
    @Override
    protected Void doInBackground(Void... params) {
        int totalcount=0;
        ArrayList<String> earray=new ArrayList<>();
        String emailIdOfContact = null;
        int emailType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
        String contactName = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        //UpdateRankAsync2 ur2=new UpdateRankAsync2(context);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(BaseColumns._ID));
                contactName = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // Log.i(TAG,"....contact name....." +
                // contactName);
                cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[] { id }, null);

                Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                while (emails.moveToNext()) {
                    emailIdOfContact = emails.getString(emails
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    //Log.e("Friends", "...COntact Name ...."+ contactName + "...contact Number..."+ emailIdOfContact);
                    CustomClass cc=new CustomClass(totalcount,"@",contactName,emailIdOfContact,randInt(1,500));
                    lcc.add(cc);
                    /* remove fr url update
                    String r=ur2.update(7217);
                    String time[] = r.split(",");
                    long totaltime = Long.parseLong(time[0]);
                    int x=-1;
                    try{
                        if(totaltime!=-1) {
                            x = (int) Float.parseFloat(time[1]);
                        }
                    }
                    catch(Exception e){

                    }
                    */
                    totalcount++;
                    earray.add(emailIdOfContact);
                    // Log.i(TAG,"...COntact Name ...."
                    // + contactName + "...contact Number..."
                    // + emailIdOfContact);
                    /*
                    emailType = emails.getInt(emails
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            */
                }
                emails.close();

            }
        }// end of contact name cursor
        cur.close();
        /*
        lcc.add(new CustomClass(totalcount, "@", "Nishant dummy2", "nishant1417@iiitd.ac.in", randInt(1, 500)));
        totalcount++;
        lcc.add(new CustomClass(totalcount,"@","Nishant@iiitd.com","nishant1417@iiitd.ac.in",randInt(1,500)));
        totalcount++;
        lcc.add(new CustomClass(totalcount, "@", "Nishant dummy3", "nishant1417@iiitd.ac.in", randInt(1, 500)));
        totalcount++;
                */
        FriendsRanking fr=new FriendsRanking();
        //Log.d("friends---->",earray.toString());
        String res=fr.getrank(earray);
        if(res.compareTo("-1")==0){
            //Toast.makeText(rview.getContext(),"Server error occured please try again later",Toast.LENGTH_LONG).show();
            return null;
        }
        Log.d("friends---->",res);
        res=res.substring(1,res.length()-1);
        //System.out.println("res sub"+res+res.length());
        String van[]=res.split(",");
        HashMap<String,Integer> hms=new HashMap<>();
        for(int k=0;k<van.length;k++){
            //System.out.println(van[k]);
            String emailr[]=van[k].split(":");
            emailr[0]=emailr[0].substring(emailr[0].indexOf("\"")+1,emailr[0].length()-1);
            emailr[1]=emailr[1].substring(1);
            int x=-1;
            try {
                x = Integer.parseInt(emailr[1]);
            }
            catch(Exception e){
                //System.out.println("no");
            }
            hms.put(emailr[0], x);
            //System.out.println(emailr[0]+x);
        }
        int l=0;

        while(l<lcc.size()){
            if(hms.containsKey(lcc.get(l).getSubtitle())){
                if(hms.get(lcc.get(l).getSubtitle())==-1){
                    lcc.remove(l);
                    continue;
                }
                else{
                    lcc.get(l).setRank(hms.get(lcc.get(l).getSubtitle()));
                    l++;
                }
            }
            else{
                lcc.remove(l);
               continue;
            }

        }

        Collections.sort(lcc, new Frndcomparator());

        String email="";
        String name="";
        String regex = "^(.+)@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        l=0;
        while(l<lcc.size()){
            if(lcc.get(l).getSubtitle().compareTo(email)==0&&lcc.get(l).getSubtitle().compareTo("")!=0){
                if(!pattern.matcher(lcc.get(l).getTitle()).matches()){
                    name=lcc.get(l).getTitle();
                    lcc.get(l-1).setTitle(name);
                }
                lcc.remove(l);
                continue;
            }

            else{
                email=lcc.get(l).getSubtitle();
                name=lcc.get(l).getTitle();
                l++;
            }
        }

        DatabaseHandler db=new DatabaseHandler(context);
        db.remove_all();
        for(int i=0;i<lcc.size();i++){
            CustomClass rv=new CustomClass(i+1,lcc.get(i).getTitle(),lcc.get(i).getSubtitle(),lcc.get(i).getRank());
            db.addRank(rv);
            Log.e("find people frgment",lcc.get(i).getTitle()+"  added");
            lcc.get(i).setCount(i+1);
        }
        forfrnds=new ListAdapterfrnds(context,lcc);
        return null;
        //return Long.valueOf(0);
        //return totalcount;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        tv.setText(progress[0]+"");
    }

    @Override
    protected void onPostExecute(Void unused) {
        Log.e("on","on post execute");
        //showDialog("Downloaded " + result + " bytes");

        ListView lv=(ListView)rview.findViewById(R.id.list_view2);

        try {
            tv.clearAnimation();
            tv.setVisibility(View.INVISIBLE);
            lv.setAdapter(forfrnds);
        }
        catch(Exception e){
            Log.e("FindPeopleFragment","onPostExecute");
        }

    }
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand=new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    /*
    public int getrank(){
        String serverResponse = "null";
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL + Key + "?userid=" + String.valueOf(7217));
            Date dNow = new Date( );
            SimpleDateFormat ft =
                    new SimpleDateFormat ("yyyy-MM-dd");
            String today = ft.format(dNow);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -30);
            Date yesterday = calendar.getTime();



            String monthdate = ft.format(yesterday);
            JSONObject userData = new JSONObject();

            //Toast.makeText(getActivity().getApplicationContext(), "Current Date: " + monthdate, Toast.LENGTH_LONG).show();





            userData.put("start_time", monthdate);
            userData.put("end_time", today);

            // System.out.println(userData.toString());
            httppost.setEntity(new StringEntity(userData.toString()));

            HttpResponse response = httpclient.execute(httppost);
            BufferedReader BuffRead = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),"UTF-8") );
            serverResponse = BuffRead.readLine();
            //tv = (TextView)rootView.findViewById(R.id.response);
            JSONObject object = (JSONObject) new JSONTokener(serverResponse).nextValue();
            String onfoot = object.getString("ON_FOOT");

        } catch(HttpHostConnectException e) {
            Log.v(e.toString(), "index=" + 1);
            //tv.setText(e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

        } catch(Exception error) {
            // System.out.println(error.toString());
            Log.v(error.toString(), "index=" + 1);
        }
        return -1;
    }
    */
}

class Frndcomparator implements Comparator<CustomClass> {
    @Override
    public int compare(CustomClass o1, CustomClass o2) {
        if(o1.getRank()==-1){
            return 1;
        }
        if(o2.getRank()==-1){
            return -1;
        }
        if(o1.getRank()!=o2.getRank()){
            return (o1.getRank()-o2.getRank());
        }
        else{
            return o1.getSubtitle().compareTo(o2.getSubtitle());
        }
    }
}
/*
class UpdateRankAsync2 {
    Context context;

    public UpdateRankAsync2(Context context){
        this.context = context;
    }

    protected String update(int rankp) {
        int userid = rankp;
        String ACCL_URL = Constants.URL;
        String rank = RankUpdate(userid, ACCL_URL, "rank");
        return rank;
    }

    public String RankUpdate(int USER_ID , String CONN_URL, String Key ) {

        String serverResponse = "null";
        try {
            Log.i("HomeFragment", "Inside RequestRankUpdate");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(CONN_URL + Key + "?userid=" + String.valueOf(USER_ID));
            //Log.d("testing connection", CONN_URL);

            HttpResponse response = httpclient.execute(httppost);
            BufferedReader BuffRead = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),"UTF-8") );
            serverResponse = BuffRead.readLine();
            Log.v("Rank",serverResponse);
        } catch (HttpHostConnectException e) {
            Log.e("HttpHostConnectE", e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

        } catch (Exception error) {
            Log.i("Exception", error.toString());
        }
        return serverResponse;
    }
}
*/

//sonia mam code
class FriendsRanking {
    public static String URL;
    public static String Key;
    public static String USER_ID;

    FriendsRanking() {
        Key = "friends_rank";
        URL = "http://muc.iiitd.edu.in:9060/";

    }

    public String getrank(ArrayList ar) {
        if (FindPeopleFragment.USER_ID == null) {
            return "-1";
        }
        this.USER_ID = FindPeopleFragment.USER_ID;
        String serverResponse = "null";
        JSONObject jobj = new JSONObject();

        ArrayList<String> email_ids = new ArrayList<String>();
        email_ids.add("meghaa@iiitd.ac.in");

        email_ids.add("ankital@iiitd.ac.in");
        email_ids.add("nothing@iiitd.ac.in");
        /*remove this
        ar.add("nisant1417@iiitd.ac.in");
        ar.add("naman1415@iiitd.ac.in");
        remove this ends*/
        try {
            jobj.put("emails", ar);

        } catch (Exception e) {

        }
        System.out.println(jobj.toJSONString());
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL + Key + "?userid=" + USER_ID);
            httppost.setEntity(new StringEntity(jobj.toString()));
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader BuffRead = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            serverResponse = BuffRead.readLine();

            System.out.println("server rsponse is" + serverResponse);
            //Log.v("abc", serverResponse);

        } catch (HttpHostConnectException e) {
            System.out.println(e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();
            serverResponse = "-1";
        } catch (Exception error) {
            System.out.println(error.toString());
            serverResponse = "-1";
        }
        return serverResponse;


    }
}