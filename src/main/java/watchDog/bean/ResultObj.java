package watchDog.bean;


public class ResultObj {
    private Integer result;
    private String comment;
    
    public ResultObj(Integer result, String comment) {
        super();
        this.result = result;
        this.comment = comment;
    }
    public Integer getResult() {
        return result;
    }
    public void setResult(Integer result) {
        this.result = result;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
}
