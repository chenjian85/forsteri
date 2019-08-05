package io.forsteri.common.exception;

public class ForsteriException extends Exception {
    private static final String format = "errNo=%d, errMsg=%s";
    public int errNo;
    private String errMsg;
    private Throwable cause;

    public ForsteriException(int errNo, String errMsg, Throwable cause) {
        super(String.format(ForsteriException.format, errNo, errMsg), cause);
        this.errNo = errNo;
        this.errMsg = errMsg;
        this.cause = cause;
    }

    public ForsteriException(int errNo, String errMsg) {
        super(String.format(ForsteriException.format, errNo, errMsg));
        this.errNo = errNo;
        this.errMsg = errMsg;
    }

    public ForsteriException(String message) {
        super(message);
    }

    public ForsteriException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
