package ua.in.iua.pyronoid.presenter;

import ua.in.iua.pyronoid.view.PyronoidGameView;

/**
 * Pyronoid Game Presenter interface
 * Created by rusin on 11.08.16.
 */
public interface PyronoidGamePresenter {

    void bindView(PyronoidGameView view);

    void unbindView();

    void initPyroProxy();

    ConnectionState connectionState();

    void moveBat(double pos);

    void restartGame();

    void closeProxy();

    enum ConnectionState {
        DISCONNECTED,
        CONNECING,
        CONNECTED
    }

    enum PyroError {
        NAME_SERVER_ERROR,
        PYRO_CONNECTION_ERROR
    }

    interface PyroProxyCallback {
        void success(PyroServerDetails details);

        void error(PyroError errors);
    }

    class PyroServerDetails {
        private String mHostname;

        public PyroServerDetails(String hostname) {
            mHostname = hostname;
        }

        public String getHostname() {
            return mHostname;
        }
    }
}
