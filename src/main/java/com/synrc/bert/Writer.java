package com.synrc.bert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.math.BigDecimal;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Writer {
    final ByteArrayOutputStream os;

    private Writer() {
        this.os = new ByteArrayOutputStream();
        os.write((byte)131);
    }

    private byte[] write_(Term bert) { // keep size in term ?
        return bert.<ByteArrayOutputStream>nil(() -> { os.write(106); return os;} )
            .orElseGet(() -> bert.<ByteArrayOutputStream>in(i -> {
                if (i > 255) {
                    os.write(98);
                    writeInt(i.intValue());
                } else {
                    os.write(97);
                    os.write(i);
                }
                return os;
            }).orElseGet(() -> bert.atom((s,cs) -> {
                final short len = (short)s.length();
                if (len > 255) throw new RuntimeException("atom " + s + " to long");
                // check quotes if uppercase _  @ escape sequences
                final byte[] atom = s.getBytes(cs);
                final int size = atom.length;

                try {
                    if (cs == ISO_8859_1) {
                        // 115 atoms in a wild (erts 5.7.2+) requires to DFLAG_SMALL_ATOM_TAGS in distribution handshake
                        os.write(100);
                        os.write((byte) (len >> 8));
                        os.write((byte) len);
                        
                    } else if (cs == UTF_8) {
                        if (size > 255) {
                            os.write(118);
                            os.write((byte) (size >> 8));
                            os.write((byte) size);
                        } else {
                            os.write(119);
                            os.write((byte) size);
                        }
                    } else throw new RuntimeException("Unsuported atom encoding.");
                    
                    os.write(atom);
                } catch (IOException e) {
                     System.out.println("atom not encoded:" + e.getMessage());
                }
                return os;
            }).orElseGet(() -> bert.tup(terms -> {
                int arity = terms.length();
                if (arity > 255) {
                    os.write(105);
                    writeInt(arity);
                } else {
                    os.write(104);
                    os.write((byte) arity);
                }
                terms.forEach(t -> write_(t));
                return os;
            }).orElseGet(() -> bert.flt(d -> {
                try {
                    if (d instanceof BigDecimal) {
                        os.write(99);
                        final ByteBuffer buf = ByteBuffer.allocate(31).order(ByteOrder.BIG_ENDIAN).put(String.format("%.20e", d).getBytes(ISO_8859_1));
                        os.write(buf.array());
                    } else {
                        os.write(70);
                        long v = Double.doubleToRawLongBits(d.doubleValue());
                        byte[] ba = new byte[] {(byte) (v >> 56),(byte) (v >> 48),(byte) (v >> 40),(byte) (v >> 32),(byte) (v >> 24),(byte) (v >> 16),(byte) (v >> 8),(byte) v};
                        os.write(ba, 0, 8);
                    }
                } catch (IOException e) {
                     System.out.println("float is not encoded: " + e.getMessage());
                }
                return os;
            }).orElseGet(() -> bert.str(s -> {
                os.write(107);// check 65535 bytes 
                byte[] str = s.getBytes(ISO_8859_1);
                short len = (short)str.length;
                os.write((byte) len >> 8);
                os.write((byte)len);
                try {
                    os.write(str);
                } catch (IOException e) {
                    System.out.println("str not encoded:" + e.getMessage());
                }
                return os;
            }).orElseGet(() -> bert.bin(bin -> {
                os.write(109);
                int len = bin.length;
                writeInt(len);
                os.write(bin, 0, len);
                return os;
            }).orElseGet(() -> bert.list(list -> {
                os.write(108);
                writeInt(list.length() - 1);//nil
                list.forEach(t -> write_(t));
                return os;
            }).orElseGet(() -> bert.big(b -> {
                final byte[] big = b.toByteArray();
                final int len = big.length;
                
                if (len <= 254) {
                    os.write(110);
                    os.write((byte)len);
                } else {
                    os.write(111);
                    writeInt(len);
                }
                int signum = b.signum();
                byte sign = 0;
                if(signum == 1) sign = 0;
                if(signum ==-1) sign = 1;
                os.write(sign);

                for(int i=len-1;i>=0;i--) os.write(big[i]);
                
                return os;
            }).orElseGet(() -> bert.mp(map -> {
                os.write(116);
                int arity = map.length()/2;
                writeInt(arity);

                for(int i=0; i<map.length();i++) {
                    final Term e = map.index(i++);
                    final Term e1 = map.index(i);
                    write_(e1);
                    write_(e);
                }
                return os;
            }).orElse(os))))))))))
            .toByteArray();
    }

    private void writeInt(int i) {
        os.write((byte)(i >> 24));
        os.write((byte)(i >> 16));
        os.write((byte)(i >> 8));
        os.write((byte)i);
    }

    public static Res<byte[]> write(Term bert){
        try {
            return Res.ok(new Writer().write_(bert));
        }catch(Exception e){
            return Res.fail("ioe" + e.getMessage());
        }
    }
}
