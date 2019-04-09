package titan.ccp.stats.streamprocessing;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class WeekdayKeyFactory implements StatsKeyFactory<WeekdayKey> {

  @Override
  public WeekdayKey create(final String sensorId, final LocalDateTime dateTime) {
    final DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
    return new WeekdayKey(dayOfWeek, sensorId);
  }

}
