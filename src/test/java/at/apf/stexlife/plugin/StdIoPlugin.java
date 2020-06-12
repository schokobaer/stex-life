package at.apf.stexlife.plugin;

import at.apf.stexlife.data.Converter;
import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;

import java.io.IOException;
import java.util.Scanner;

@StexLifeModule("stdio")
public class StdIoPlugin {

    @StexLifeFunction
    public void println(DataUnit d) {
        System.out.println(Converter.stringify(d));
    }

    @StexLifeFunction
    public void print(DataUnit d) {
        System.out.print(Converter.stringify(d));
    }

    @StexLifeFunction
    public DataUnit read() {
        return read(1);
    }

    public DataUnit read(int amount) {
        byte[] b = new byte[amount];
        try {
            System.in.read(b);
        } catch (IOException e) {
            return new DataUnit(null, DataType.NULL);
        }
        return new DataUnit(new String(b), DataType.STRING);
    }

    @StexLifeFunction
    public DataUnit readLine() {
        Scanner s = new Scanner(System.in);
        String res = s.nextLine();
        s.close();
        return new DataUnit(res, DataType.STRING);
    }
}
