package com.synrc.bert;

import fj.*;
import fj.data.List;
import fj.data.Either;
import java.nio.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import static com.synrc.bert.Term.*;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;

public class Parser {
    final ByteBuffer buffer;

    private Parser(byte[] bin) {
        this.buffer = ByteBuffer.wrap(bin).order(ByteOrder.BIG_ENDIAN);
    }

    private Term parse() throws IOException, ParseException {
        System.out.println("parse " + Arrays.toString(buffer.array()));
        if (buffer.get() != -125) throw new RuntimeException("BERT?");
        return read();
    }

    private Term read() throws IOException, ParseException {
        switch(buffer.get()) {
            case 70:  return float754();
            case 97:  return bt();
            case 98:  return in();
            case 99:  return floatStr();
            case 100: return atom(buffer.getShort(), ISO_8859_1);
            case 104: return tup();
            case 105: return tupL();
            case 106: return nil();
            case 107: return str();
            case 108: return list();
            case 109: return bin();
            case 110: return big(true);
            case 111: return big(false);
            case 115: return atom(buffer.get(), ISO_8859_1); 
            case 116: return map();
            case 118: return atom(buffer.getShort(), UTF_8);
            case 119: return atom(buffer.get(), UTF_8);
            default: throw new RuntimeException("BERT?");
        }
    }

    public Map map() throws IOException, ParseException {
        int arity = buffer.getInt();
        java.util.Map<Term,Term> map = new HashMap<>();

        for(int i=0;i<arity;i++) map.put(read(),read());

        return new Map(map);
    }

    private Big big(boolean small) {
        int len = small ? buffer.get() : buffer.getInt();
        byte sign = buffer.get();
        int signum = 0;
        if (sign==0) signum = 1;
        if (sign==1) signum = -1;
        byte[] mag = new byte[len];
        byte[] lmag = new byte[len];
        buffer.get(mag);// least significant first 
        for(int i=len-1;i>=0;i--) lmag[len-i-1]=mag[i];        
        // probably something custom to store, but slow and big integer for now
        BigInteger bi = new BigInteger(signum,lmag);// most significant first
        return new Big(bi);
    }

    private Atom atom(int len, Charset cs) {
        byte[] atom = new byte[len];
        buffer.get(atom);
        return new Atom(new String(atom, cs), cs);
    }

    private In in() {
        return new In(buffer.getInt());
    }

    private FloatStr floatStr() throws ParseException {
        byte[] fs = new byte[31];
        buffer.get(fs);
        String s = new String(fs, ISO_8859_1);
        BigDecimal bd = BigDecimal.ZERO;
        DecimalFormat fmt = new DecimalFormat();
        fmt.setParseBigDecimal(true);
        bd = (BigDecimal) fmt.parse(s);
        return new FloatStr(bd);
    }

    private Fload754 float754() throws IOException {
        return new Fload754(BigDecimal.valueOf(buffer.getDouble()));
    }
    private Bt bt() {
        return new Bt(buffer.get());
    }

    private Tuple tup() throws IOException, ParseException {
        final int arity = buffer.get();
        List<Term> vs = List.nil();

        for (int i=0;i<arity;i++) vs = vs.cons(read());

        return new Tuple(vs.reverse());
    }

    private Tuple tupL() throws IOException, ParseException {
        final int arity = buffer.getInt();
        List<Term> vs = List.nil();

        for (int i=0;i<arity;i++) vs = vs.cons(read());

        return new TupleL(vs.reverse());
    }

    private Array nil() { return new Array(List.nil()); }

    private Array list() throws IOException, ParseException {
        final int length = buffer.getInt();
        List<Term> list = List.nil();

        for (int i = 0; i < length; i++) {
            list = list.cons(read());
        }

        final Array tail = (Array) read();
        if (!tail.v.isEmpty()) {
            //list = list.cons(new Nil());
            list = list.cons(tail); // Nil
        }
        return new Array(list.reverse());
    }

    private Bin bin() {
        byte[] bin = new byte[buffer.getInt()];
        buffer.get(bin);
        return new Bin(bin);
    }

    private Str str() {
        byte[] string = new byte[buffer.getShort()];
        buffer.get(string);
        return new Str(new String(string, ISO_8859_1));
    }

    public static Res<Term> parse(byte[] bin) {
        try {
            return Res.ok(new Parser(bin).parse());
        } catch (Exception e) {
            return Res.fail("Parsing failed: " + e.getMessage());
        }
    }
}
