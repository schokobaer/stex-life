package at.apf.stexlife.plugin;

import at.apf.stexlife.api.Converter;
import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.io.IOException;
import java.util.Scanner;

@StexLifeModule("stdio")
public class StdIO {

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

    @StexLifeFunction
    public DataUnit read(DataUnit amount) {
        DataType.expecting(amount, DataType.INT);
        return read(amount.getInt().intValue());
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
