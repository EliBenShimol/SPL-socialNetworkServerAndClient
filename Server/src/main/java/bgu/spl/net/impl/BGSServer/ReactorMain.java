package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.srv.Reactor;

import java.io.IOException;
import java.util.function.Supplier;

public class ReactorMain {


        public static void main(String[] args) throws IOException {

            if (args.length != 2) {
                System.out.println("you must supply two arguments: port, num of threads");
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
            int num=Integer.parseInt(args[1]);
            int port=Integer.parseInt(args[0]);
            try(Reactor r=new Reactor(num, port, protocolFactory, ()->new EncoderDecoder<>());){
                r.serve();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


