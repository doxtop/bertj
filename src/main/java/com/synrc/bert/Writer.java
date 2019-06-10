package com.synrc.bert;

import java.io.*;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Writer {
    ByteArrayOutputStream os; //ByteBuffer or byte[]

    private Writer(){
        this.os = new ByteArrayOutputStream();
        os.write(-125);
    }

    private byte[] write_(Term bert){
        return bert.nil(() -> {os.write(106);return os;})
            .orElse( bert.str(s -> {
                os.write(107);// check 65535 bytes 
                byte[] str = s.getBytes(ISO_8859_1);
                short len = (short)str.length;
                os.write((byte) len >> 8);
                os.write((byte)len);
                try {
                    os.write(str);
                } catch (IOException e){
                    System.out.println("str not encoded:" + e.getMessage());                    
                }
                return os;
            }))
            .orElse(bert.tup(terms -> {
                System.out.println("write terms" + terms);
                os.write(104);
                os.write((byte)terms.length());
                terms.foreachDoEffect(t -> write_(t));
                return os;
            }))
            .map(os -> os.toByteArray())
            .orSome(() -> os.toByteArray());
    }

    public static Res<byte[]> write(Term bert){
        try {
            return Res.ok(new Writer().write_(bert));
        }catch(Exception e){
            return Res.fail("ioe" + e.getMessage());
        }
    }
}
