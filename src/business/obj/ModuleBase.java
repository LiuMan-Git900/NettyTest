package business.obj;

public class ModuleBase<T> {
    public T owner;
    public ModuleBase(T humanService) {
        this.owner = humanService;
    }
}
