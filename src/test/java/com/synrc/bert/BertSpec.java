package com.synrc.bert;

import org.junit.*;
import fj.*;
import fj.data.List;
import fj.data.HashMap;
import fj.data.hlist.HList;
import java.util.Arrays;
import java.util.Map;
import java.math.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static com.synrc.bert.Dec.*;
import static com.synrc.bert.Enc.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class BertSpec {

    class User {
        String org;
        String name;
        User(String org, String name){
            this.org=org;
            this.name = name;
        }
        @Override public String toString() { return "User[" + name + "," + org+ "]"; }
    }

    class Roster {
        List<User> users;
        String status;
        double pi;
        byte b;
        int i;
        String atom;
        List<String> tup;
        BigInteger bi;
        //List<String> map;
        Map<String, String> map;
        
        Roster(List<User>users, byte[] status, double pi, byte b, int i, String atom, List<String> tup, /*BigInteger bi, */
            Map<String,String> map){
            //List<String> map) {
            this.users=users;
            this.status=new String(status, UTF_8);
            this.pi = pi;
            this.b = b;
            this.i = i;
            this.atom = atom;
            this.tup = tup;
            this.bi = bi;
            this.map = map;
        }

        @Override public String toString() { return "Roaster[" + users + ", " + status + ", " + pi + 
            ", " + b + ", " + i + ", " + atom +
            "," + tup + "," + bi + ", " + map + "]"; }
    }

    class Mp {

    }

    @Test
    public void bert() {
        System.out.println("decode {[{\"synrc\",\"dxt\"}, {\"synrc\",\"5HT\"}],<<\"active\">>,3.14,128,256,ok, {ok}} =>");

        final byte[] in = {//{minor_version, 2}
            (byte)131,104,8,
            108,0,0,0,2,
                104,2,
                    107,0,5,115,121,110,114,99,
                    107,0,3,100,120,116,
                104,2,
                    107,0,5,115,121,110,114,99,
                    107,0,3,53,72,84,
            106,
            109,0,0,0,6,97,99,116,105,118,101,
            70,64,9,30,(byte)184,81,(byte)235,(byte)133,31,
            97,(byte)128,
            98,0,0,1,0,
            //100,0,2,111,107,
            //119,6,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,
//   118,1,0,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,
//   (byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,
//   (byte)185,(byte)208,(byte)183,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)208,(byte)184,(byte)208,(byte)184,(byte)209,(byte)132,
//   (byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,
//   (byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)184,(byte)209,(byte)132,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,
//   (byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,
//   (byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,
//   (byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,
//   (byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,
//   (byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,
//   (byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)208,(byte)184,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,
//   (byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,
//   (byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132, 
//   (byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,

  119,(byte)254,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)
  (byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)
  (byte)185,(byte)208,(byte)183,(byte)209,(byte)131,(byte)208,(byte)185,(byte)209,(byte)133,(byte)209,(byte)131,(byte)208,(byte)185,(byte)208,(byte)184,(byte)208,(byte)184,(byte)209,(byte)132,(byte)
  (byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)
  (byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)184,(byte)209,(byte)132,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)
  (byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)
  (byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)
  (byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)
  (byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)
  (byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)
  (byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)209,(byte)132,(byte)208,(byte)184,(byte)208,(byte)178,(byte)208,(byte)184,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)
  (byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)
  (byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte) 
  (byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,(byte)209,(byte)132,

            104,1,100,0,2,111,107,//105,0,0,0,1,100,0,2,111,107
            //110,8,0,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,31, //2305843009213693951
            116,0,0,0,1,
                107,0,2,115,49,/*->*/107,0,2,115,50
            // 116,0,0,0,3,
            //     97,1, /*->*/ 104,1,109,0,0,0,3,116,117,112,
            //     100,0,2,111,107,/*->*/97,1,
            //     107,0,2,104,105,/*->*/108,0,0,0,1,
            //         104,2,
            //             107,0,6,115,97,105,108,111,114,
            //             107,0,5,119,111,114,108,100,
            //         106
            };

        // map tuples to object with decoder combinations
        final F<String, List<String>> f = a -> List.<String>nil().cons(a);
        final Dec<List<String>> innerDecoder = tuple(el(1,atomDec), f);
        final Dec<Map<String,String>> mapDecoder = mp(stringDec).map(l -> {
            List<String> keys = List.nil();
            List<String> vals = List.nil();
            for (int i=0;i<l.length();i++) {
                if (i % 2 == 0) {
                    keys = keys.cons(l.index(i));
                } else {
                    vals = vals.cons(l.index(i));
                }
            }
            return HashMap.iterableHashMap(keys.zip(vals)).toMap();
        });
        final Dec<User> userDecoder     = tuple(el(1, stringDec), el(2, stringDec), User::new);
        final Dec<Roster> rosterDecoder = tuple(
            el(1, list(userDecoder)), 
            el(2, binDec),
            el(3, floatDec.map(n->n.doubleValue())),
            el(4, intDec.map(i->i.byteValue())),
            el(5, intDec),
            el(6, atomDec),
            el(7, innerDecoder),
            //el(8, bigDec),
            el(8, mapDecoder),
             Roster::new);

        final Res<Term> bert = Parser.parse(in);
        System.out.println("Parsed: " + bert.res.right().value());
        final Res<Roster> roster = bert.decode(rosterDecoder);
        System.out.println("Decoded: " + roster.res);

        final Enc<List<String>> listEncoder = tuplee(ele(1, atomEnc), l -> l.head());
        final Enc<User> userEncoder = tuplee(ele(1, stringEnc), ele(2, stringEnc), user -> P.p(user.org, user.name));
        // from list to p2
        //final Enc<List<String>> mapEncoder = mape(stringEnc);
        final Enc<Map<String,String>> mapEncoder = mape(stringEnc).contramap(map -> {
            final List<String> keys = List.iterableList(map.keySet());
            final List<String> vals = List.iterableList(map.values());
            List<List<String>> terms = keys.zipWith(vals, (t,s) -> List.list(t,s));
            return List.<String>join().f(terms);
        });

        final Enc<List<byte[]>> binEncoder = tuplee(ele(1, binEnc), l -> l.head());
        final Enc<Roster> rosterEncoder = tuplee(
            ele(1, liste(userEncoder)), 
            ele(2, binEnc),
            ele(3, floatEnc),
            ele(4, intEnc.contramap(i -> i.intValue())),
            ele(5, intEnc),
            ele(6, guessAtomEnc),
            ele(7, listEncoder),
//            ele(8, bigEnc),
            ele(8, mapEncoder),
            r -> P.p(r.users, r.status.getBytes(UTF_8), r.pi, r.b, r.i, r.atom, r.tup, r.map));

        //final User u = new User("synrc", "dxt");
        //final User u1 = new User("synrc", "5HT");
        //final List<User> l = List.list(u, u1);
        //final Roster r = new Roster(l, "active".getBytes(UTF_8), 3.14000000000000012434e+00, (byte)128,(int)2147483647);

        System.out.println("encode " + roster.res + " =>");
        roster.res.either(
              dl -> {fail(dl); return dl;},
              dr -> {
                Writer.write(rosterEncoder.encode(dr)).res.either(
                      el-> {fail(el); return el;},
                      er -> {
                          System.out.println("write " + Arrays.toString(er));
                          assertArrayEquals("dec <-> enc shoud be equal", in, er);return er;});
                return dr;});
    }
}
