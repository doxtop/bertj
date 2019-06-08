package com.synrc.bert;

import org.junit.*;
import fj.data.List;
import static java.nio.charset.StandardCharsets.UTF_8;
import static com.synrc.bert.Dec.*;

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

        Roster(List<User>users, byte[] status) {
            this.users=users;
            this.status=new String(status, UTF_8);
        }

        @Override public String toString() { return "Roaster[" + users + ", " + status + "]"; }
    }

    @Test
    public void bert() {
        System.out.println("decode {[{\"synrc\",\"dxt\"}, {\"synrc\",\"5HT\"}],<<\"active\">>} =>");

        byte[] in = {(byte)131,104,2,108,0,0,0,2,104,2,107,0,5,115,121,110,114,99,107,0,3,100,120,116,104,2,107,0,5,115,121,110,114,99,107,0,3,53,72,84,106,109,0,0,0,6,97,99,116,105,118,101};

        // map tuples to object with decoder combinations
        final Dec<User> userDecoder     = tuple(el(1, stringDec), el(2, stringDec), User::new);
        final Dec<Roster> rosterDecoder = tuple(el(1, list(userDecoder)), el(2, binDec), Roster::new);

        final Res<Term> bert = Parser.parse(in);
        final Roster roster = bert.decode(rosterDecoder).orThrow(RuntimeException::new);

        System.out.println(roster);
    }
}
