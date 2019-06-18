package com.synrc.bert;

import fj.*;
import fj.data.*;
import static fj.data.Option.none;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.stream.*;

public class Term {
    Object v;
    Term(Object v) { this.v = v; }

    public Res<String>  str() { return str(Res::ok).orSome(Res.fail(this + " is not a string"));}
    public Res<byte[]>  bin() { return bin(Res::ok).orSome(Res.fail(this + " is not a binary"));}
    public Res<Double>  float754() { return float754(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<BigDecimal>  floatStr() { return floatStr(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<Integer> in()  { return in(Res::ok).orSome(Res.fail(this + " is not a integer"));}
    public Res<String> atom() { return atom((v,c) -> v).map(Res::ok).orSome(Res.fail(this + " is not an atom"));}
    
    public Res<BigInteger> big()  { return big(Res::ok).orSome(Res.fail(this + " is not an big"));}

    public static Term str(String str) { return new Str(str); }
    public static Term bin(byte[] bin) { return new Bin(bin); }
    public static Term list(List<Term> l) {return new Array(l.snoc(new Nil()));}
    public static Term float754(double v) { return new Fload754(v);}
    public static Term floatStr(BigDecimal v) { return new FloatStr(v);}
    public static Term in(Integer v) { return new Int(v.intValue());}
    public static Term atom(String v, Charset cs) { return new Atom(v, cs); }
    public static Term big(BigInteger v){ return new Big(v);}
    public static Term mp(List<Term> l) {
        List<Term> keys = List.nil();
        List<Term> vals = List.nil();
        for (int i=0;i<l.length();i++) {
            if (i % 2 == 0) {
                keys = keys.cons(l.index(i));
            } else {
                vals = vals.cons(l.index(i));
            }
        }
        return new Map(HashMap.iterableHashMap(keys.zip(vals)).toMap());
    }
    
    public <T> Option<T> str(F<String, T> f) { return none(); }
    public <T> Option<T> bin(F<byte[], T> f) { return none(); }
    public <T> Option<T> tup(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> tupL(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> list(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> nil(F0<T> f) { return none(); }
    public <T> Option<T> float754(F<Double,T> f) { return none(); }
    public <T> Option<T> floatStr(F<BigDecimal,T> f) { return none(); }
    public <T> Option<T> in(F<Integer,T> f) { return none(); }
    public <T> Option<T> atom(F2<String,Charset,T> f) {return none();}
    public <T> Option<T> big(F<BigInteger,T> f) { return none(); }
    public <T> Option<T> mp(F<List<Term>,T> f) { return none(); }
    
    public static final class Map extends Term {
        final java.util.Map<Term,Term> v;
        public Map(java.util.Map<Term,Term> v) { super(v); this.v=v; }
        public <T> Option<T> mp(F<List<Term>, T> f) {
            final List<Term> keys = List.iterableList(v.keySet());
            final List<Term> vals = List.iterableList(v.values());
            List<List<Term>> terms = keys.zipWith(vals, (t,s) -> List.list(t,s));
            List<Term> map = List.<Term>join().f(terms);
            return Option.some(f.f(map)); 
        }
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

    public static final class Fload754 extends Term {
        final double v;
        public Fload754(double v) { super(v); this.v=v; }
        public <T> Option<T> float754(F<Double,T> f) { return Option.some(f.f(v)); }
    }

    public static final class FloatStr extends Term {
        final BigDecimal v;
        public FloatStr(BigDecimal v) { super(v); this.v=v; }
        public <T> Option<T> floatStr(F<BigDecimal,T> f) { return Option.some(f.f(v)); }
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

        @Override public String toString() {return v.toString();}
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
