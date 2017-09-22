package io.github.chaitya62.tripsharr.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by mikasa on 10/9/17.
 */

public class AlertDialogHelper {
    Context context;
    AlertDialog alertDialog=null;
    AlertDialogListener callBack;
    Activity current_activity;
    String tripUrl="http://tripshare.codeadventure.in/TripShare/index.php/Trip/delete/";

    public AlertDialogHelper(Context context)
    {
        this.context = context;
        this.current_activity = (Activity) context;
        callBack = (AlertDialogListener) context;
    }

    /**
     * Displays the AlertDialog with 3 Action buttons
     *
     * you can set cancelable property
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param neutral
     * @param from
     * @param isCancelable
     */
    public void showAlertDialog(String title, String message, String positive, String negative, String neutral, final int from, boolean isCancelable)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(current_activity);

        if(!TextUtils.isEmpty(title))
            alertDialogBuilder.setTitle(title);
        if(!TextUtils.isEmpty(message))
            alertDialogBuilder.setMessage(message);

        if(!TextUtils.isEmpty(positive)) {
            alertDialogBuilder.setPositiveButton(positive,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Log.v("positive","hello");
                            /*if(!selectTrips.isEmpty()){
                                for(Trip t : selectTrips){
                                    String tempUrl = tripUrl + t.getId();
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                         jsonObject = new JSONObject(t.toString());
                                    }
                                    catch (Exception e){}
                                    deleteTrip(tempUrl,jsonObject);
                                }
                            }*/
                            callBack.onPositiveClick(from);
                            alertDialog.dismiss();
                        }
                    });
        }
        if(!TextUtils.isEmpty(neutral)) {
            alertDialogBuilder.setNeutralButton(neutral,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            callBack.onNeutralClick(from);
                            alertDialog.dismiss();
                        }
                    });
        }
        if(!TextUtils.isEmpty(negative))
        {
            alertDialogBuilder.setNegativeButton(negative,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            callBack.onNegativeClick(from);
                            alertDialog.dismiss();
                        }
                    });
        }
        else
        {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        Button negative_button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negative_button.setVisibility(View.GONE);

                        Looper.loop();

                    }
                }.start();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        alertDialogBuilder.setCancelable(isCancelable);


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Displays the AlertDialog with positive action button only
     *
     * you can set cancelable property
     *
     * @param title
     * @param message
     * @param positive
     * @param from
     * @param isCancelable
     */
    public void showAlertDialog(String title,String message,String positive,final int from,boolean isCancelable)
    {
        showAlertDialog(title,message,positive,"","",from,isCancelable);
    }

    /**
     * Displays the AlertDialog with positive action button only
     *
     * cancelable property is false (Default)
     *
     * @param title
     * @param message
     * @param positive
     * @param from
     */
    public void showAlertDialog(String title,String message,String positive,final int from)
    {
        showAlertDialog(title,message,positive,"","",from,false);
    }


    /**
     *
     * Displays the AlertDialog with positive & negative buttons
     *
     * you can set cancelable property
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param from
     * @param isCancelable
     */

    public void showAlertDialog(String title, String message, String positive, String negative, final int from, boolean isCancelable)
    {
        showAlertDialog(title,message,positive,negative,"",from,isCancelable);
    }

    /**
     *
     * Displays the AlertDialog with positive & negative buttons
     *
     * cancelable property is false (Default)
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param from
     */
    public void showAlertDialog(String title,String message,String positive,String negative,final int from)
    {
        showAlertDialog(title,message,positive,negative,"",from,false);
    }

    /**
     * Displays the AlertDialog with 3 Action buttons
     *
     * cancelable property is false (Default)
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param neutral
     * @param from
     */
    public void showAlertDialog(String title,String message,String positive,String negative,String neutral,final int from)
    {
        showAlertDialog(title,message,positive,negative,neutral,from,false);
    }


    public interface AlertDialogListener
    {
        public void onPositiveClick(int from);
        public void onNegativeClick(int from);
        public void onNeutralClick(int from);
    }

    public void deleteTrip(String url,JSONObject jsonObject){
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("respo : ",""+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Hugga : ", error.toString());
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonRequest);
    }

}
