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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Pyronoid";
    @Inject
    public PyronoidGame mGame;
    private long maxActivityWidth = 0L;
    private View moveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveView = findViewById(R.id.vTouchView);

        DaggerPyronoidGameComponent.builder().pyronoidGameModule(new PyronoidGameModule()).build().inject(this);
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

    @Override
    protected void onStart() {
        super.onStart();

        connectToServer();
    }

    private void connectToServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Looking for Pyronoid game server...");
        dialog.show();

        mGame.initPyroProxy(new PyronoidGame.PyroProxyCallback() {
            @Override
            public void success(PyronoidGame.PyroServerDetails details) {
                dialog.dismiss();
                setTitle(String.format(Locale.getDefault(), "Connected to: %s", details.getHostname()));
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
            public void error(PyronoidGame.PyroError errors) {
                dialog.dismiss();
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
            }
        });
    }

}
