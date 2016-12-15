package com.margin.camera.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.margin.camera.R;

/**
 * Created on Feb 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class DialogUtils {

    /**
     * Shows camera error dialog if camera is not available now. If user press 'Ok',
     * photo activity will be finished.
     */
    public static Dialog showCameraErrorDialog(final Activity activity) {
        Dialog dialog = new AlertDialog.Builder(activity).setTitle(R.string.error_title_camera)
                .setMessage(R.string.error_message_camera)
                .setIcon(R.drawable.ic_warning_black).setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                                dialog.dismiss();
                            }
                        }).create();
        dialog.show();
        return dialog;
    }

    /**
     * Shows image warning dialog if user if going to discard the image. If user press 'Ok',
     * image will be discarded.
     */
    public static void showImageWarningDialog(Context context, final Runnable onSuccessAction) {
        new AlertDialog.Builder(context).setMessage(R.string.warning_message_discard_image)
                .setPositiveButton(R.string.dialog_action_discard,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onSuccessAction != null) {
                                    onSuccessAction.run();
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }
}
