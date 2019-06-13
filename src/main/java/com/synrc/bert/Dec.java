package com.synrc.bert;

import fj.*;
import fj.data.List;
import static fj.P.p;
import java.math.BigDecimal;

public interface Dec<T> extends F<Term, Res<T>> {
    Res<T> decode(Term v);
    default Res<T> f(Term v) { return decode(v); }
    default <S> Dec<S> map(F<T,S> f) { return v -> this.decode(v).map(f); }

    public static final Dec<String> stringDec = Term::str;
    public static final Dec<byte[]> binDec = Term::bin;
    public static final Dec<Double> floatDec = Term::float754;
    public static final Dec<BigDecimal> floatSrtDec = Term::floatStr;
    public static final Dec<Byte> byteDec = Term::bt;
    public static final Dec<Integer> intDec = Term::in;

    public static interface ElementDec<A> {
        Res<A> apply(List<Term> elements);
    }

    public static <S> Dec<List<S>> list(Dec<S> ds) {
        return term -> term.list(list -> Res.seq(list.map(ds))).orSome(Res.fail(term + " isn't a list"));
    }

    public static <T, S> Dec<P2<T,S>> tuple(ElementDec<T> t, ElementDec<S> s) {
        return v -> v.tup(pair(t, s)::apply).orSome(Res.fail("Tuple expected " + v));
    }

    public static <T, S, U> Dec<U> tuple(ElementDec<T> t, ElementDec<S> s, F2<T, S, U> f) {
        return tuple(t, s).map(tup -> f.f(tup._1(), tup._2()));
    }

    public static <T, S, U> Dec<P3<T,S,U>> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u) {
        return o -> o.tup(tup -> pair(t, pair(s,u)).apply(tup).map(Dec::flat3))
            .orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S,U,V> Dec<V> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u, F3<T,S,U,V> f) {
        return tuple(t,s,u).map(tup -> f.f(tup._1(),tup._2(),tup._3()));
    }

    public static <T,S,U,V> Dec<P4<T,S,U,V>> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v){
        return o -> o.tup(tup -> pair(t, pair(s, pair(u,v))).apply(tup).map(Dec::flat4))
            .orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S,U,V,W> Dec<W> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, F4<T,S,U,V,W> f) {
        return tuple(t,s,u,v).map(tup -> f.f(tup._1(),tup._2(),tup._3(),tup._4()));
    }

    public static <T,S,U,V,W> Dec<P5<T,S,U,V,W>> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w) {
        return o -> o.tup(tup -> pair(t, pair(s, pair(u, pair(v,w)))).apply(tup).map(Dec::flat5))
            .orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S,U,V,W,X> Dec<X> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w, F5<T,S,U,V,W,X> f) {
        return tuple(t,s,u,v,w).map(tup -> f.f(tup._1(),tup._2(),tup._3(),tup._4(), tup._5()));
    }

    public static <A, B> ElementDec<P2<A, B>> pair(ElementDec<A> ad, ElementDec<B> bd) {
        return elements -> ad.apply(elements).bind(a -> bd.apply(elements).map(b -> p(a, b)));
    }

    public static <T> ElementDec<T> el(Integer pos, Dec<T> dec) {
        try {
            return obj -> dec.decode(obj.index(pos-1)).mapFail(str -> "Failure decoding element " + (pos-1) + ": " + str);
        } catch(Exception e) {
            return obj -> Res.<T>fail("No element on pos " + pos);
        }
    }

    public static <T, S, U> P3<T, S, U> flat3(P2<T, P2<S, U>> n) {
        return p(n._1(), n._2()._1(), n._2()._2());
    }

    public static <T,S,U,V> P4<T,S,U,V> flat4(P2<T,P2<S,P2<U,V>>> n) {
        P3<S,U,V> p3 = flat3(n._2());
        return p(n._1(),p3._1(),p3._2(),p3._3());
    }

    public static <T,S,U,V,W> P5<T,S,U,V,W> flat5(P2<T,P2<S,P2<U,P2<V,W>>>> n) {
        P4<S,U,V,W> p4 = flat4(n._2());
        return p(n._1(), p4._1(), p4._2(), p4._3(), p4._4());
    }
}
