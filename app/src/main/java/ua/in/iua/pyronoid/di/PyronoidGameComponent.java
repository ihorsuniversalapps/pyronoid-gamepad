package ua.in.iua.pyronoid.di;

import javax.inject.Singleton;

import dagger.Component;
import ua.in.iua.pyronoid.MainActivity;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;
import ua.in.iua.pyronoid.view.PyronoidGameView;

/**
 * Created by rusin on 13.09.16.
 */

@Component(
        modules = {
                PyronoidGameViewModule.class,
                PyronoidGamePresenterModule.class
        }
)
@Singleton
public interface PyronoidGameComponent {
    void inject(MainActivity activity);

    PyronoidGamePresenter getPyronoidGamePresenter();

    PyronoidGameView getPyronoidGameView();
}