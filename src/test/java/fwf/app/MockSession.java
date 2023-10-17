package fwf.app;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.MessageHandler.Partial;
import jakarta.websocket.MessageHandler.Whole;
import jakarta.websocket.RemoteEndpoint.Async;
import jakarta.websocket.RemoteEndpoint.Basic;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

public class MockSession implements Session {
    private String id;

    public MockSession() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public WebSocketContainer getContainer() {
        throw new UnsupportedOperationException("Unimplemented method 'getContainer'");
    }

    @Override
    public void addMessageHandler(MessageHandler handler) throws IllegalStateException {
        throw new UnsupportedOperationException("Unimplemented method 'addMessageHandler'");
    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, Whole<T> handler) {
        throw new UnsupportedOperationException("Unimplemented method 'addMessageHandler'");
    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, Partial<T> handler) {
        throw new UnsupportedOperationException("Unimplemented method 'addMessageHandler'");
    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        throw new UnsupportedOperationException("Unimplemented method 'getMessageHandlers'");
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {
        throw new UnsupportedOperationException("Unimplemented method 'removeMessageHandler'");
    }

    @Override
    public String getProtocolVersion() {
        throw new UnsupportedOperationException("Unimplemented method 'getProtocolVersion'");
    }

    @Override
    public String getNegotiatedSubprotocol() {
        throw new UnsupportedOperationException("Unimplemented method 'getNegotiatedSubprotocol'");
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        throw new UnsupportedOperationException("Unimplemented method 'getNegotiatedExtensions'");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Unimplemented method 'isSecure'");
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Unimplemented method 'isOpen'");
    }

    @Override
    public long getMaxIdleTimeout() {
        throw new UnsupportedOperationException("Unimplemented method 'getMaxIdleTimeout'");
    }

    @Override
    public void setMaxIdleTimeout(long milliseconds) {
        throw new UnsupportedOperationException("Unimplemented method 'setMaxIdleTimeout'");
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int length) {
        throw new UnsupportedOperationException("Unimplemented method 'setMaxBinaryMessageBufferSize'");
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        throw new UnsupportedOperationException("Unimplemented method 'getMaxBinaryMessageBufferSize'");
    }

    @Override
    public void setMaxTextMessageBufferSize(int length) {
        throw new UnsupportedOperationException("Unimplemented method 'setMaxTextMessageBufferSize'");
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        throw new UnsupportedOperationException("Unimplemented method 'getMaxTextMessageBufferSize'");
    }

    @Override
    public Async getAsyncRemote() {
        return new MockAsync();
    }

    @Override
    public Basic getBasicRemote() {
        throw new UnsupportedOperationException("Unimplemented method 'getBasicRemote'");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
    }

    @Override
    public URI getRequestURI() {
        throw new UnsupportedOperationException("Unimplemented method 'getRequestURI'");
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        throw new UnsupportedOperationException("Unimplemented method 'getRequestParameterMap'");
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Unimplemented method 'getQueryString'");
    }

    @Override
    public Map<String, String> getPathParameters() {
        throw new UnsupportedOperationException("Unimplemented method 'getPathParameters'");
    }

    @Override
    public Map<String, Object> getUserProperties() {
        throw new UnsupportedOperationException("Unimplemented method 'getUserProperties'");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Unimplemented method 'getUserPrincipal'");
    }

    @Override
    public Set<Session> getOpenSessions() {
        throw new UnsupportedOperationException("Unimplemented method 'getOpenSessions'");
    }
}
