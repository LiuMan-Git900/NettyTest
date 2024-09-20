package enCodAndDeCode.mesasagePack.other;

import business.obj.CallInfo;

public class MsgToObjFunction {

    public static<T > T GetObjectById(int id) {
        switch (id) {
            case 1:
                return (T) new CallInfo();
            default:
                return  null;
        }
    }
}
