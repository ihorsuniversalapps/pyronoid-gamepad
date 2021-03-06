package ua.in.iua.pyronoid.view;

import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;

/**
 * Created by rusin on 13.09.16.
 */

public interface PyronoidGameView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void onConnectedToGameServerSucceed(PyronoidGamePresenter.PyroServerDetails details);

    void onConnectedToGameServerFailed(PyronoidGamePresenter.PyroError errors);
}
