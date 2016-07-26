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
                PyroProxy remoteobject = null;
                try {
                    ns = NameServerProxy.locateNS(null);

                    remoteobject = new PyroProxy(ns.lookup("local.pyronoid"));

                    while (true) {
                        Log.d("666", "run: Sent");
                        remoteobject.call("move", 42);
                        Thread.sleep(1000);
                    }


                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if (remoteobject != null) {
                        remoteobject.close();
                    }
                    if (ns != null) {
                        ns.close();
                    }
                }
            }
        }).start();
    }
}
