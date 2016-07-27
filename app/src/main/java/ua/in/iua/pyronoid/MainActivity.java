package ua.in.iua.pyronoid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroProxy;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NameServerProxy ns = null;
                PyroProxy batMover = null;
                try {
                    ns = NameServerProxy.locateNS(null);

                    batMover = new PyroProxy(ns.lookup("PYRONAME:local.pyronoid"));

                    while (true) {
                        Log.d("666", "run: Sent");
                        batMover.call("hello", 42);
                        Thread.sleep(1000);
                    }


                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
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
}
