package viewsHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.flow.flowlocationassignment.R;

import helper.AppConfig;

/**
 * Created by Usman Khan on 13/12/2017.
 * This class provides small View related modules like progress bar, hiding keyboard.
 */

public class UIView {

    private static UIView uiViewInstance;

    public static UIView getInstance() {
        if (uiViewInstance == null) {
            uiViewInstance = new UIView();
        }
        return uiViewInstance;
    }

    public ProgressDialog showProgressBar(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public void showToast(Context context, String msg){
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard(Activity context){
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
