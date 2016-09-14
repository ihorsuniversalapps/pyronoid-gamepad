package ua.in.iua.pyronoid.di;

import dagger.Module;
import dagger.Provides;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenterImpl;

/**
 * Module for Pyronoid game model (MVP)
 * Created by rusin on 12.09.16.
 */

@Module
public class PyronoidGamePresenterModule {

    @Provides
    @PyronoidGameScope
    PyronoidGamePresenter providePyronoidGamePresenter() {
        return new PyronoidGamePresenterImpl();
    }
}
