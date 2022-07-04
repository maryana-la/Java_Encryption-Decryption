package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<String, String> info = parseArgs(args);
        Coding code = info.get("-alg").equals("shift") ? new Shift(info) : new Unicode(info);
        code.codingProcess();
    }

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> info = new HashMap<>();
        info.put("-mode", "enc");
        info.put("-key", "0");
        info.put("-data", "");
        info.put("-in", "");
        info.put("-out", "");
        info.put("-alg", "shift");

        for(int i = 0; i < args.length; i++) {
            if (info.containsKey(args[i])) {
                info.put(args[i], args[i + 1]);
            }
        }
        return info;
    }
}

abstract class Coding {

    HashMap<String, String> info;
    int key;
    String str;
    String result;
    
    Coding(HashMap<String, String> info) {
        this.info = info;
        this.str = "";
    }
    
    public void codingProcess() {
        getKey();
        getInputString();
        crypting();
        outputResult();
    }

    private void getKey() {
        this.key = info.get("-mode").equals("enc") ? Integer.parseInt(info.get("-key")) : -Integer.parseInt(info.get("-key"));
    }

    private void getInputString() {
        try {
            if (!info.get("-data").equals("")) {
                this.str = info.get("-data");
            } else if (!info.get("-in").equals("")) {
                this.str = Files.readString(Paths.get(info.get("-in")));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void outputResult() {
        if (!info.get("-out").equals("")) {
            File file = new File(info.get("-out"));
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(result);
                writer.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(result);
        }
    }
    
    abstract void crypting();
}

class Shift extends Coding {
    
    Shift(HashMap<String, String> info) {
        super(info);
    }
    
    @Override
    void crypting() {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isLetter(c)) {
                output.append(c);
                continue;
            }
            if (key >= 0) {
                if (c >= 'A' && c <= 'Z') {
                    output.append((c + key <= 'Z') ? (char) (c + key) : (char) ('A' - 1 + (c + key) % 'Z'));
                } else if (c >= 'a' && c <= 'z') {
                    output.append((c + key <= 'z') ? (char) (c + key) : (char) ('a' - 1 + (c + key) % 'z'));
                }
            } else {
                if (c >= 'A' && c <= 'Z') {
                    output.append((c + key >= 'A') ? (char) (c + key) : (char) ('Z' + 1 - ('A' - key) % c));
                } else if (c >= 'a' && c <= 'z') {
                    output.append((c + key >= 'a') ? (char) (c + key) : (char) ('z' + 1 - ('a' - key) % c));
                }
            }
        }
        result = output.toString();
    }
}

class Unicode extends Coding {

    Unicode(HashMap<String, String> info) {
        super(info);
    }

    @Override
    void crypting() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c + key < 0) {
                output.append((char)(127 + c + key));
            } else if (c + key > 127) {
                output.append((char)((c + key) % 127));
            } else {
                output.append((char)(c + key));
            }
        }
        result = output.toString();
    }
}


