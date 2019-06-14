package com.synrc.bert;

import fj.*;
import fj.data.List;
import fj.data.Option;
import static fj.P.p;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface Dec<T> extends F<Term, Res<T>> {
    Res<T> decode(Term v);
    default Res<T> f(Term v) { return decode(v); }
    default <S> Dec<S> map(F<T,S> f) { return v -> this.decode(v).map(f); }

    public static final Dec<String> stringDec = Term::str;
    public static final Dec<byte[]> binDec = Term::bin;
    public static final Dec<BigDecimal> floatDec = Term::float754;   // double easy
    public static final Dec<BigDecimal> floatSrtDec = Term::floatStr;// deprecate
    public static final Dec<Byte> byteDec = Term::bt;
    public static final Dec<Integer> intDec = Term::in;
    public static final Dec<String> atomDec = Term::atom;
    public static final Dec<BigInteger> bigDec = Term::big;

    public static interface ElementDec<A> {
        Res<A> apply(List<Term> elements);
    }

    public static <S> Dec<List<S>> list(Dec<S> ds) {
        return term -> term.list(list -> Res.seq(list.map(ds))).orSome(Res.fail(term + " isn't a list"));
    }

    public static <S> Dec<List<S>> mapa(Dec<S> ds) {
        return term -> term.mp(list -> Res.seq(list.map(ds))).orSome(Res.fail(term + " isn't a map."));
    }

    public static <T> Dec<P1<T>> tuple(ElementDec<T> td) {
        return o -> o.tup(l -> td.apply(l).map(e -> P.p(e))).orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S> Dec<S> tuple(ElementDec<T> t, F<T,S> f) {
        return tuple(t).map(tup -> f.f(tup._1()));
    }

    public static <T, S> Dec<P2<T,S>> tuple(ElementDec<T> t, ElementDec<S> s) {
        return o -> o.tup(pair(t, s)::apply).orSome(Res.fail("Tuple expected " + o));
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

    public static <T,S,U,V,W,X> Dec<P6<T,S,U,V,W,X>> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w,ElementDec<X> x) {
        return o -> o.tup(tup -> pair(t, pair(s, pair(u, pair(v, pair(w,x))))).apply(tup).map(Dec::flat6))
            .orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S,U,V,W,X,Y> Dec<Y> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w,ElementDec<X> x, F6<T,S,U,V,W,X,Y> f) {
        return tuple(t,s,u,v,w,x).map(tup -> f.f(tup._1(),tup._2(),tup._3(),tup._4(), tup._5(), tup._6()));
    }

    public static <T,S,U,V,W,X,Y> Dec<P7<T,S,U,V,W,X,Y>> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w,ElementDec<X> x,ElementDec<Y> y) {
        return o -> o.tup(tup -> pair(t, pair(s, pair(u, pair(v, pair(w, pair(x,y)))))).apply(tup).map(Dec::flat7))
            .orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S,U,V,W,X,Y,Z> Dec<Z> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w,ElementDec<X> x, ElementDec<Y> y, F7<T,S,U,V,W,X,Y,Z> f) {
        return tuple(t,s,u,v,w,x,y).map(tup -> f.f(tup._1(),tup._2(),tup._3(),tup._4(), tup._5(), tup._6(), tup._7()));
    }

    public static <T,S,U,V,W,X,Y,Z> Dec<P8<T,S,U,V,W,X,Y,Z>> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w,ElementDec<X> x,ElementDec<Y> y,ElementDec<Z> z) {
        return o -> o.tup(tup -> pair(t, pair(s, pair(u, pair(v, pair(w, pair(x, pair(y,z))))))).apply(tup).map(Dec::flat8))
            .orSome(Res.fail("Tuple expected " + o));
    }

    public static <T,S,U,V,W,X,Y,Z,R> Dec<R> tuple(ElementDec<T> t, ElementDec<S> s, ElementDec<U> u,ElementDec<V> v, ElementDec<W> w,ElementDec<X> x, ElementDec<Y> y,ElementDec<Z> z, F8<T,S,U,V,W,X,Y,Z,R> f) {
        return tuple(t,s,u,v,w,x,y,z).map(tup -> f.f(tup._1(),tup._2(),tup._3(),tup._4(), tup._5(), tup._6(), tup._7(),tup._8()));
    }

    public static <T, S> ElementDec<P2<T, S>> pair(ElementDec<T> td, ElementDec<S> sd) {
        return o -> td.apply(o).bind(t -> sd.apply(o).map(s -> p(t, s)));
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

    public static <T,S,U,V,W,X> P6<T,S,U,V,W,X> flat6(P2<T,P2<S,P2<U,P2<V,P2<W,X>>>>> n) {
        P5<S,U,V,W,X> p5 = flat5(n._2());
        return p(n._1(), p5._1(), p5._2(), p5._3(), p5._4(), p5._5());
    }

    public static <T,S,U,V,W,X,Y> P7<T,S,U,V,W,X,Y> flat7(P2<T,P2<S,P2<U,P2<V,P2<W,P2<X,Y>>>>>> n) {
        P6<S,U,V,W,X,Y> p6 = flat6(n._2());
        return p(n._1(), p6._1(), p6._2(), p6._3(), p6._4(), p6._5(),p6._6());
    }

    public static <T,S,U,V,W,X,Y,Z> P8<T,S,U,V,W,X,Y,Z> flat8(P2<T,P2<S,P2<U,P2<V,P2<W,P2<X,P2<Y,Z>>>>>>> n) {
        P7<S,U,V,W,X,Y,Z> p = flat7(n._2());
        return p(n._1(), p._1(), p._2(), p._3(), p._4(), p._5(),p._6(), p._7());
    }

}
