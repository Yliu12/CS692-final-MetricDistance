package modulemetricdistance.distancecalculationalgo;

/**
 * Created by yliu12 on 2017/4/29.
 *
 */

public class MDException extends RuntimeException {

    private String retCd  = "9";  //异常对应的返回码
    private String msgDes  = "MDException";  //异常对应的描述信息

    public MDException() {
        super();
    }


    public String getRetCd() {
        return retCd;
    }

    public String getMsgDes() {
        return msgDes;
    }
}