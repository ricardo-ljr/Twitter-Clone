import org.junit.Before;
import org.junit.Test;

import edu.byu.cs.tweeter.model.domain.User;

public class StoryPagesTest {


    private String imageUrl = "https://mycs340bucket.s3.us-west-1.amazonaws.com/%40a.jpg";
    @Before
    public void setup() {

        User currUser = new User("a", "a", "@a", imageUrl);



    }

    @Test
    public void userStoryPageTest() {

    }

}
