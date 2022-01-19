package billboards.shared;

public class Stopwatch{

    public Stopwatch(){
        refreshTime();
    }

    private long time;

    public void refreshTime(){
        time = System.currentTimeMillis();
    }

    public void pushTime(int ms){
        time -= ms;
    }

    public long getTime(){
        return System.currentTimeMillis() - time;
    }

    public long pollTime(){
        long ret = getTime();
        refreshTime();
        return ret;
    }

    public boolean triggered(long triggerMS){
        return triggerMS < getTime();
    }

    public void refreshEvery(long ms){
        if(time > ms) { refreshTime(); }
    }

    @Override
    public String toString(){
        long ms = getTime();

        if(ms < 50000) return ms + " milliseconds";

        long seconds = (ms / 1000) % 60;
        long minutes = ((ms / 1000) / 60) % 60;
        long hours = ((ms / 1000) / 60) / 60;

        return String.format("Stopwatch: %02d:%02d:%02d", hours ,minutes, seconds);
    }

    public String toString(String function){
        long ms = getTime();

        if(ms < 50000) return function + ": "  + ms;

        long seconds = (ms / 1000) % 60;
        long minutes = ((ms / 1000) / 60) % 60;
        long hours = ((ms / 1000) / 60) / 60;

        return String.format("%s: %02d:%02d:%02d", function, hours ,minutes, seconds);
    }
}

