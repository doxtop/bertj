package com.synrc.bert;

import fj.*;
import fj.data.*;
import static fj.data.Option.none;

abstract class Term {
    private Term() {}

    public Res<String> str() { return str(Res::ok).orSome(Res.fail(this + " is not a string"));}
    public Res<byte[]> bin() { return bin(Res::ok).orSome(Res.fail(this + " is not a binary"));}

    public <T> Option<T> str(F<String, T> f) { return none(); }
    public <T> Option<T> bin(F<byte[], T> f) { return none(); }
    public <T> Option<T> tup(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> list(F<List<Term>, T> f) { return none(); }

    public static final class Bin extends Term {
        final byte[] v;
        public Bin(byte[] v) { this.v = v; }

        public <T> Option<T> bin(F<byte[], T> f) { return Option.some(f.f(v)); }
    }

    public static final class Str extends Term {
        final String v;
        public Str(String v){ this.v = v; }

        public <T> Option<T> str(F<String, T> f) { return Option.some(f.f(v)); }
    }

    public static final class Tuple extends Term {
        final fj.data.Array<Term> v;
        public Tuple(List<Term> v) { this.v = v.toArray(); }

        public <T> Option<T> tup(F<List<Term>, T> f) { return Option.some(f.f(v.toList())); }
    }

    public static final class Array extends Term {
        public final List<Term> xs;

        public Array(List<Term> xs) { this.xs = xs; }

        public <T> Option<T> list(F<List<Term>, T> f) { return Option.some(f.f(xs)); }
    }

}
