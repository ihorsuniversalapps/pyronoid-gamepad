package ua.in.iua.pyronoid.di;

import dagger.Module;
import dagger.Provides;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenterImpl;
import ua.in.iua.pyronoid.view.PyronoidGameView;

/**
 * Module for Pyronoid game model (MVP)
 * Created by rusin on 12.09.16.
 */

@Module
public class PyronoidGamePresenterModule {
    @Provides
    PyronoidGamePresenter providePyronoidGamePresenter(PyronoidGameView gameView) {
        return new PyronoidGamePresenterImpl(gameView);
    }
}
