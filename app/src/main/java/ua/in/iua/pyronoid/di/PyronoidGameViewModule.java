package ua.in.iua.pyronoid.di;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import ua.in.iua.pyronoid.view.PyronoidGameView;
import ua.in.iua.pyronoid.view.PyronoidGameViewImpl;

/**
 * Created by rusin on 13.09.16.
 */

@Module
public class PyronoidGameViewModule {

    private Context mContext;

    public PyronoidGameViewModule(@NonNull Context context) {
        mContext = context;
    }

    @Provides
    PyronoidGameView providePyronoidGameView() {
        return new PyronoidGameViewImpl(mContext);
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
