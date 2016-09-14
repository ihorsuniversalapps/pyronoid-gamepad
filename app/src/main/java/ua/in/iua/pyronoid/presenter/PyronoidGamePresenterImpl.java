package ua.in.iua.pyronoid.presenter;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.razorvine.pickle.PickleException;
import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ua.in.iua.pyronoid.view.PyronoidGameView;

/**
 * Pyronoid game Presenter implementation
 * Created by rusin on 11.08.16.
 */
public class PyronoidGamePresenterImpl implements PyronoidGamePresenter {

    private static final String MESSAGE_FIND_SERVER = "Looking for Pyronoid game server...";
    private PyronoidGameView mGameView;
    private volatile ConnectionState mConnectionState = ConnectionState.DISCONNECTED;
    private PyroProxy mCommandSendProxy = null;
    private NameServerProxy mNameServer = null;
    private ExecutorService mCommandSendQueue = Executors.newSingleThreadExecutor();

    @Override
    public void bindView(PyronoidGameView view) {
        mGameView = view;
        if (mConnectionState == ConnectionState.CONNECING) {
            mGameView.showProgressDialog(MESSAGE_FIND_SERVER);
        }
    }

    @Override
    public void unbindView() {
        mGameView.hideProgressDialog();
        mGameView = null;
    }

    @Override
    public void initPyroProxy() {
        mConnectionState = ConnectionState.CONNECING;
        if (mGameView != null) {
            mGameView.showProgressDialog(MESSAGE_FIND_SERVER);
        }
        ProxyInitializer proxyInitializer = new ProxyInitializer(new PyroProxyCallback() {
            @Override
            public void success(PyroServerDetails details) {
                mConnectionState = ConnectionState.CONNECTED;
                if (mGameView != null) {
                    mGameView.hideProgressDialog();
                    mGameView.onConnectedToGameServerSucceed(details);
                }
            }

            @Override
            public void error(PyroError error) {
                mConnectionState = ConnectionState.DISCONNECTED;
                if (mGameView != null) {
                    mGameView.hideProgressDialog();
                    mGameView.onConnectedToGameServerFailed(error);
                }
            }
        });
        proxyInitializer.execute();
    }

    public ConnectionState connectionState() {
        return mConnectionState;
    }

    @Override
    public void moveBat(final double pos) {
        mCommandSendQueue.submit(new Runnable() {
            @Override
            public void run() {
                if (mConnectionState == ConnectionState.DISCONNECTED) {
                    ProxyInitializerResponse proxyInitializerResponse = connectToPyroServer();
                    if (proxyInitializerResponse.getError() != null) {
                        return;
                    }
                    mConnectionState = ConnectionState.CONNECTED;
                }

                try {
                    mCommandSendProxy.call("move_bat", pos);
                } catch (IOException | PyroException | PickleException e) {
                    mConnectionState = ConnectionState.DISCONNECTED;
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void restartGame() {
        mCommandSendQueue.submit(new Runnable() {
            @Override
            public void run() {
                if (mConnectionState == ConnectionState.DISCONNECTED) {
                    ProxyInitializerResponse proxyInitializerResponse = connectToPyroServer();
                    if (proxyInitializerResponse.getError() != null) {
                        return;
                    }
                    mConnectionState = ConnectionState.CONNECTED;
                }

                try {
                    mCommandSendProxy.call("restart_game");
                } catch (IOException | PyroException | PickleException e) {
                    mConnectionState = ConnectionState.DISCONNECTED;
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void closeProxy() {
        if (mCommandSendProxy != null) {
            mCommandSendProxy.close();
        }
        if (mNameServer != null) {
            mNameServer.close();
        }
    }

    @NonNull
    private ProxyInitializerResponse connectToPyroServer() {
        try {
            if (mNameServer != null) {
                mNameServer.close();
            }
            mNameServer = NameServerProxy.locateNS(null);
        } catch (IOException | PyroException e) {
            e.printStackTrace();
            closeProxy();
            return new ProxyInitializerResponse(null, PyroError.NAME_SERVER_ERROR);
        }

        try {
            if (mCommandSendProxy != null) {
                mCommandSendProxy.close();
            }
            mCommandSendProxy = new PyroProxy(mNameServer.lookup("PYRONAME:local.pyronoid"));
        } catch (IOException | PyroException e) {
            e.printStackTrace();
            closeProxy();
            return new ProxyInitializerResponse(null, PyroError.PYRO_CONNECTION_ERROR);
        }
        return new ProxyInitializerResponse(new PyroServerDetails(mCommandSendProxy.hostname), null);
    }

    class ProxyInitializerResponse {
        PyroServerDetails mServerDetails;
        PyroError mError;

        public ProxyInitializerResponse(PyroServerDetails serverDetails, PyroError error) {
            mServerDetails = serverDetails;
            mError = error;
        }

        public PyroServerDetails getServerDetails() {
            return mServerDetails;
        }

        public PyroError getError() {
            return mError;
        }
    }

    private class ProxyInitializer extends AsyncTask<Void, Void, ProxyInitializerResponse> {

        PyroProxyCallback mCallback;

        public ProxyInitializer(@NonNull PyroProxyCallback callback) {
            mCallback = callback;
        }

        @Override
        protected ProxyInitializerResponse doInBackground(Void... voids) {
            return connectToPyroServer();
        }

        @Override
        protected void onPostExecute(ProxyInitializerResponse proxyInitializerResponse) {
            super.onPostExecute(proxyInitializerResponse);
            if (proxyInitializerResponse.getServerDetails() != null) {
                mCallback.success(proxyInitializerResponse.getServerDetails());
            } else {
                mCallback.error(proxyInitializerResponse.getError());
            }
        }
    }
}
