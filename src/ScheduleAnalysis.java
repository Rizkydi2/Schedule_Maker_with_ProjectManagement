package src;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ScheduleAnalysis {
    public static class Result {
        public final Map<Integer, Integer> earlyStart = new HashMap<>();
        public final Map<Integer, Integer> earlyFinish = new HashMap<>();
        public final Map<Integer, Integer> lateStart = new HashMap<>();
        public final Map<Integer, Integer> lateFinish = new HashMap<>();
        public final Map<Integer, Integer> floatDays = new HashMap<>();
        public final List<Integer> criticalPath = new ArrayList<>();
        public int projectDuration;
    }

    public static Result analyze(List<ProjectTask> tasks) {
        Result r = new Result();
        List<ProjectTask> ordered = new ArrayList<>(tasks);
        ordered.sort(Comparator.comparingInt(ProjectTask::getId));

        for (ProjectTask t : ordered) {
            int es = 0;
            for (Integer dep : t.getDependencies()) {
                es = Math.max(es, r.earlyFinish.getOrDefault(dep, 0));
            }
            int ef = es + Math.max(1, t.getDuration());
            r.earlyStart.put(t.getId(), es);
            r.earlyFinish.put(t.getId(), ef);
        }

        r.projectDuration = ordered.stream().mapToInt(t -> r.earlyFinish.get(t.getId())).max().orElse(0);

        Map<Integer, List<ProjectTask>> successors = new HashMap<>();
        for (ProjectTask t : ordered) {
            for (Integer dep : t.getDependencies()) {
                successors.computeIfAbsent(dep, k -> new ArrayList<>()).add(t);
            }
        }

        List<ProjectTask> reverse = new ArrayList<>(ordered);
        Collections.reverse(reverse);
        for (ProjectTask t : reverse) {
            List<ProjectTask> next = successors.getOrDefault(t.getId(), Collections.emptyList());
            int lf;
            if (next.isEmpty()) lf = r.projectDuration;
            else {
                lf = Integer.MAX_VALUE;
                for (ProjectTask s : next) lf = Math.min(lf, r.lateStart.get(s.getId()));
            }
            int ls = lf - Math.max(1, t.getDuration());
            r.lateFinish.put(t.getId(), lf);
            r.lateStart.put(t.getId(), ls);
            r.floatDays.put(t.getId(), ls - r.earlyStart.get(t.getId()));
        }

        for (ProjectTask t : ordered) {
            if (r.floatDays.get(t.getId()) == 0) r.criticalPath.add(t.getId());
        }
        return r;
    }

    public static int workingDuration(LocalDate start, LocalDate finish) {
        return (int) (ChronoUnit.DAYS.between(start, finish) + 1);
    }
}
