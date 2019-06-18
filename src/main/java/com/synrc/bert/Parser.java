package com.synrc.bert;

import fj.data.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static com.synrc.bert.Term.*;

public class Parser {

    final static DecimalFormat fmt = new DecimalFormat();
    
    { fmt.setParseBigDecimal(true); }
    
    final ByteBuffer buffer;

    private Parser(byte[] bin) {
        this.buffer = ByteBuffer.wrap(bin).order(ByteOrder.BIG_ENDIAN);
    }

    private Term parse() throws IOException, ParseException {
        if ((buffer.get()& 0xff) != 131) throw new RuntimeException("BERT?");
        return read();
    }

    private Term read() throws IOException, ParseException {
        switch(buffer.get() & 0xff) {
            case 70:  return new Flt(Double.valueOf(buffer.getDouble()));
            case 97:  return new Int(buffer.get() & 0xff);
            case 98:  return new Int(buffer.getInt());
            case 99:  byte[] d = new byte[31]; buffer.get(d); return new Flt(fmt.parse(new String(d, ISO_8859_1)));
            case 100: return atom(buffer.getShort(), ISO_8859_1);
            case 104: return tup(buffer.get() & 0xff);
            case 105: return tup(buffer.getInt());
            case 106: return new Nil();
            case 107: byte[] s = new byte[buffer.getShort()];buffer.get(s); return new Str(new String(s, ISO_8859_1));
            case 108: return list();
            case 109: byte[] bi = new byte[buffer.getInt()];buffer.get(bi); return new Bin(bi);
            case 110: return big(buffer.get() & 0xff);
            case 111: return big(buffer.getInt());
            case 115: return atom(buffer.get() & 0xff, ISO_8859_1); 
            case 116: return map();
            case 118: return atom(buffer.getShort(), UTF_8);
            case 119: return atom(buffer.get() & 0xff, UTF_8);
            default: throw new RuntimeException("BERT?");
        }
    }

    private Atom atom(int len, Charset cs) {
        byte[] atom = new byte[len];
        buffer.get(atom);
        return new Atom(new String(atom, cs), cs);
    }

    public Map map() throws IOException, ParseException {
        int arity = buffer.getInt();
        List<Term> map = List.nil();

        for(int i=0;i<arity;i++) map = map.cons(read()).cons(read());

        return new Map(map);
    }

    private Big big(int len) {
        byte sign = buffer.get();
        int signum = 0;
        if (sign==0) signum = 1;
        if (sign==1) signum = -1;
        byte[] mag = new byte[len];
        byte[] lmag = new byte[len];
        buffer.get(mag);
        for(int i=len-1;i>=0;i--) lmag[len-i-1]=mag[i];
        
        return new Big(new BigInteger(signum,lmag));
    }

    private Tuple tup(int arity) throws IOException, ParseException {
        List<Term> vs = List.nil();

        for (int i=0;i<arity;i++) vs = vs.cons(read());

        return new Tuple(vs.reverse());
    }
    
    private Array list() throws IOException, ParseException {
        final int length = buffer.getInt();
        List<Term> list = List.nil();

        for (int i = 0; i < length; i++) list = list.cons(read());
        
        final Array tail = (Array) read();
        if (!tail.v.isEmpty()) {
            list = list.cons(tail);
        }
        return new Array(list.reverse());
    }

    public static Res<Term> parse(byte[] bin) {
        try {
            return Res.ok(new Parser(bin).parse());
        } catch (Exception e) {
            return Res.fail("Parsing failed: " + e.getMessage());
        }
    }
}
