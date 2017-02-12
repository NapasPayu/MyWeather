package com.napas.myweather.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.napas.myweather.R;

public class DialogFactory {

    public static Dialog createSimpleOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(context.getString(R.string.dialog_action_ok), null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setMessage(message)
                .setNeutralButton(context.getString(R.string.dialog_action_ok), null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkDialog(Context context,
                                              @StringRes int titleResource,
                                              @StringRes int messageResource) {
        return createSimpleOkDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_title_error))
                .setMessage(message)
                .setNeutralButton(context.getString(R.string.dialog_action_ok), null);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }
}
