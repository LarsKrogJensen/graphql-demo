package se.lars;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;


public class ApiUser extends AbstractUser
{
    private JsonObject _principal;

    public ApiUser(JsonObject principal)
    {
        _principal = principal;
    }

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler)
    {

    }

    @Override
    public JsonObject principal()
    {
        return _principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider)
    {

    }
}
