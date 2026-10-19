package src;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ProjectTask {
    private int id;
    private String name;
    private String description;
    private List<Integer> dependencies;
    private int duration;
    private LocalDate startDate;
    private LocalDate finishDate;
    private String resource;
    private boolean milestone;

    public ProjectTask(int id, String name, String description, List<Integer> dependencies,
                       int duration, LocalDate startDate, LocalDate finishDate,
                       String resource, boolean milestone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dependencies = new ArrayList<>(dependencies);
        this.duration = duration;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.resource = resource;
        this.milestone = milestone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Integer> getDependencies() { return dependencies; }
    public int getDuration() { return duration; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getFinishDate() { return finishDate; }
    public String getResource() { return resource; }
    public boolean isMilestone() { return milestone; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDependencies(List<Integer> dependencies) { this.dependencies = new ArrayList<>(dependencies); }
    public void setDuration(int duration) { this.duration = duration; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setFinishDate(LocalDate finishDate) { this.finishDate = finishDate; }
    public void setResource(String resource) { this.resource = resource; }
    public void setMilestone(boolean milestone) { this.milestone = milestone; }

    public long getCalendarDays() {
        return ChronoUnit.DAYS.between(startDate, finishDate) + 1;
    }
}
