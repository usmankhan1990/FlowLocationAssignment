package viewsHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;

import Interfaces.DialogConfirmCallBack;
import Interfaces.DialogTitleDescriptionCallBack;

/**
 * Created by Usman Khan on 13/12/2017.
 * This class provides View related modules like progress bar, custom Dialog boxes.
 */

public class UIView {


    private static UIView uiViewInstance;
    DialogTitleDescriptionCallBack dialogTitleDescriptionCallBack = null;
    DialogConfirmCallBack dialogConfirmCallBack = null;
    String title = "", description = "";

    public static UIView getInstance() {
        if (uiViewInstance == null) {
            uiViewInstance = new UIView();
        }
        return uiViewInstance;
    }


    /**
     * <p>Setting instance of DialogTitleDescription interface
     *    This function will use for sending call back to the desired View class.
     *    It is will by StartEnd Trip Dialog box
     * </p>
     *@param dialogTitleDescriptionCallBack   - Making an instance of DialogTitleDescriptionCallBack.
     */
    public void setiDialogTitleDescriptionListener(DialogTitleDescriptionCallBack dialogTitleDescriptionCallBack) {
        this.dialogTitleDescriptionCallBack = dialogTitleDescriptionCallBack;
    }


    /**
     * <p>Setting instance of DialogConfirmCallBack interface
     *    This function will use for sending call back to the desired View class.
     *    It is will by StartEnd Trip Dialog box
     * </p>
     *@param dialogConfirmCallBack   - Making an instance of DialogConfirmCallBack.
     */
    public void setTripStartOffDialogListener(DialogConfirmCallBack dialogConfirmCallBack) {
        this.dialogConfirmCallBack = dialogConfirmCallBack;
    }



    /**
     * <p>This function initialize Progress Dialog for showing.
     * </p>
     *@param context   - Context from desired class.
     */

    public ProgressDialog showProgressBar(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public void showToast(Context context, String msg) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * <p>This function gets title and description for Start Trip to make a custom dialog box.
     * Sends a callback to activity after taking title and description.
     * Yes will send a call and start a trip.
     * </p>
     *@param context   - Context from desired class.
     */


    public void showTripTitleDescDialogBox(final Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("Write details");

        // set the custom dialog components - textView, editFields and button

        final EditText edtTitle       = dialog.findViewById(R.id.edtTitle);
        final EditText edtDescription = dialog.findViewById(R.id.edtDescription);
              Button btnStartTrip     = dialog.findViewById(R.id.btnStartTrip);
              Button btnCancel     = dialog.findViewById(R.id.btnCancel);

        // if button is clicked, close the custom dialog
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title       = edtTitle.getText().toString();
                description = edtDescription.getText().toString();

                if(title.equalsIgnoreCase("")||description.equalsIgnoreCase("")){

                    Toast.makeText(context, "Please fill all fields",Toast.LENGTH_LONG).show();
                }else{

                    if(dialogTitleDescriptionCallBack !=null){
                        dialogTitleDescriptionCallBack.sendDescriptionTitleTrip(description,title);
                        dialog.dismiss();
                    }

                    }


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * <p>This function shows confirm dialog for Start & Stop Trip to make a custom dialog box.
     *    Taking true value for start Trip confirmation.
     *    Taking false value for stop Trip confirmation.
     *    Negative Button will dismiss the Dialog box.
     *    Sends a callback to activity after taking true, false value for Trip.
     * </p>
     *@param context   - Context from desired class.
     *@param message   - Message to display here.
     *@param title     - Title to display here.
     *@param startTrip - Boolean for start(true) and start(false) for Trip off and Trip on.
     */


    public void confirmDialog(Context context, String message, String title,final boolean startTrip){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(dialogConfirmCallBack !=null){
                            if(startTrip == true){
                                dialogConfirmCallBack.sendStartStopTrip(true);
                                dialog.dismiss();
                            }else{
                                dialogConfirmCallBack.sendStartStopTrip(false);
                                dialog.dismiss();
                            }

                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        if(dialogConfirmCallBack !=null){
                            dialog.dismiss();
                        }
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}
