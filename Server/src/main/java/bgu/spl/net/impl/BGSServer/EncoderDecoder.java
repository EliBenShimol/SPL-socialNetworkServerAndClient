package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class EncoderDecoder<T> implements MessageEncoderDecoder<T> {
    
    String message;
    public EncoderDecoder(){
        message="";
    }
    @Override
    public T decodeNextByte(byte nextByte) {
        char c=(char)(nextByte & 0XFF);
        message=message+c;
        if(c==';') {
            String ans=message;
            message="";
            return (T)ans;
        }
        return null;
    }

    @Override
    public byte[] encode(T message) {
        byte[] data = null;
        try {
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            ObjectOutputStream obj=new ObjectOutputStream(stream);
            obj.writeObject(message);
            obj.flush();
            obj.close();
            stream.close();
            data=stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
