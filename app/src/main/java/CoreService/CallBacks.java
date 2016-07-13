package CoreService;

/**
* Created by abc on 2015/10/09.
        */
public interface CallBacks<T> {
    public void OnResult(T t);
    public void OnFault(T t);
}
