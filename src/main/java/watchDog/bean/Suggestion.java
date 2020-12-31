package watchDog.bean;

import watchDog.database.Record;

public class Suggestion {
    private String userId = null;
    private String suggestion = null;
    public Suggestion(Record r)
    {
        this.userId = (String)r.get("user_id");
        this.suggestion = (String)r.get("suggestion");
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSuggestion() {
        return suggestion;
    }
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    
}
