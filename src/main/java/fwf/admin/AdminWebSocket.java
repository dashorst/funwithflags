package fwf.admin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import fwf.app.Application;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/admin/ws")
public class AdminWebSocket {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance adminPartial(Application application, Statistics stats);
    }

    @Inject
    Application application;
    
    @Inject
    Statistics statistics;

    private List<Session> sessions = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        sessions.remove(session);
    }

    @Scheduled(every = "2s")
    public void onScheduledRefresh() {
        var html = Templates.adminPartial(application, statistics).render();
        sessions.forEach(s -> s.getAsyncRemote().sendObject(html));
    }
}
