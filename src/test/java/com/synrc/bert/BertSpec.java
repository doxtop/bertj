package com.synrc.bert;

import org.junit.*;
import fj.*;
import fj.data.List;
import java.util.Arrays;
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
        byte bi;
        int i;

        Roster(List<User>users, byte[] status, double pi, byte bi, int i) {
            this.users=users;
            this.status=new String(status, UTF_8);
            this.pi = pi;
            this.bi = bi;
            this.i = i;
        }

        @Override public String toString() { return "Roaster[" + users + ", " + status + ", " + pi + ", " + bi + ", " + i + "]"; }
    }

    @Test
    public void bert() {
        System.out.println("decode {[{\"synrc\",\"dxt\"}, {\"synrc\",\"5HT\"}],<<\"active\">>,3.14,128} =>");

        byte[] in = {// [{minor_version, 0}]
            (byte)131,104,5,
                108,0,0,0,2,104,2,107,0,5,115,121,110,114,99,107,0,3,100,120,116,
                104,2,107,0,5,115,121,110,114,99,
                107,0,3,53,72,84,106,
                109,0,0,0,6,97,99,116,
                105,118,101,
                99,51,46,49,52,48,48,48,48,48,48,48,48,48,48,48,48,48,49,50,52,51,52,101,43,48,48,0,0,0,0,0,
                97,(byte)128,
                98,127,(byte)255,(byte)255,(byte)255};

        // map tuples to object with decoder combinations
        final Dec<User> userDecoder     = tuple(el(1, stringDec), el(2, stringDec), User::new);
        final Dec<Roster> rosterDecoder = tuple(el(1, list(userDecoder)), el(2, binDec), el(3,floatSrtDec), el(4,byteDec), el(5, intDec), Roster::new);

        final Res<Term> bert = Parser.parse(in);
        final Res<Roster> roster = bert.decode(rosterDecoder);
        System.out.println("Decoded: " + roster.res);

        final Enc<User> userEncoder = tuplee(ele(1, stringEnc), ele(2, stringEnc), user -> P.p(user.org, user.name));
        final Enc<Roster> rosterEncoder = tuplee(ele(1, liste(userEncoder)), ele(2, binEnc), ele(3, floatStrEnc), ele(4, byteEnc), ele(5, intEnc),
            r -> P.p(r.users, r.status.getBytes(UTF_8), r.pi, r.bi, r.i));

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
