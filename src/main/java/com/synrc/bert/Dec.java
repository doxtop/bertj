package com.synrc.bert;

import fj.*;
import fj.data.List;
import static fj.P.p;

public interface Dec<T> extends F<Term, Res<T>> {
    Res<T> decode(Term v);
    default Res<T> f(Term v) { return decode(v); }
    default <S> Dec<S> map(F<T,S> f) { return v -> this.decode(v).map(f); }

    public static final Dec<String> stringDec = Term::str;
    public static final Dec<byte[]> binDec = Term::bin;

    public static interface ElementDec<A> {
        Res<A> apply(List<Term> elements);
    }

    public static <S> Dec<List<S>> list(Dec<S> ds) {
        return term -> term.list(list -> Res.seq(list.map(ds))).orSome(Res.fail(term + " isn't a list"));
    }

    public static <T, S> Dec<P2<T, S>> tuple(ElementDec<T> a, ElementDec<S> b) {
        return v -> v.tup(pair(a, b)::apply).orSome(Res.fail("Tuple expected" + v + "."));
    }

    public static <T, S, U> Dec<U> tuple(ElementDec<T> a, ElementDec<S> b, F2<T, S, U> f) {
        return tuple(a, b).map(t -> f.f(t._1(), t._2()));
    }

    public static <A, B> ElementDec<P2<A, B>> pair(ElementDec<A> ad, ElementDec<B> bd) {
        return elements -> ad.apply(elements).bind(a -> bd.apply(elements).map(b -> p(a, b)));
    }

    public static <T> ElementDec<T> el(Integer pos, Dec<T> dec) {
        try {
            return obj -> dec.decode(obj.index(pos)).mapFail(str -> "Failure decoding element " + pos + ": " + str);
        } catch(Exception e) {
            return obj -> Res.<T>fail("No element on pos " + pos);
        }
    }
}
