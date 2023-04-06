package bgu.spl.net.api.bidi;

import bgu.spl.net.impl.BGSServer.Client;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

    public void addConnection(ConnectionHandler<T> connectionHandler);
    public boolean addClient(Client c);
    public Client getClient(String name);
    public boolean login(String name, String password, char captcha);
    public void post(String poster, T message, LinkedList<String> mentioned);
    public ConcurrentHashMap<String, Client> getClients();
    public int getID();
}
