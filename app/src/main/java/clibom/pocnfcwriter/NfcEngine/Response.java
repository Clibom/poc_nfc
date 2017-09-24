package clibom.pocnfcwriter.NfcEngine;

/**
 * Created by CliBoM
 */
 public class Response {
    public static final int SUCCESS = 42;
    public static final int FAIL = 0;

    int status;
    String message;

    Response(int Status, String Message) {
        this.status = Status;
        this.message = Message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static class getSuccessResponse extends Response {
        public getSuccessResponse(String response) {
            super(SUCCESS, response);
        }
    }

    public static class getFailResponse extends Response {
        public getFailResponse(String response) {
            super(FAIL, response);
        }
    }
}
