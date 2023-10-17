package fwf.app;

import java.util.Objects;

import jakarta.enterprise.context.Dependent;
import jakarta.websocket.Session;

@Dependent
public class Player {
    private String id;
    private Session session;
    private String name;

    public void init(Session session, String name) {
        this.session = session;
        this.name = name;
        this.id = session.getId() + "|" + name;
    }

    public Session session() {
        return session;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(session);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player other) {
            return Objects.equals(this.session(), other.session());
        }
        return false;
    }
}
