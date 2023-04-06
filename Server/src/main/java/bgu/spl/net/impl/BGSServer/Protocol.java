package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;

public class Protocol<T> implements BidiMessagingProtocol<T> {


    private Connections<T> con;
    private int connectionID;
    private boolean shouldTerminate = false;
    private boolean connectedUser;
    private Client user;

    @Override
    public void process(T msg) {
        MessageDecider<T> messageDecider=new MessageDecider<T>(msg, con, connectionID, this);
        T message= messageDecider.result();
        if(message!=null)
            con.send(connectionID,  message);

    }

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.con=connections;
        this.connectionID=connectionId;
        this.connectedUser=false;
        user=null;

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void terminate(){
        shouldTerminate=true;
    }
    public boolean isConnectedUser(){
        return connectedUser;
    }

    public void connectUser(Client c){
        user=c;
        c.setConnectionId(connectionID);
        connectedUser=true;
    }

    public Client getUser(){
        return user;
    }

    public boolean follow(Client called, char follow){
        boolean ans=user.follow(called, follow);
        return ans;
    }

    public void readAll(){
        if(user!=null) {
            LinkedList<String> messages = user.getmTOSend();
            if(messages.size()>0) {
                for(int i=0;i<messages.size();i++) {
                    con.send(connectionID, (T) messages.get(i));
                }
            }
        }
    }
    public void addMessage(String  message){
        user.addToSend(message);
    }

}
