package codesquad.server.handlers;

import codesquad.http.Context;

public class ViewRegistrationHandler {
    public static void viewRegistration(Context ctx) {
        ctx.response()
                .addHeader("Content-Type", "text/html")
                .setBody("<html><h1>registration</h1></html>".getBytes());

    }
}
