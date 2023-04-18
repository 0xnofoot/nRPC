package xyz.nofoot.extension;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.loader.extension
 * @className: Holder
 * @author: NoFoot
 * @date: 4/17/2023 6:22 PM
 * @description: TODO
 */
public class Holder<T> {

    private volatile T value;

    /**
     * @return: T
     * @author: NoFoot
     * @date: 4/17/2023 6:23 PM
     * @description: 用于持有目标对象
     */
    public T get() {
        return value;
    }

    /**
     * @param value:
     * @author: NoFoot
     * @date: 4/17/2023 6:23 PM
     * @description: TODO
     */
    public void set(T value) {
        this.value = value;
    }
}
