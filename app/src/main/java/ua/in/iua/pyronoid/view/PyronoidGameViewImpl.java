package ua.in.iua.pyronoid.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * View implementation
 * Created by rusin on 13.09.16.
 */
public class PyronoidGameViewImpl implements PyronoidGameView {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public PyronoidGameViewImpl(Context context) {
        mContext = context;
    }

    @Override
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
