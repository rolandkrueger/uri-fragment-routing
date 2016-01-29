package org.roklib.webapps.uridispatching;

public class TURIActionCommand implements URIActionCommand {
    private static final long serialVersionUID = 8282933112969092819L;

    public boolean executed = false;

    @Override
    public void execute() {
        executed = true;
    }
}
