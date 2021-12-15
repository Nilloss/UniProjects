package billboards.shared;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This contains helper methods for identifying which billboard should take precedence
 * it is used in control panel for highlighting the displayed billboard and in the viewer request method to retrieve the display content
 * @author  Jack Nielsen
 */
public class Schedule {

    /**
     * Feed this the schedule table data and it returns the schedule id for the schedule which is currently active
     * and is taking precedence is there are multiple active schedules
     * @param scheduleData
     * @return
     */
    public static String getCurrentlyScheduledId(String[][] scheduleData){
        if(scheduleData == null){
            System.out.println("No currently active schedule");
        }

        ArrayList<LocalDateTime> toSort = new ArrayList<>();
        HashMap<LocalDateTime,String> activeSchedules = new HashMap<>();
        for(String[] s : scheduleData){
            String scheduleId = s[0];
            LocalDateTime startTime = getDateTime(s[2]);
            LocalDateTime endTime = startTime.plusMinutes(Integer.parseInt(s[3]));
            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(startTime) && now.isBefore(endTime)){
                toSort.add(startTime);
                activeSchedules.put(startTime,scheduleId);
            }
        }
        Collections.sort(toSort);
        return activeSchedules.get(toSort.get(toSort.size()-1));
    }

    /**
     * This returns the index of the billboard that should be displayed
     * @param scheduleData
     * @return
     */
    public static int getCurrentlyScheduledIndex(String[][] scheduleData){
        String scheduledId = getCurrentlyScheduledId(scheduleData);

        for(int i = 0; i < scheduleData.length; i++){
            if(scheduleData[i][0].equals(scheduledId)){
                return i;
            }
        }
        return 0;
    }

    /**
     * Converts dateTime into String
     * @param ldt
     * @return
     */
    public static String dateTimeToString(LocalDateTime ldt){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ldt.format(format);
    }

    /**
     * Converts string to date time
     * @param field
     * @return
     */
    public static LocalDateTime getDateTime(String field){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(field.substring(0,19),format);
    }
}
