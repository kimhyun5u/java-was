package codesquad.http.handler;

@FunctionalInterface
public interface Handler {
    void handle(Context ctx);
}
