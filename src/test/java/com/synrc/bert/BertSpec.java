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

        Roster(List<User>users, byte[] status, double pi, byte bi) {
            this.users=users;
            this.status=new String(status, UTF_8);
            this.pi = pi;
            this.bi = bi;
        }

        @Override public String toString() { return "Roaster[" + users + ", " + status + ", " + pi + ", " + bi + "]"; }
    }

    @Test
    public void bert() {
        System.out.println("decode {[{\"synrc\",\"dxt\"}, {\"synrc\",\"5HT\"}],<<\"active\">>,3.14,128} =>");

        byte[] in = {
            (byte)131,104,4,108,0,0,0,2,104,2,107,0,5,115,121,110,114,99,107,0,3,100,120,116,
            104,2,107,0,5,115,121,110,114,99,107,0,3,53,72,84,106,109,0,0,0,6,97,99,116,
            105,118,101,70,64,9,30,(byte)184,81,(byte)235,(byte)133,31,97,(byte)128};

        // map tuples to object with decoder combinations
        final Dec<User> userDecoder     = tuple(el(1, stringDec), el(2, stringDec), User::new);
        final Dec<Roster> rosterDecoder = tuple(el(1, list(userDecoder)), el(2, binDec), el(3,floatDec), el(4,byteDec), Roster::new);

        final Res<Term> bert = Parser.parse(in);
        final Res<Roster> roster = bert.decode(rosterDecoder);
        System.out.println("Decoded: " + roster.res);

        final Enc<User> userEncoder = tuplee(ele(1, stringEnc), ele(2, stringEnc), user -> P.p(user.org, user.name));
        final Enc<Roster> rosterEncoder = tuplee(ele(1, liste(userEncoder)), ele(2, binEnc), ele(3, floatEnc), ele(4, byteEnc),
            r -> P.p(r.users, r.status.getBytes(UTF_8), r.pi, r.bi));

        final User u = new User("synrc", "dxt");
        final User u1 = new User("synrc", "5HT");
        final List<User> l = List.list(u, u1);
        final Roster r = new Roster(l, "active".getBytes(UTF_8), 3.14, (byte)128);

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
