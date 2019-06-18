package com.synrc.bert;

import fj.F;
import fj.F0;
import fj.F2;
import fj.data.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@SuppressWarnings("unchecked")
public class Term {
    final Object v;
    private Term(Object v) { this.v = v; }

    public Res<String>  str()   { return str(Res::ok).orElse(Res.fail(this + " is not a string")); }
    public Res<byte[]> bin()    { return bin(Res::ok).orElse(Res.fail(this + " is not a binary")); }
    public Res<Number> flt()    { return flt(Res::ok).orElse(Res.fail(this + " is not a float")); }
    public Res<Integer> in()    { return in(Res::ok).orElse(Res.fail(this + " is not a integer")); }
    public Res<String> atom()   { return atom((v,c) -> v).map(Res::ok).orElse(Res.fail(this + " is not an atom")); }
    public Res<BigInteger> big(){ return big(Res::ok).orElse(Res.fail(this + " is not an big")); }

    public static Term str(String str)   { return new Str(str); }
    public static Term bin(byte[] bin)   { return new Bin(bin); }
    public static Term list(List<Term> l){ return new Array(l.snoc(new Nil()));}
    public static Term flt(Number v)     { return new Flt(v);}
    public static Term in(Integer v)     { return new Int(v.intValue());}
    public static Term atom(String v, Charset cs) { return new Atom(v, cs); }
    public static Term big(BigInteger v) { return new Big(v); }
    public static Term mp(List<Term> l)  { return new Map(l); }
    
    // switch on t
    public <T> Optional<T> str(F<String, T> f)          { return empty(); }
    public <T> Optional<T> bin(F<byte[], T> f)          { return empty(); }
    public <T> Optional<T> tup(F<List<Term>, T> f)      { return empty(); }
    public <T> Optional<T> list(F<List<Term>, T> f)     { return empty(); }
    public <T> Optional<T> nil(F0<T> f)                 { return empty(); }
    public <T> Optional<T> flt(F<Number,T> f)           { return empty(); }
    public <T> Optional<T> in(F<Integer,T> f)           { return empty(); }
    public <T> Optional<T> atom(F2<String,Charset,T> f) { return empty(); }
    public <T> Optional<T> big(F<BigInteger,T> f)       { return empty(); }
    public <T> Optional<T> mp(F<List<Term>,T> f)        { return empty(); }
    
    public static final class Map extends Term {
        public Map(List<Term> v) { super(v); }
        public <T> Optional<T> mp(F<List<Term>, T> f) { return ofNullable(f.f((List<Term>)v)); }
    }

    public static final class Big extends Term {
        public Big(BigInteger v) { super(v); }
        public <T> Optional<T> big(F<BigInteger,T> f) { return ofNullable(f.f((BigInteger)v)); }
    }

    public static final class Atom extends Term {
        final Charset charset;
        public Atom(String v, Charset cs){ super(v); this.charset = cs; }
        public <T> Optional<T> atom(F2<String,Charset,T> f) {return ofNullable(f.f((String)v,charset));}
    }

    public static final class Int extends Term {
        public Int(int v){ super(v); }
        public <T> Optional<T> in(F<Integer,T> f) { return ofNullable(f.f((Integer)v)); }
    }

    public static final class Flt extends Term {
        public Flt(Number v){ super(v); }
        public <T> Optional<T> flt(F<Number,T> f) { return ofNullable(f.f((Number)v)); }
    }

    public static final class Bin extends Term {
        public Bin(byte[] v){ super(v); }
        public <T> Optional<T> bin(F<byte[], T> f) { return ofNullable(f.f((byte[])v)); }
    }

    public static final class Str extends Term {
        public Str(String v){ super(v); }
        public <T> Optional<T> str(F<String, T> f) { return ofNullable(f.f((String)v)); }
    }

    public static class Tuple extends Term {
        public Tuple(List<Term> v) { super(v); }
        public <T> Optional<T> tup(F<List<Term>, T> f) { return ofNullable(f.f((List<Term>)v)); }
        public Tuple ins(int index, Term x) { return new Tuple(((List<Term>)v).snoc(x));};
    }

    public static class Array extends Term {
        public Array(List<Term> v) { super(v); }
        public <T> Optional<T> list(F<List<Term>, T> f) { return ofNullable(f.f((List<Term>)v)); }
    }

    public static final class Nil extends Array {
        public Nil() { super(List.nil());}
        public <T> Optional<T> nil(F0<T> f) { return ofNullable(f.f()); }
        public <T> Optional<T> list(F<List<Term>, T> f) { return empty(); }
    }

}
