package com.relecotech.androidsparsh_smallwonder.utils;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.relecotech.androidsparsh_smallwonder.R;


/**
 * Created by Relecotech on 24-01-2018.
 */

public class DialogsUtils {

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }

    public static void showAlertDialog(Context context, String title, String message, Boolean status) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.ic_warning_red_a700_24dp);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.create().show();

    }

    public static void showAlertDialogOnlyTitle(Context context, String title, Boolean status) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.ic_warning_red_a700_24dp);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.create().show();

    }
}
