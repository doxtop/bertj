package com.synrc.bert;

import fj.F;
import fj.data.Either;
import fj.data.List;

public class Res<T> {
    // use Either directly or collect errors in Validation...
    final Either<String, T> res;

    private Res(Either<String, T> res) { this.res = res; }

    public static <T> Res<T> fail(String msg) { return new Res<>(Either.left(msg)); }
    public static <T> Res<T> ok(T x)          { return new Res<>(Either.right(x)); }

    public <S> Res<S> decode(Dec<S> dec) { return value(dec);}

    private <T> Res<T> value(F<Term,Res<T>> f) {
        return res.either(
            fail -> Res.fail(fail), 
            success -> {if (success instanceof Term) {
                return f.f((Term)success);
            } else {
                return Res.fail("BERT?");
            }});
    }

    public <S> Res<S> map(F<T, S> f) {
        try {
            return new Res<>(Either.<String, T, S>rightMap_().f(f).f(res));
        } catch (Exception e) {
            return new Res<>(Either.left(this + " map failed." + e.getMessage()));
        }
    }

    public static <T> Res<List<T>> seq(List<Res<T>> xs) {
        return xs.foldLeft(
            (ac, res) -> res.bind(a -> ac.map(list -> list.cons(a))),
            Res.ok(List.<T>nil())).map(List::reverse);
    }

    public <S> Res<S> bind(F<T, Res<S>> f) {
        try {
            return new Res<S>(Either.<String, T, Res<S>>rightMap_().f(f).f(res).right().bind(res -> res.res));
        } catch(Exception e) {
            return new Res<>(Either.left(this + " bind failed." + e.getMessage()));
        }
    }

    public Res<T> mapFail(F<String, String> f) {
        try {
            return new Res<>(Either.<String, T, String>leftMap_().f(f).f(res));
        } catch (Exception e) {
            return new Res<>(Either.left(this + " map failed." + e.getMessage()));
        }
    }

}
