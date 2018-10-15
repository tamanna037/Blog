package newbie.learingfirebase.myblog;

/**
 * Created by MiNNiE on 28-Feb-17.
 */
public class Blog {

    private String title,description,image,username;

    public Blog()
    {}

    public Blog(String title,String desc,String image)
    {
        this.title=title;
        this.description=desc;
        this.image=image;

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
