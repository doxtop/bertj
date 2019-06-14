package com.synrc.bert;

import fj.*;
import static fj.P.p;
import fj.data.List;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface Enc<T> extends F<T, Term> {
    Term encode(T v);

    @Override default Term f(T v) { return encode(v); }

    default <S> Enc<S> contramap(F<S,T> f){
        return v -> this.encode(f.f(v));
    }

    public static Enc<String>   stringEnc = Term::str;
    public static Enc<byte[]>   binEnc  = Term::bin;
    public static Enc<BigDecimal>   floatEnc = Term::float754;
    public static Enc<BigDecimal>   floatStrEnc = Term::floatStr;
    public static Enc<Byte>     byteEnc = Term::bt;
    public static Enc<Integer>  intEnc = Term::in;
    public static Enc<String>   atomEnc = Term::atom;
    public static Enc<BigInteger> bigEnc = Term::big;
    public static Enc<Object> objEnc = Term::obj;

    public static <T> Enc<List<T>> liste(Enc<T> enc) {
        return list -> Term.list(list.map(enc::encode));
    }

    public interface ElementEnc<T> {
        Term.Tuple apply(Term.Tuple tuple, T v);
    }

    static <T, S> ElementEnc<P2<T, S>> and(ElementEnc<T> ft, ElementEnc<S> fs) {
        return (tuple, t) -> fs.apply(ft.apply(tuple, t._1()), t._2());
    }

    // term_to_binary will not produce large tuple for small one, quick shit here
    public static <T> Enc<T> tupleel(ElementEnc<T> e) {
        System.out.println("encode tuple l");
        return x -> e.apply(new Term.TupleL(List.nil()), x);
    }
    public static <T,S> Enc<S> tupleel(ElementEnc<T> e, F<S,T> f){
        System.out.println("map f on tuple l");
        return tupleel(e).contramap(f);
    }

    public static <T> Enc<T> tuplee(ElementEnc<T> e){
        return x -> e.apply(new Term.Tuple(List.nil()), x);
    }

    public static <T,S> Enc<S> tuplee(ElementEnc<T> e, F<S,T> f){
        return tuplee(e).contramap(f);
    }

    public static <T, S> Enc<P2<T, S>> tuplee(ElementEnc<T> t, ElementEnc<S> s) {
        return p -> and(t, s).apply(new Term.Tuple(List.nil()), p);
    }

    public static <T,S,U> Enc<U> tuplee(ElementEnc<T> t, ElementEnc<S> s, F<U, P2<T,S>> f) {
        return tuplee(t,s).contramap(f);
    }
    
    public static <T,S,U> Enc<P3<T,S,U>> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u){
        return p -> and(t, and(s,u)).apply(new Term.Tuple(List.nil()), Enc.ext(p));
    }

    public static <T,S,U,V> Enc<V> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, F<V, P3<T,S,U>> f) {
        return tuplee(t,s,u).contramap(f);
    }

    public static <T,S,U,V> Enc<P4<T,S,U,V>> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v) {
        return o -> and(t, and(s, and(u,v))).apply(new Term.Tuple(List.nil()), Enc.ext(o));
    }

    public static <T,S,U,V,W> Enc<W> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, F<W, P4<T,S,U,V>> f) {
        return tuplee(t,s,u,v).contramap(f);
    }

    public static <T,S,U,V,W> Enc<P5<T,S,U,V,W>> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w) {
        return o -> and(t, and(s, and(u,and(v,w)))).apply(new Term.Tuple(List.nil()), Enc.ext(o));
    }

    public static <T,S,U,V,W,X> Enc<X> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, F<X, P5<T,S,U,V,W>> f) {
        return tuplee(t,s,u,v,w).contramap(f);
    }

    public static <T,S,U,V,W,X> Enc<P6<T,S,U,V,W,X>> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, ElementEnc<X> x){
        return o -> and(t, and(s, and(u, and(v, and(w, x))))).apply(new Term.Tuple(List.nil()), Enc.ext(o));
    }

    public static <T,S,U,V,W,X,Y> Enc<Y> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, ElementEnc<X> x, F<Y,P6<T,S,U,V,W,X>> f){
        return tuplee(t,s,u,v,w,x).contramap(f);        
    }

    public static <T,S,U,V,W,X,Y> Enc<P7<T,S,U,V,W,X,Y>> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, ElementEnc<X> x,ElementEnc<Y> y){
        return o -> and(t, and(s, and(u, and(v, and(w, and(x, y)))))).apply(new Term.Tuple(List.nil()), Enc.ext(o));
    }

    public static <T,S,U,V,W,X,Y,Z> Enc<Z> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, ElementEnc<X> x,ElementEnc<Y> y, F<Z,P7<T,S,U,V,W,X,Y>> f) {
        return tuplee(t,s,u,v,w,x,y).contramap(f);        
    }

    public static <T,S,U,V,W,X,Y,Z> Enc<P8<T,S,U,V,W,X,Y,Z>> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, ElementEnc<X> x,ElementEnc<Y> y,ElementEnc<Z> z) {
        return o -> and(t, and(s, and(u, and(v, and(w, and(x, and(y,z))))))).apply(new Term.Tuple(List.nil()), Enc.ext(o));
    }

    public static <T,S,U,V,W,X,Y,Z,R> Enc<R> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, ElementEnc<V> v, ElementEnc<W> w, ElementEnc<X> x,ElementEnc<Y> y,ElementEnc<Z> z, F<R,P8<T,S,U,V,W,X,Y,Z>> f) {
        return tuplee(t,s,u,v,w,x,y,z).contramap(f);        
    }

    public static <T> ElementEnc<T> ele(int index, Enc<T> enc) {
        return (tup,v) -> tup.ins(index, enc.encode(v));
    }

    public static <T> Enc<T> nile() {
        return v -> new Term.Nil();
    }

    public static <T, S, U> P2<T, P2<S, U>> ext(P3<T, S, U> p) {
        return P.p(p._1(), p(p._2(), p._3()));
    }

    public static <T,S,U,V> P2<T,P2<S,P2<U,V>>> ext(P4<T,S,U,V> p) {
        return P.p(p._1(), ext(p(p._2(),p._3(),p._4())));
    }

    public static <T,S,U,V,W> P2<T,P2<S,P2<U,P2<V,W>>>> ext(P5<T,S,U,V,W> p) {
        return P.p(p._1(), ext(p(p._2(),p._3(),p._4(),p._5())));
    }

    public static <T,S,U,V,W,X> P2<T,P2<S,P2<U,P2<V,P2<W,X>>>>> ext(P6<T,S,U,V,W,X> p) {
        return P.p(p._1(), ext(p(p._2(),p._3(),p._4(),p._5(), p._6())));
    }

    public static <T,S,U,V,W,X,Y> P2<T,P2<S,P2<U,P2<V,P2<W,P2<X,Y>>>>>> ext(P7<T,S,U,V,W,X,Y> p) {
        return P.p(p._1(), ext(p(p._2(),p._3(),p._4(),p._5(), p._6(), p._7())));
    }

    public static <T,S,U,V,W,X,Y,Z> P2<T,P2<S,P2<U,P2<V,P2<W,P2<X,P2<Y,Z>>>>>>> ext(P8<T,S,U,V,W,X,Y,Z> p) {
        return P.p(p._1(), ext(p(p._2(),p._3(),p._4(),p._5(), p._6(), p._7(), p._8())));
    }

}
