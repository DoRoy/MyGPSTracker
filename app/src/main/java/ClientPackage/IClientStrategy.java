package ClientPackage;

import java.io.*;

public interface IClientStrategy {

    void clientStrategy(InputStream inFromServer, OutputStream outToServer);

}
