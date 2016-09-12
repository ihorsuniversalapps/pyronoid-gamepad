package ua.in.iua.pyronoid;

import dagger.Module;
import dagger.Provides;

/**
 * Module for Pyronoid game model (MVP)
 * Created by rusin on 12.09.16.
 */

@Module
public class PyronoidGameModule {
    @Provides
    PyronoidGame providePyronoidGame() {
        return new PyronoidGameImpl();
    }
}
