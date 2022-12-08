package edu.cmu.activitymachine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

/* Author: Xiawei He
 *  Andrew-id: xiaweih
 */

/*
* AlertDialog for input error
* code referred to
* https://developer.android.com/guide/topics/ui/dialogs.html
*/
public class StartErrorDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.input_error_message)
                .setNeutralButton(R.string.close_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setTitle("Warning:");

        // create the AlertDialog object and return it
        return builder.create();
    }
}
