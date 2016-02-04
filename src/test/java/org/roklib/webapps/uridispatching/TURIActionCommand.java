package org.roklib.webapps.uridispatching;

public class TUriActionCommand implements UriActionCommand {
    private static final long serialVersionUID = 8282933112969092819L;

    public boolean executed = false;

    @Override
    public void execute() {
        executed = true;
    }
}
