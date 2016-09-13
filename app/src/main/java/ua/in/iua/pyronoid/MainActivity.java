package ua.in.iua.pyronoid;

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
import ua.in.iua.pyronoid.di.PyronoidGameViewModule;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;
import ua.in.iua.pyronoid.view.PyronoidGameView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Pyronoid";

    @Inject
    public PyronoidGamePresenter mGame;

    @Inject
    public PyronoidGameView mGameView;

    private long maxActivityWidth = 0L;
    private View moveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveView = findViewById(R.id.vTouchView);

        DaggerPyronoidGameComponent.builder()
                .pyronoidGamePresenterModule(new PyronoidGamePresenterModule())
                .pyronoidGameViewModule(new PyronoidGameViewModule(this))
                .build()
                .inject(this);

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
        mGame.initPyroProxy(new PyronoidGamePresenter.PyroProxyCallback() {
            @Override
            public void success(PyronoidGamePresenter.PyroServerDetails details) {
                setTitle(String.format(Locale.getDefault(), "Connected to: %s", details.getHostname()));
                initViewTouchListener();
            }

            @Override
            public void error(PyronoidGamePresenter.PyroError errors) {
                switch (errors) {
                    case NAME_SERVER_ERROR:
                        Toast.makeText(MainActivity.this, "Pyro name server not found.", Toast.LENGTH_LONG).show();
                        break;
                    case PYRO_CONNECTION_ERROR:
                        Toast.makeText(MainActivity.this, "Pyro connection error.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
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
    protected void onResume() {
        super.onResume();

        moveView.post(new Runnable() {
            @Override
            public void run() {
                maxActivityWidth = moveView.getMeasuredWidth();
                initViewTouchListener();
            }
        });
    }
}
