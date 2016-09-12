package ua.in.iua.pyronoid;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component to resolve dependencies.
 * Created by rusin on 12.09.16.
 */

@Component(
        modules = {PyronoidGameModule.class}
)
@Singleton
public interface PyronoidGameComponent {
    void inject(MainActivity activity);

    PyronoidGame getPyronoidGame();
}
