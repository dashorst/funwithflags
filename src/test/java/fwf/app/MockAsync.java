package fwf.app;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

import jakarta.websocket.RemoteEndpoint.Async;
import jakarta.websocket.SendHandler;

public class MockAsync implements Async {
    @Override
    public void setBatchingAllowed(boolean allowed) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'setBatchingAllowed'");
    }

    @Override
    public boolean getBatchingAllowed() {
        throw new UnsupportedOperationException("Unimplemented method 'getBatchingAllowed'");
    }

    @Override
    public void flushBatch() throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'flushBatch'");
    }

    @Override
    public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'sendPing'");
    }

    @Override
    public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'sendPong'");
    }

    @Override
    public long getSendTimeout() {
        throw new UnsupportedOperationException("Unimplemented method 'getSendTimeout'");
    }

    @Override
    public void setSendTimeout(long timeoutmillis) {
        throw new UnsupportedOperationException("Unimplemented method 'setSendTimeout'");
    }

    @Override
    public void sendText(String text, SendHandler handler) {
    }

    @Override
    public Future<Void> sendText(String text) {
        throw new UnsupportedOperationException("Unimplemented method 'sendText'");
    }

    @Override
    public Future<Void> sendBinary(ByteBuffer data) {
        throw new UnsupportedOperationException("Unimplemented method 'sendBinary'");
    }

    @Override
    public void sendBinary(ByteBuffer data, SendHandler handler) {
        throw new UnsupportedOperationException("Unimplemented method 'sendBinary'");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<Void> sendObject(Object data) {
        InvocationHandler handler = (o, m, p) -> {
            throw new UnsupportedOperationException("Unimplemented method '" + m.getName() + "'");
        };
        return (Future<Void>) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Future.class },
                handler);
    }

    @Override
    public void sendObject(Object data, SendHandler handler) {
    }
}
