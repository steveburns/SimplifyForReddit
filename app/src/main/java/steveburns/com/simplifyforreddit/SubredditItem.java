package steveburns.com.simplifyforreddit;

/**
 * Created by sburns.
 */
public class SubredditItem {

    private Long id;
    private String name;

    public Long getId() { return id; }
    public String getName() { return name; }

    public SubredditItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}