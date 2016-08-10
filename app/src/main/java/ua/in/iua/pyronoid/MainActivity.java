package ua.in.iua.pyronoid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Pyronoid";
    private volatile double curPosX = 0;
    private View moveView;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            curPosX = event.getX();
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveView = findViewById(R.id.vTouchView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        moveView.post(new Runnable() {
            @Override
            public void run() {
                final long maxX = moveView.getMeasuredWidth();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NameServerProxy ns = null;
                        PyroProxy batMover = null;
                        try {
                            ns = NameServerProxy.locateNS(null);

                            batMover = new PyroProxy(ns.lookup("PYRONAME:local.pyronoid"));

                            while (true) {
                                double newX = curPosX / (double) maxX;
                                Log.d(TAG, String.format("run: newX: %1.5f", newX));
                                batMover.call("move", newX);
                                Thread.sleep(10);
                            }


                        } catch (IOException | InterruptedException | PyroException e) {
                            e.printStackTrace();
                        } finally {
                            Toast.makeText(MainActivity.this, "Connection is closed", Toast.LENGTH_SHORT).show();

                            if (batMover != null) {
                                batMover.close();
                            }
                            if (ns != null) {
                                ns.close();
                            }
                        }
                    }
                }).start();
            }
        });
    }

}
