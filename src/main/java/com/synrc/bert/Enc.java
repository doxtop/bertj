package com.synrc.bert;

import fj.*;
import static fj.P.p;
import fj.data.List;

public interface Enc<T> extends F<T, Term> {
    Term encode(T v);

    @Override default Term f(T v) { return encode(v); }

    default <S> Enc<S> contramap(F<S,T> f){
        return v -> this.encode(f.f(v));
    }

    public static Enc<String> stringEnc = Term::str;
    public static Enc<byte[]> binEnc = Term::bin;
    public static Enc<Double> floatEnc = Term::flt;

    public static <T> Enc<List<T>> liste(Enc<T> enc) {
        return list -> Term.list(list.map(enc::encode));
    }

    public interface ElementEnc<T> {
        Term.Tuple apply(Term.Tuple tuple, T v);
    }

    static <T, S> ElementEnc<P2<T, S>> and(ElementEnc<T> ft, ElementEnc<S> fs) {
        return (tuple, t) -> fs.apply(ft.apply(tuple, t._1()), t._2());
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

    public static<T,S,U,V> Enc<V> tuplee(ElementEnc<T> t, ElementEnc<S> s, ElementEnc<U> u, F<V, P3<T,S,U>> f) {
        return tuplee(t,s,u).contramap(f);
    }

    public static <T> ElementEnc<T> ele(int index, Enc<T> enc) {
        return (tup,v) -> tup.ins(index, enc.encode(v));
    }

    public static <T> Enc<T> nile() {
        return v -> new Term.Nil();
    }

    public static <A, B, C> P2<A, P2<B, C>> ext(P3<A, B, C> p) {
        return P.p(p._1(), p(p._2(), p._3()));
    }

}
