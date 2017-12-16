package viewsHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flow.flowlocationassignment.R;

import Interfaces.DialogCallBack;
import helper.AppConfig;

/**
 * Created by Usman Khan on 13/12/2017.
 * This class provides small View related modules like progress bar, hiding keyboard.
 */

public class UIView {


    private static UIView uiViewInstance;
    DialogCallBack dialogCallBack = null;
    String title = "", description = "";

    public static UIView getInstance() {
        if (uiViewInstance == null) {
            uiViewInstance = new UIView();
        }
        return uiViewInstance;
    }

    public void setiDialogListener(DialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
    }

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

    public void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showDialogBox(final Context context) {

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

                    if(dialogCallBack!=null){
                        dialogCallBack.sendCall(description,title);
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


}
