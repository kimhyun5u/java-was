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
        var json = JsonConverter.toJson(user).getBytes();
        ctx.response().setStatus(HttpStatus.REDIRECT_FOUND)
                .addHeader("Location", "/")
                .addHeader("Content-Type", "application/json")
                .setBody(json);
    }
}
