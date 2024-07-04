package codesquad.http.handler;

import codesquad.http.Context;

@FunctionalInterface
public interface Handler {
    void handle(Context ctx);
}
