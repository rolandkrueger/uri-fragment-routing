package org.roklib.webapps.uridispatching;

import java.io.Serializable;

public abstract class AbstractURIActionCommand implements Serializable {
    private static final long serialVersionUID = 9020047185322723866L;

    public abstract void execute();
}
