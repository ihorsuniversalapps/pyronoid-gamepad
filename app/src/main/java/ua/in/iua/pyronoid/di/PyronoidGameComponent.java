package ua.in.iua.pyronoid.di;

import dagger.Component;
import ua.in.iua.pyronoid.MainActivity;
import ua.in.iua.pyronoid.presenter.PyronoidGamePresenter;

/**
 * Created by rusin on 13.09.16.
 */

@Component(
        modules = {
                PyronoidGamePresenterModule.class
        }
)
@PyronoidGameScope
public interface PyronoidGameComponent {
    void inject(MainActivity activity);

    PyronoidGamePresenter getPyronoidGamePresenter();
}
