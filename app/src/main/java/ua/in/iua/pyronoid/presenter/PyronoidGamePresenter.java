package ua.in.iua.pyronoid.presenter;

/**
 * Created by rusin on 11.08.16.
 */
public interface PyronoidGamePresenter {
    void initPyroProxy(PyroProxyCallback callback);

    void moveBat(double pos);

    void restartGame();

    void closeProxy();

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
