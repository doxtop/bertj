package com.synrc.bert;

import fj.*;
import fj.data.*;
import static fj.data.Option.none;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class Term<T> {
    final T v;
    Term(T v) { this.v = v; }

    public Res<String>  str() { return str(Res::ok).orSome(Res.fail(this + " is not a string"));}
    public Res<byte[]>  bin() { return bin(Res::ok).orSome(Res.fail(this + " is not a binary"));}
    public Res<BigDecimal>  float754() { return float754(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<BigDecimal>  floatStr() { return floatStr(Res::ok).orSome(Res.fail(this + " is not a float"));}
    public Res<Byte>    bt()  { return bt(Res::ok).orSome(Res.fail(this + " is not a byte"));}
    public Res<Integer> in()  { return in(Res::ok).orSome(Res.fail(this + " is not a integer"));}
    public Res<String> atom()  { return atom(Res::ok).orSome(Res.fail(this + " is not an atom"));}
    public Res<BigInteger> big()  { return big(Res::ok).orSome(Res.fail(this + " is not an big"));}
    public Res<Object> obj() {return obj(Res::ok).orSome(Res.fail(this + " is not an object."));}

    public static Term str(String str) { return new Str(str); }
    public static Term bin(byte[] bin) { return new Bin(bin); }
    public static Term list(List<Term> l) {return new Array(l.snoc(new Nil()));}
    public static Term float754(BigDecimal v) { return new Fload754(v);}
    public static Term floatStr(BigDecimal v) { return new FloatStr(v);}
    public static Term bt(Byte v) { return new Bt(v.byteValue());}
    public static Term in(Integer v) { return new In(v.intValue());}
    public static Term atom(String v) { return new Atom(v); }
    public static Term big(BigInteger v){ return new Big(v);}
    public static Term obj(Object v) { return new Term<Object>(v); }

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
    public <T> Option<T> big(F<BigInteger,T> f) { return none(); }
    public <T> Option<T> map(F<java.util.Map<Term,Term>,T> f) { return none(); }
    public <T> Option<T> obj(F<Object,T> f) { return none(); }

    public static final class Map extends Term<java.util.Map<Term,Term>> {
        public Map(java.util.Map<Term,Term> v) { super(v); }
        public <T> Option<T> map(F<java.util.Map<Term,Term>,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Big extends Term<BigInteger> {
        public Big(BigInteger v) {super(v);}
        public <T> Option<T> big(F<BigInteger,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Atom extends Term<String> {
        private Charset charset;// latin1 - in minor_version 0,1. 2 in utf8
        public Atom(String v){ super(v);}
        public <T> Option<T> atom(F<String,T> f) {return Option.some(f.f(v));}
    }

    public static final class In extends Term<Integer> {
        public In(int v){super(v);}
        public <T> Option<T> in(F<Integer,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Bt extends Term<Byte> {
        public Bt(byte v) { super(v); }
        public <T> Option<T> bt(F<Byte,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Fload754 extends Term<BigDecimal> {
        public Fload754(BigDecimal v) { super(v); }
        public <T> Option<T> float754(F<BigDecimal,T> f) { return Option.some(f.f(v)); }
    }

    public static final class FloatStr extends Term<BigDecimal> {
        public FloatStr(BigDecimal v) { super(v); }
        public <T> Option<T> floatStr(F<BigDecimal,T> f) { return Option.some(f.f(v)); }
    }

    public static final class Bin extends Term<byte[]> {
        public Bin(byte[] v) { super(v); }

        public <T> Option<T> bin(F<byte[], T> f) { return Option.some(f.f(v)); }
    }

    public static final class Str extends Term<String> {
        public Str(String v){ super(v); }
        public <T> Option<T> str(F<String, T> f) { return Option.some(f.f(v)); }
    }

    public static class Tuple extends Term<List<Term>> {
        public Tuple(List<Term> v) { super(v); }

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

    public static final class Array extends Term<List<Term>> {
        public Array(List<Term> v) { super(v); }

        public <T> Option<T> list(F<List<Term>, T> f) { return Option.some(f.f(v)); }
    }

    public static final class Nil extends Term<List<Term>> {
        public Nil() { super(List.nil());} 
        public <T> Option<T> nil(F0<T> f) { return Option.some(f.f()); }
    }

}
