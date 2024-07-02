package codesquad.http.handler;

import java.net.Socket;

public interface RequestHandler {
    void handleRequest(Socket clientSocket);
}
