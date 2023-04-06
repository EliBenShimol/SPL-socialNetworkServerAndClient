package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.srv.BaseServer;

import java.io.IOException;
import java.util.function.Supplier;

public class TPCMain {


    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("you must supply one arguments: port");
            System.exit(1);
        }

        ConnectionsImpl connections=ConnectionsImpl.getInstance();
        Supplier<BidiMessagingProtocol<Object>> protocolFactory=new Supplier<BidiMessagingProtocol<Object>>() {
            @Override
            public BidiMessagingProtocol<Object> get() {
                Protocol<Object> protocol=new Protocol<Object>();
                protocol.start(connections.getID(), connections);
                return protocol;
            }
        };
        int port=Integer.parseInt(args[0]);
        try(BaseServer server =  BaseServer.threadPerClient(port, protocolFactory,()->new EncoderDecoder());){
            server.serve();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
