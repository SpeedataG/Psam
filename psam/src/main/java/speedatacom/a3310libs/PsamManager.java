package speedatacom.a3310libs;

import speedatacom.a3310libs.inf.IPsam;
import speedatacom.a3310libs.realize.Psam3310Realize;

/**
 * Created by brxu on 2017/2/8.
 */

public class PsamManager {
    public static IPsam psam;

    public static IPsam getPsamIntance() {
        if (psam == null) {
            psam = new Psam3310Realize();
        }
        return psam;
    }
}
