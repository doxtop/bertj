package com.synrc.bert;

import fj.F;
import fj.F0;
import fj.F2;
import fj.data.Option;
import fj.data.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

import static fj.data.Option.none;

public class Term {
    Object v;
    Term(Object v) { this.v = v; }

    public Res<String>  str() { return str(Res::ok).orSome(Res.fail(this + " is not a string"));}
    public Res<byte[]>  bin() { return bin(Res::ok).orSome(Res.fail(this + " is not a binary"));}
    public Res<Number> flt() { return flt(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<Integer> in()  { return in(Res::ok).orSome(Res.fail(this + " is not a integer"));}
    public Res<String> atom() { return atom((v,c) -> v).map(Res::ok).orSome(Res.fail(this + " is not an atom"));}
    public Res<BigInteger> big()  { return big(Res::ok).orSome(Res.fail(this + " is not an big"));}

    public static Term str(String str) { return new Str(str); }
    public static Term bin(byte[] bin) { return new Bin(bin); }
    public static Term list(List<Term> l) {return new Array(l.snoc(new Nil()));}
    public static Term flt(Number v) { return new Flt(v);}
    public static Term in(Integer v) { return new Int(v.intValue());}
    public static Term atom(String v, Charset cs) { return new Atom(v, cs); }
    public static Term big(BigInteger v) { return new Big(v); }
    public static Term mp(List<Term> l)  { return new Map(l); }
    
    public <T> Option<T> str(F<String, T> f) { return none(); }
    public <T> Option<T> bin(F<byte[], T> f) { return none(); }
    public <T> Option<T> tup(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> list(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> nil(F0<T> f) { return none(); }
    public <T> Option<T> flt(F<Number,T> f) { return none(); }
    public <T> Option<T> in(F<Integer,T> f) { return none(); }
    public <T> Option<T> atom(F2<String,Charset,T> f) {return none();}
    public <T> Option<T> big(F<BigInteger,T> f) { return none(); }
    public <T> Option<T> mp(F<List<Term>,T> f) { return none(); }
    
    public static final class Map extends Term {
        final List<Term> v;
        public Map(List<Term> v) { super(v); this.v=v; }
        public <T> Option<T> mp(F<List<Term>, T> f) { return Option.some(f.f(v)); }
    }

    public static final class Big extends Term {
        final BigInteger v;
        public Big(BigInteger v) {super(v);this.v=v; }
        public <T> Option<T> big(F<BigInteger,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Atom extends Term {
        final String v;
        final Charset charset;
        public Atom(String v, Charset cs){ super(v);this.v=v; this.charset = cs; }
        public <T> Option<T> atom(F2<String,Charset,T> f) {return Option.some(f.f(v,charset));}
    }

    public static final class Int extends Term {
        final Integer v;
        public Int(int v){super(v);this.v=v; }
        public <T> Option<T> in(F<Integer,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Flt extends Term {
        final Number v;
        public Flt(Number v){ super(v); this.v = v; }
        public <T> Option<T> flt(F<Number,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Bin extends Term {
        final byte[] v;
        public Bin(byte[] v) { super(v);this.v=v;  }

        public <T> Option<T> bin(F<byte[], T> f) { return Option.some(f.f(v)); }
    }

    public static final class Str extends Term {
        final String v;
        public Str(String v){ super(v); this.v=v; }
        public <T> Option<T> str(F<String, T> f) { return Option.some(f.f(v)); }
    }

    public static class Tuple extends Term {
        final List<Term> v;
        public Tuple(List<Term> v) { super(v); this.v=v; }

        public <T> Option<T> tup(F<List<Term>, T> f) { return Option.some(f.f(v)); }
        public Tuple ins(int index, Term x) { return new Tuple(v.snoc(x));};
    }

    public static class Array extends Term {
        final List<Term>  v;
        public Array(List<Term> v) { super(v); this.v=v; }

        public <T> Option<T> list(F<List<Term>, T> f) { return Option.some(f.f(v)); }
    }

    public static final class Nil extends Array {
        public Nil() { super(List.nil());}
        public <T> Option<T> nil(F0<T> f) { return Option.some(f.f()); }
        public <T> Option<T> list(F<List<Term>, T> f) { return none(); }
    }

}
