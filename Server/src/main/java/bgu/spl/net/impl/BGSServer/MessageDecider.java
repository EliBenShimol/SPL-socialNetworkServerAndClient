package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

import java.io.*;
import java.util.LinkedList;

public class MessageDecider<T> {
    private String message;
    private Connections<T> connections;
    private int connectionId;
    protected final File filtered= new File("./filtered");
    BufferedReader in;

    {
        try {
            in = new BufferedReader(new FileReader(filtered));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private final LinkedList<String> filteredWords;
    private final BidiMessagingProtocol<T> protocol;
    public MessageDecider(T message, Connections<T> connections, int connectionId, BidiMessagingProtocol<T> protocol){
        this.message=(String)message;
        this.connections=connections;
        this.connectionId=connectionId;
        this.protocol=protocol;
        filteredWords=new LinkedList<String>();
    }

    public T result(){
        String str=null;
        while(true) {
            try {
                str=in.readLine();
                if (str==null) break;
                filteredWords.add(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String opCode="";
        opCode=""+message.charAt(0);
        message=message.substring(1);
        if(opCode.charAt(0)=='1'&&message.charAt(0)=='2') {
            opCode += message.charAt(0);
            message=message.substring(1);
        }
        int code=Integer.parseInt(opCode);
        T ans=messageToSend(code);
        return ans;
    }


    private T messageToSend(int opCode){
        String answer="";
        boolean ans=false;
            if(opCode==1){
                if(!protocol.isConnectedUser()) {
                    int index=0;
                    String name="";
                    String password="";
                    String birthday="";
                    while (message.charAt(index)!='0'){
                        name+=message.charAt(index);
                        index++;
                    }
                    index++;

                    while (index<message.length()-13){
                        password+=message.charAt(index);
                        index++;
                    }
                    index++;
                    while (index<message.length()-2){
                        birthday+=message.charAt(index);
                        index++;
                    }
                    Client newClient = new Client(name, password, birthday, connectionId);
                    ans = connections.addClient(newClient);
                }
            }

        else if(opCode==2){
            int index=0;
            String name="";
            String password="";
            if(!protocol.isConnectedUser()) {
                while (message.charAt(index) != '0') {
                    name += message.charAt(index);
                    index++;
                }
                index++;

                while (message.charAt(index) != '0') {
                    password += message.charAt(index);
                    index++;
                }
                index++;
                char captcha = message.charAt(index);
                ans = connections.login(name, password, captcha);
                if (ans) {
                    Client client = connections.getClient(name);
                    protocol.connectUser(client);
                }
            }
        }

        else if(opCode==3){
            if(protocol.isConnectedUser()){
                ans=true;
                protocol.getUser().disconnect();
                protocol.terminate();
            }
        }


        else if(opCode==4){
            if(protocol.isConnectedUser()) {
                char follow = message.charAt(0);
                String userName = "";
                int index = 1;
                while (message.charAt(index) != '0') {
                    userName += message.charAt(index);
                    index++;
                }
                Client toFollow = connections.getClient(userName);
                if(toFollow!=null) {
                    ans = protocol.follow(toFollow, follow);
                    answer = userName + '0';
                }
            }

        }

        else if(opCode==5){
            if(protocol.isConnectedUser()) {
                String content = "";
                LinkedList<String> names = new LinkedList<String>();
                int index = 0;
                while (message.charAt(index) != ';') {
                    content += message.charAt(index);
                    if (message.charAt(index) == '@') {
                        int tmpIndex = index + 1;
                        String name = "";
                        while (tmpIndex < message.length() - 2 && message.charAt(tmpIndex) != ' ') {
                            name += message.charAt(tmpIndex);
                            tmpIndex++;
                        }
                        names.add(name);
                    }
                    index++;
                }
                ans=true;
                content="91"+protocol.getUser().getName()+'0'+content+';';
                for(int i=0;i<filteredWords.size();i++) {
                    content=content.replace(filteredWords.get(i), "<filtered>");
                }
                protocol.getUser().addMessage(content);
                protocol.getUser().addNumMessage();
                connections.post(protocol.getUser().getName(), (T)content, names);
            }
        }

        else if(opCode==6){
            int index=0;
            String name="";
                while (message.charAt(index)!='0'){
                    name+=message.charAt(index);
                    index++;
                }
                index++;
                String content="";
                while (message.charAt(index)!='0'){
                    content+=message.charAt(index);
                    index++;
                }
                index++;
                String date="";
                while (index<message.length()-2){
                    date+=message.charAt(index);
                    index++;
                }

                content="90"+protocol.getUser().getName()+'0'+content+' '+date+'0'+';';
                for(int i=0;i<filteredWords.size();i++) {
                    content=content.replace(filteredWords.get(i), "<filtered>");
                }
                Client c=connections.getClient(name);
                if(c!=null&&!content.contains("@")&&
                        !c.isBlocking(protocol.getUser().getName())&&!protocol.getUser().isBlocking(name)) {
                    ans=true;
                    protocol.getUser().addMessage(content);
                    if(c.isLogged())
                        connections.send(c.getConnectionId(), (T)content);
                    else {
                        c.addToSend(content);
                    }
                }
            }
        else if(opCode==7){
            if(protocol.isConnectedUser()){
                for(Client c:connections.getClients().values()) {
                    if(!c.isBlocking(protocol.getUser().getName())&&!protocol.getUser().isBlocking(c.getName())) {
                        String ret = "10" + opCode + c.getProperties();
                        connections.send(connectionId, (T) ret);
                    }
                }
                ans=true;
            }
        }
        else if(opCode==8){
            if(protocol.isConnectedUser()){
                int index=0;
                while(index<message.length()-2){
                    String name="";
                    while (index<message.length()-2&&message.charAt(index)!='|'){
                        name+=message.charAt(index);
                        index++;
                    }
                    index++;
                    Client c=connections.getClient(name);
                    if(c!=null) {
                        if (!c.isBlocking(protocol.getUser().getName()) && !protocol.getUser().isBlocking(name)) {
                            ans=true;
                            String ret = "10" + opCode + c.getProperties();
                            connections.send(connectionId, (T) ret);
                        }
                    }
                }
            }
        }

        else if(opCode==12){
            if(protocol.isConnectedUser()) {
                String name = "";
                int index = 0;
                while (index < message.length() - 2) {
                    name += message.charAt(index);
                    index++;
                }
                if(!protocol.getUser().getName().equals(name)) {
                    boolean follow = false;
                    boolean following=false;
                    Client c = connections.getClient(name);
                    if (connections.getClient(name) != null) {
                        follow = protocol.getUser().block(name);
                        following=c.block(protocol.getUser().getName());

                    }
                    if (follow)
                        c.removeFollower();
                    if(following)
                        protocol.getUser().removeFollower();
                    ans = true;
                }
            }
        }


        if(ans) {
            if (opCode!= 7&&opCode!=8)
                answer = "10" + opCode + answer;
            else
                answer=null;

        }

        else
            answer="11"+opCode+answer;
        if(answer!=null)
            answer=answer+';';
        return (T)answer;
    }

}
