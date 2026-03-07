package app.controllers.Generic;

import io.javalin.http.Context;

public interface IController{

    void getAll(Context ctx);

    void getByID(Context ctx);
}
