package com.synrc.bert;

import java.io.*;
import java.nio.*;
import java.util.Arrays;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Writer {
    ByteArrayOutputStream os; //ByteBuffer or byte[]

    private Writer(){
        this.os = new ByteArrayOutputStream();
        os.write(-125);
    }

    private byte[] write_(Term bert){
        return bert.nil(() -> {os.write(106);return os;})
            .orElse( bert.bt(b -> {
                os.write(97);
                os.write(b);
                return os;
            }))
            .orElse( bert.in(i -> {
                os.write(98);
                int v = i.intValue();
                os.write(new byte[] {
                     (byte) (v>>24),
                     (byte) (v>>16),
                     (byte) (v>> 8),
                     (byte) v
                 }, 0,4);
                return os;
            }))
            .orElse( bert.floatStr(d -> {
                os.write(99);
                final String s = String.format("%.20e", d);
                byte[] fl = s.getBytes(ISO_8859_1);
                ByteBuffer buf = ByteBuffer.allocate(31).order(ByteOrder.BIG_ENDIAN);
                buf.put(fl);
                try {
                     os.write(buf.array());
                } catch(IOException e) {
                     System.out.println("float is not encoded: " + e.getMessage());
                }
                return os;
            }))
            .orElse( bert.float754(d -> {
                long v = Double.doubleToRawLongBits(d);
                os.write(70);
                byte[] ba = new byte[] {
                    (byte) (v >> 56),
                    (byte) (v >> 48),
                    (byte) (v >> 40),
                    (byte) (v >> 32),
                    (byte) (v >> 24),
                    (byte) (v >> 16),
                    (byte) (v >> 8),
                    (byte) v
                };
                os.write(ba, 0, 8);
                return os;
            }))
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
            .orElse(bert.bin(bin -> {
                os.write(109);
                int len = bin.length;
                os.write((byte)len >> 24);
                os.write((byte)len >> 16);
                os.write((byte)len >> 8);
                os.write((byte)len);
                os.write(bin, 0, len);
                return os;
            })) 
            .orElse(bert.tup(terms -> {
                os.write(104);
                os.write((byte)terms.length());
                terms.foreachDoEffect(t -> write_(t));
                return os;
            }))
            .orElse(bert.list(list -> {
                os.write(108);
                int len = list.length()-1;
                os.write((byte)len >> 24);
                os.write((byte)len >> 16);
                os.write((byte)len >> 8);
                os.write((byte)len);
                list.foreachDoEffect(t -> write_(t));
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
