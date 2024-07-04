package codesquad.server.handlers;

import codesquad.http.Context;
import codesquad.http.HttpStatus;
import codesquad.model.User;
import codesquad.utils.JsonConverter;

public class CreateUserHandler {
    private CreateUserHandler() {
    }

    public static void createUser(Context ctx) {
        User user = new User(ctx.request().getQuery("userId"), ctx.request().getQuery("password"), ctx.request().getQuery("name"));
        String json = JsonConverter.toJson(user);
        ctx.response().setStatus(HttpStatus.CREATED);
        ctx.response().setVersion(ctx.request().getVersion());
        ctx.response().addHeader("Content-Type", "application/json");
        ctx.response().addHeader("Content-Length", String.valueOf(json.getBytes().length));
        ctx.response().setBody(json.getBytes());
    }
}
