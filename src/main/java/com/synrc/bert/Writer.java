package com.synrc.bert;

import java.io.*;

public class Writer {
    ByteArrayOutputStream os; //ByteBuffer or byte[]

    private Writer(){
        this.os = new ByteArrayOutputStream();
        os.write(-125);
    }

    private byte[] write_(Term bert) {
        return bert.nil(() -> {os.write(106);return os;})
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
