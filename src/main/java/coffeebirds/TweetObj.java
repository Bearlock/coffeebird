package coffeebirds;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * Used to create, access, and modify twitter API response objects
 * Maintains the following data for each tweet: text, created_at, name, screen_name
 * @author Ryan Huntbach, Ben Berntson
 */
public class TweetObj {

    @JsonProperty("text")
    private String text;
    @JsonProperty("created_at")
    private String created_at;
    @JsonProperty("name")
    private String name;
    @JsonProperty("scree_name")
    private String screen_name;
    @JsonProperty("id_str")
    private String id_str;
    @JsonProperty("id")
    private long id;
    @JsonProperty("source")
    private String source;

    public String getId_str(){return id_str;}

    public void setId_str(String id_str){this.id_str = id_str;}

    public long getId(){return id;}

    public void setId(long id){this.id = id;}

    /**
     * @author Ben Berntson
     * @return the source of the tweet (e.g. android app, ios app, web browser on pc)
     */
    public String getSource(){return source;}

    /**
     * @author Ben Berntson
     *
     * @param source
     */
    public void setSource(String source){this.source = source;}


    /**Getter for "text" data member
     * @author Ryan Huntbach
     * @param
     * @return text (String)
     */
    protected String getText() {
        return text;
    }

    /**Setter for "text" data member
     * @author Ryan Huntbach
     * @param text (String)
     * @return void
     */
    protected void setText(String text) {
        this.text = text;
    }


    /**Getter for "created_at" data member
     * @author Ryan Huntbach
     * @param
     * @return created_at (String)
     */
    protected String getCreated_at() {
        return created_at;
    }

    /**Setter for "created_at" data member
     * @author Ryan Huntbach
     * @param created_at (String)
     * @return void
     */
    protected void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**Getter for "name" data member
     * @author Ryan Huntbach
     * @param
     * @return name (String)
     */
    protected String getName() {
        return name;
    }

    /**Setter for "name" data member
     * @author Ryan Huntbach
     * @param name (String)
     * @return void
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**Getter for "screen_name" data member
     * @author Ryan Huntbach
     * @param
     * @return screen_name (String)
     */
    protected String getScreen_name() {
        return screen_name;
    }

    /**Setter for "screen_name" data member
     * @author Ryan Huntbach
     * @param screen_name
     * @return void
     */
    protected void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

}