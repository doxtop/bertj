package com.synrc.bert;

import fj.*;
import fj.data.*;
import static fj.data.Option.none;
import java.math.BigDecimal;

abstract class Term {
    private Term() {}

    public Res<String>  str() { return str(Res::ok).orSome(Res.fail(this + " is not a string"));}
    public Res<byte[]>  bin() { return bin(Res::ok).orSome(Res.fail(this + " is not a binary"));}
    public Res<BigDecimal>  float754() { return float754(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<BigDecimal>  floatStr() { return floatStr(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<Byte>    bt()  { return bt(Res::ok).orSome(Res.fail(this + " is not a byte"));}
    public Res<Integer> in()  { return in(Res::ok).orSome(Res.fail(this + " is not a integer"));}
    public Res<String> atom()  { return atom(Res::ok).orSome(Res.fail(this + " is not an atom"));}

    public static Term str(String str) { return new Str(str); }
    public static Term bin(byte[] bin) { return new Bin(bin); }
    public static Term list(List<Term> l) {return new Array(l.snoc(new Nil()));}
    public static Term float754(BigDecimal v) { return new Fload754(v);}
    public static Term floatStr(BigDecimal v) { return new FloatStr(v);}
    public static Term bt(Byte v) { return new Bt(v.byteValue());}
    public static Term in(Integer v) { return new In(v.intValue());}
    public static Term atom(String v) { return new Atom(v); }

    public <T> Option<T> str(F<String, T> f) { return none(); }
    public <T> Option<T> bin(F<byte[], T> f) { return none(); }
    public <T> Option<T> tup(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> tupL(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> list(F<List<Term>, T> f) { return none(); }
    public <T> Option<T> nil(F0<T> f) { return none(); }
    public <T> Option<T> float754(F<BigDecimal,T> f) { return none(); }
    public <T> Option<T> floatStr(F<BigDecimal,T> f) { return none(); }
    public <T> Option<T> bt(F<Byte,T> f) { return none(); }
    public <T> Option<T> in(F<Integer,T> f) { return none(); }
    public <T> Option<T> atom(F<String,T> f) {return none();}

    public static final class Atom extends Term {
        // latin1 - in minor_version 0,1. 2 in utf8
        final String v;
        public Atom(String v){ this.v=v;}
        public <T> Option<T> atom(F<String,T> f) {return Option.some(f.f(v));}
    }

    public static final class In extends Term {
        final int v;
        public In(int v){this.v = v;}
        public <T> Option<T> in(F<Integer,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Bt extends Term {
        final byte v;
        public Bt(byte v) {  this.v = v; }
        public <T> Option<T> bt(F<Byte,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Fload754 extends Term {
        final BigDecimal v;
        public Fload754(BigDecimal v) {  this.v = v; }
        public <T> Option<T> float754(F<BigDecimal,T> f) { return Option.some(f.f(v)); }
    }

    public static final class FloatStr extends Term {
        final BigDecimal v;
        public FloatStr(BigDecimal v) { this.v = v; }
        public <T> Option<T> floatStr(F<BigDecimal,T> f) { return Option.some(f.f(v)); }
    }

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

    public static class Tuple extends Term {
        final List<Term> v;
        public Tuple(List<Term> v) { this.v = v; }

        public <T> Option<T> tup(F<List<Term>, T> f) { return Option.some(f.f(v)); }
        public <T> Option<T> tupL(F<List<Term>, T> f) { return none(); }
        public Tuple ins(int index, Term x) { return new Tuple(v.snoc(x));};

        @Override public String toString() {return v.toString();}
    }
    
    public static final class TupleL extends Tuple{
        public TupleL(List<Term> v){ super(v); }
        public <T> Option<T> tup(F<List<Term>, T> f) { return none(); }
        public <T> Option<T> tupL(F<List<Term>, T> f) { return Option.some(f.f(v)); }
        public TupleL ins(int index, Term x) { return new TupleL(v.snoc(x));};
        @Override public String toString() { return "L->" + v.toString();}
    }

    public static final class Array extends Term {
        public final List<Term> xs;

        public Array(List<Term> xs) { this.xs = xs; }

        public <T> Option<T> list(F<List<Term>, T> f) { return Option.some(f.f(xs)); }
    }

    public static final class Nil extends Term {
        public <T> Option<T> nil(F0<T> f) { return Option.some(f.f()); }
    }

}
