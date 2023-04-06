package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements bgu.spl.net.api.bidi.Connections<T> {
    private ConcurrentHashMap<String, Client> clients;
    private static ConnectionsImpl instance;
    private int idCounter;
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connections;

    public ConnectionsImpl(){
        idCounter=0;
        connections=new ConcurrentHashMap<>();
        clients=new ConcurrentHashMap<>();
    }


    public void addConnection(ConnectionHandler<T> connectionHandler){
        connections.put(idCounter, connectionHandler);
        idCounter++;

    }

    public boolean addClient(Client c){
        if(!clients.containsKey(c.getName())){
            clients.put(c.getName(), c);
            return true;
        }
        return false;

    }

    public boolean login(String name, String password, char captcha){
        boolean ans=false;
        if(clients.containsKey(name)) {
            Client c = clients.get(name);
            if (c.log(password, captcha))
                ans=true;

        }
        return ans;
    }
    public Client getClient(String name){
        if(clients.containsKey(name))
            return clients.get(name);
        return null;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> connectionHandler=connections.get(connectionId);
        connectionHandler.send(msg);
        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }


    public int getID(){
        return idCounter;
    }

    public void post(String poster, T message, LinkedList<String> mentioned){
        LinkedList<String> alreadySent=new LinkedList<String>();
        Client pos=getClient(poster);
        for(Client c:clients.values()){
            boolean send=c.doesFollow(poster);
            if(send&&!c.isBlocking(poster)&&!pos.isBlocking(c.getName())) {
                if(c.isLogged()) {
                    alreadySent.add(c.getName());
                    send(c.getConnectionId(), message);
                }
                else{
                    alreadySent.add(c.getName());
                    c.addToSend((String) message);
                }

            }

        }
        if(mentioned.size()>0) {
            for (int i = 0; i < mentioned.size(); i++) {
                Client c=getClient(mentioned.get(i));
                if(c!=null) {
                    if (!poster.equals(c.getName()) && !alreadySent.contains(c.getName()) && clients.containsKey(c.getName())
                            && !c.isBlocking(poster) && !pos.isBlocking(c.getName())) {
                        Client send = clients.get(mentioned.get(i));
                        if (c.isLogged())
                            send(send.getConnectionId(), message);
                        else
                            c.addToSend((String) message);
                    }
                }
            }
        }


    }

    public ConcurrentHashMap<String, Client> getClients(){
        return clients;
    }
    public static synchronized ConnectionsImpl getInstance(){
        if(instance==null)
            instance=new ConnectionsImpl();
        return instance;

    }
}
