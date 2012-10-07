import de.bezier.data.sql.*;

// database
PostgreSQL pgsql;
String host = "db";
String database = "g1136221_u";
String user = "g1136221_u";
String pass = "J2ezbmSgff";

ArrayList<Comment> comments = new ArrayList<Comment>();
Comment stoppedComment = null;

// parameters
int score = 0;
int topic = 15;

PFont f = createFont("Georgia", 36, true);

void setup() {
  size(1500, 800);
  frameRate(100);

  pgsql = new PostgreSQL( this, host, database, user, pass );
  if (!pgsql.connect()) {
    println("Could not connect to database");
  }

  // get parameters
  score = Integer.parseInt(param("score"));
  topic = Integer.parseInt(param("topic"));
  getCommentsFromDB(topic);
}

void draw() {
  background(255);
  fill(0);

  boolean mouseInsideAComment = false;
  // move all comments
  for (Comment c : comments) {
    // Look at each word
    String s = c.commentText;

    textFont(f, c.fontSize);
    if (c.sentiment.equals("neutral")) {
      fill(0, 0, 0);
    }
    else if (c.sentiment.equals("positive")) {
      fill(0, 255, 0);
    }
    else if (c.sentiment.equals("negative")) {
      fill(255, 0, 0);
    }
    text(s, c.x, c.y);
    // Move along the x-axis
    c.x += c.speed;

    // If x gets to the end, move Y
    if (c.x > width) {
      c.x = 0;
    }

    c.speed = c.storedSpeed;
    // if mouse inside a comment, stop it
    if (c.contains(mouseX, mouseY)) {
      mouseInsideAComment = true;
      // if this is a new stopped comment, start the old one moving again
      if (stoppedComment != null && c != stoppedComment) {
        stoppedComment.restoreSpeed();
      }
      stoppedComment = c;
      c.speed = 0;
    }
  }
}

void getCommentsFromDB(int topic) {
  // get comments
  pgsql.query("SELECT * FROM comment WHERE topic = " + topic);
  while ( pgsql.next () )
  {
    Comment c = new Comment(pgsql.getInt("id"), pgsql.getInt("topic"), pgsql.getString("commenter"), pgsql.getString("text"), pgsql.getInt("level"), pgsql.getInt("parent"), pgsql.getString("agreeing"), pgsql.getString("sentiment"));
    comments.add(c);
  }
}

class Comment {
  float x, y;
  int id;
  int topic;
  String commenter;
  String commentText;
  int level;
  int parent;
  boolean agreeing = false;
  String sentiment;
  int fontSize;
  float speed;
  float storedSpeed;

  Comment(int tempId, int tempTopic, String tempCommenter, String tempCommentText, int tempLevel, int tempParent, String tempAgreeing, String tempSent) {
    id = tempId;
    topic = tempTopic;
    commenter = tempCommenter;
    commentText = tempCommentText;
    level = tempLevel;
    parent = tempParent;
    if (tempAgreeing.equals("t")) {
      agreeing = true;
    }
    sentiment = tempSent;

    x = random(0, width - textWidth(commentText));
    y = random(0, height - 50);

    fontSize = round(random(10, height/8));
    speed = random(2, 5);
    storedSpeed = speed;
  }

  void restoreSpeed() {
    speed = storedSpeed;
  }

  boolean contains(int tempX, int tempY) {
    return tempX > x && tempY > y - fontSize && tempX < x + textWidth(commentText) && tempY < y;
  }
} // end comment class

