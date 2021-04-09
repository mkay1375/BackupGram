package ir.mkay.javafx.async;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleAsyncTask<PASS> extends AsyncTask<Void, Void, PASS> {
    private Runnable onPreExecute;
    private Supplier<PASS> doInBackground;
    private Consumer<PASS> onPostExecute;
    private Runnable progressCallback;

    public SimpleAsyncTask(Runnable onPreExecute, Supplier<PASS> doInBackground, Consumer<PASS> onPostExecute, Runnable progressCallback) {
        this.onPreExecute = onPreExecute;
        this.doInBackground = doInBackground;
        this.onPostExecute = onPostExecute;
        this.progressCallback = progressCallback;
        execute();
    }

    public SimpleAsyncTask(Supplier<PASS> doInBackground, Consumer<PASS> onPostExecute) {
        this.doInBackground = doInBackground;
        this.onPostExecute = onPostExecute;
        execute();
    }

    @Override
    public void onPreExecute() {
        if (onPreExecute != null) {
            onPreExecute.run();
        }
    }

    @Override
    public PASS doInBackground(Void... params) {
        return doInBackground.get();
    }

    @Override
    public void onPostExecute(PASS params) {
        if (onPostExecute != null)
            onPostExecute.accept(params);
    }

    @Override
    public void progressCallback(Void... params) {
        if (progressCallback != null)
            progressCallback.run();
    }


//    public SimpleAsyncTask(ARG arg, Runnable onPreExecute, Function<ARG, PASS> doInBackground, Consumer<PASS> onPostExecute, Consumer<UPDATE> progressCallback) {
//        this.onPreExecute = onPreExecute;
//        this.doInBackground = doInBackground;
//        this.onPostExecute = onPostExecute;
//        this.progressCallback = progressCallback;
//        execute(arg);
//    }
//
//    public SimpleAsyncTask(ARG arg,Function<ARG, PASS> doInBackground, Consumer<PASS> onPostExecute) {
//        this.doInBackground = doInBackground;
//        this.onPostExecute = onPostExecute;
//        execute(arg);
//    }
//
//    @Override
//    public void onPreExecute() {
//        onPreExecute.run();
//    }
//
//    @Override
//    public PASS doInBackground(ARG... params) {
//        if (params.length > 0)
//            return doInBackground.apply(params[0]);
//        else
//            return doInBackground.apply(null);
//    }
//
//    @Override
//    public void onPostExecute(PASS param) {
//        onPostExecute.accept(param);
//    }
//
//    @Override
//    public void progressCallback(UPDATE... params) {
//        if (params.length > 0)
//            progressCallback.accept(params[0]);
//        else
//            progressCallback.accept(null);
//    }
}
