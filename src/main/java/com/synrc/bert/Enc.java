package com.synrc.bert;

import fj.*;
import fj.data.List;

public interface Enc<T> extends F<T, Term> {
    Term encode(T v);

    @Override default Term f(T v) { return encode(v); }

    default <S> Enc<S> contramap(F<S,T> f){
        return v -> this.encode(f.f(v));
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

    public static <T> ElementEnc<T> ele(int index, Enc<T> enc) {
        return (tup,v) -> tup.ins(index, enc.encode(v));
    }

    public static <T> Enc<T> nile() {
        return v -> new Term.Nil();
    }
}
