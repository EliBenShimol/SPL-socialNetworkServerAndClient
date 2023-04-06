package bgu.spl.net.impl.BGSServer;

import java.util.LinkedList;

public class Client {
    private String name;
    private String password;
    private String birthday;
    private int age;
    private int followers;
    private int publicMessagesSent;
    private int connectionId;
    private boolean isLogged;
    private final LinkedList<String> blocked;
    private final LinkedList<String> follows;
    private final LinkedList<String> messages;
    private final LinkedList<String>mTOSend;
    public Client(String name, String password, String birthday, int connectionId){
        this.name=name;
        this.password=password;
        this.birthday=birthday;
        this.connectionId=connectionId;
        blocked=new LinkedList<String>();
        follows =new LinkedList<String>();
        messages=new LinkedList<String>();
        mTOSend=new LinkedList<String>();
        isLogged=false;
        int count=0;
        int index=0;
        while(count<2){
            if(birthday.charAt(index)=='-')
                count++;
            index++;
        }
        String strYear="";
        while (index<birthday.length()) {
            strYear += birthday.charAt(index);
            index++;
        }
        int year=Integer.parseInt(strYear);
        age=2022-year;
    }

    public int getConnectionId(){
        return connectionId;
    }
    public void setConnectionId(int connectionId){this.connectionId=connectionId;}
    public boolean isLogged(){
        return isLogged;
    }

    public boolean follow(Client called, char follow){
        boolean ans=false;
        if(!name.equals(called.getName())) {
            if (follow == '0') {
                if (!follows.contains(called.getName())) {
                    called.addFollower();
                    follows.add(called.getName());
                    ans = true;
                }
            } else if (follow == '1') {
                if (follows.contains(called.getName())) {
                    called.removeFollower();
                    follows.remove(called.getName());
                    ans = true;
                }
            }
        }
        return ans;
    }

    public boolean log(String pass, char captcha){
        boolean ans=false;
        if(!isLogged&&password.equals(pass)&&captcha=='1') {
            isLogged = true;
            ans=true;
        }

        return ans;
    }


    public boolean doesFollow(String name){
        if(follows.contains(name))
            return true;
        return false;
    }

    public void disconnect(){
        isLogged=false;
    }

    public String getName(){
        return name;
    }
    public void addFollower(){followers++;}
    public void removeFollower(){followers--;}
    public void addNumMessage(){publicMessagesSent++;}
    public void addMessage(String message){messages.add(message);}

    public String getProperties(){
        String ret=age+","+publicMessagesSent+","+followers+","+follows.size()+'0'+";";
        return ret;
    }

    public boolean block(String name){
        boolean follow=false;
        if(follows.contains(name)) {
            follows.remove(name);
            follow=true;
        }
        blocked.add(name);
        return follow;
    }
    public boolean isBlocking(String name){
        boolean ans=false;
        if(blocked.contains(name))
            ans=true;
        return ans;
    }

    public void addToSend(String message){
        mTOSend.add(message);
    }

    public LinkedList<String> getmTOSend(){
        LinkedList<String> ans=new LinkedList<String>();
        for(int i=0;i<mTOSend.size();i++)
            ans.add(mTOSend.get(i));
        while (mTOSend.size()>0)
            mTOSend.remove();
        return ans;
    }


}
