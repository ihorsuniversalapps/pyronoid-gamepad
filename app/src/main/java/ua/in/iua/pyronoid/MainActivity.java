package ua.in.iua.pyronoid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import ua.in.iua.pyronoid.di.DaggerPyronoidGameComponent;
import ua.in.iua.pyronoid.di.PyronoidGamePresenterModule;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;
import ua.in.iua.pyronoid.view.PyronoidGameView;

public class MainActivity extends AppCompatActivity implements PyronoidGameView {
    private static final String TAG = "Pyronoid";

    @Inject
    public PyronoidGamePresenter mGame;

    private long maxActivityWidth = 0L;
    private View moveView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveView = findViewById(R.id.vTouchView);

        mGame = (PyronoidGamePresenter) getLastCustomNonConfigurationInstance();
        if (mGame == null) {
            DaggerPyronoidGameComponent.builder()
                    .pyronoidGamePresenterModule(new PyronoidGamePresenterModule())
                    .build()
                    .inject(this);
        }

        if (savedInstanceState == null) {
            moveView.post(new Runnable() {
                @Override
                public void run() {
                    connectToServer();
                }
            });
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mGame;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuReconnect:
                connectToServer();
                break;
            case R.id.menuRestart:
                mGame.restartGame();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectToServer() {
        mGame.initPyroProxy();
    }

    private void initViewTouchListener() {
        moveView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    final float curPosX = motionEvent.getX();
                    if (maxActivityWidth > 0) {
                        mGame.moveBat(curPosX / (double) maxActivityWidth);
                        return true;
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        mGame.closeProxy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGame.unbindView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGame.bindView(this);

        moveView.post(new Runnable() {
            @Override
            public void run() {
                maxActivityWidth = moveView.getMeasuredWidth();
                initViewTouchListener();
            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
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

    @Override
    public void onConnectedToGameServerSucceed(PyronoidGamePresenter.PyroServerDetails details) {
        setTitle(String.format(Locale.getDefault(), "Connected to: %s", details.getHostname()));
        initViewTouchListener();
    }

    @Override
    public void onConnectedToGameServerFailed(PyronoidGamePresenter.PyroError errors) {
        switch (errors) {
            case NAME_SERVER_ERROR:
                Toast.makeText(MainActivity.this, "Pyro name server not found.", Toast.LENGTH_LONG).show();
                break;
            case PYRO_CONNECTION_ERROR:
                Toast.makeText(MainActivity.this, "Pyro connection error.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
