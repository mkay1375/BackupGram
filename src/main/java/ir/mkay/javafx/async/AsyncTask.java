package ir.mkay.javafx.async;

import javafx.application.Platform;

/**
 * @author Victor Oliveira https://github.com/victorlaerte
 */
public abstract class AsyncTask<T1, T2, T3> {
    private boolean daemon = true;

    public abstract void onPreExecute();

    public abstract T3 doInBackground(T1... params);

    public abstract void onPostExecute(T3 params);

    public abstract void progressCallback(T2... params);

    public void publishProgress(final T2... params) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressCallback(params);
            }
        });
    }


    public final Thread backGroundThread = new Thread(new Runnable() {
        @Override
        public void run() {
            final T3 param = doInBackground((T1[]) params);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    onPostExecute(param);
                }
            });
        }
    });

    Object params;

    public void execute(final T1... params) {
        this.params = params;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                backGroundThread.setDaemon(daemon);
                backGroundThread.start();
            }
        });
    }


    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }


    public final boolean isInterrupted() {
        return this.backGroundThread.isInterrupted();
    }


    public final boolean isAlive() {
        return this.backGroundThread.isAlive();
    }

    public final void interrupt() {
        this.backGroundThread.interrupt();
    }
}
