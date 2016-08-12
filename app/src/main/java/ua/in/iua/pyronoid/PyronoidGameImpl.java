package ua.in.iua.pyronoid;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.razorvine.pickle.PickleException;
import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rusin on 11.08.16.
 */
public class PyronoidGameImpl implements PyronoidGame {

    private PyroProxy mCommandSendProxy = null;
    private NameServerProxy mNameServer = null;
    private ExecutorService mCommandSendQueue = Executors.newSingleThreadExecutor();

    @Override
    public void initPyroProxy(PyroProxyCallback callback) {
        ProxyInitializer proxyInitializer = new ProxyInitializer(callback);
        proxyInitializer.execute();
    }

    @Override
    public void moveBat(final double pos) {
        mCommandSendQueue.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mCommandSendProxy.call("move_bat", pos);
                } catch (IOException | PyroException | PickleException e) {
                    // Currently ignoring
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void restartGame() {

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

    class ProxyIniterResponse {
        PyroServerDetails mServerDetails;
        PyroError mError;

        public ProxyIniterResponse(PyroServerDetails serverDetails, PyroError error) {
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

    class ProxyInitializer extends AsyncTask<Void, Void, ProxyIniterResponse> {

        PyroProxyCallback mCallback;

        public ProxyInitializer(@NonNull PyroProxyCallback callback) {
            mCallback = callback;
        }

        @Override
        protected ProxyIniterResponse doInBackground(Void... voids) {
            try {
                mNameServer = NameServerProxy.locateNS(null);
            } catch (IOException | PyroException e) {
                e.printStackTrace();
                closeProxy();
                return new ProxyIniterResponse(null, PyroError.NAME_SERVER_ERROR);
            }

            try {
                mCommandSendProxy = new PyroProxy(mNameServer.lookup("PYRONAME:local.pyronoid"));
            } catch (IOException | PyroException e) {
                e.printStackTrace();
                closeProxy();
                return new ProxyIniterResponse(null, PyroError.PYRO_CONNECTION_ERROR);
            }
            return new ProxyIniterResponse(new PyroServerDetails(mCommandSendProxy.hostname), null);
        }

        @Override
        protected void onPostExecute(ProxyIniterResponse proxyIniterResponse) {
            super.onPostExecute(proxyIniterResponse);
            if (proxyIniterResponse.getServerDetails() != null) {
                mCallback.success(proxyIniterResponse.getServerDetails());
            } else {
                mCallback.error(proxyIniterResponse.getError());
            }
        }
    }
}
