package com.synrc.bert;

import fj.*;
import fj.data.List;
import fj.data.Either;
import java.nio.*;
import java.io.IOException;
import java.util.Arrays;
import static com.synrc.bert.Term.*;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Parser {
    final ByteBuffer buffer;

    private Parser(byte[] bin) {
        this.buffer = ByteBuffer.wrap(bin).order(ByteOrder.BIG_ENDIAN);
    }

    private Term parse() throws IOException {
        System.out.println("parse " + Arrays.toString(buffer.array()));
        if (buffer.get() != -125) throw new RuntimeException("BERT?");
        return read();
    }

    private Term read() throws IOException {
        switch(buffer.get()) {
            case 70:  return flt();
            case 104: return tup();
            case 106: return nil();
            case 107: return str();
            case 108: return list();
            case 109: return bin();
            default: throw new RuntimeException("BERT?");
        }
    }

    private Flt flt() throws IOException {
        return new Flt(buffer.getDouble());
    }

    private Tuple tup() throws IOException {
        final int arity = buffer.get();
        List<Term> vs = List.nil();

        for (int i=0;i<arity;i++) vs = vs.cons(read());

        return new Tuple(vs.reverse());
    }

    private Array nil() { return new Array(List.nil()); }

    private Array list() throws IOException {
        final int length = buffer.getInt();
        List<Term> list = List.nil();

        for (int i = 0; i < length; i++) {
            list = list.cons(read());
        }

        final Array tail = (Array) read();
        if (!tail.xs.isEmpty()) {
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
