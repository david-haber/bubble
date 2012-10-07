import de.bezier.data.sql.*;

// database
PostgreSQL pgsql;
String host = "db";
String database = "g1136221_u";
String user = "g1136221_u";
String pass = "J2ezbmSgff";

// parameters
int topic = 56;

String topicName = "";

ArrayList<Circle> circles = new ArrayList<Circle>();
HashMap<Integer, ArrayList<Circle>> childrenMap = new HashMap<Integer, ArrayList<Circle>>();
Circle circleClicked;

// text
TextBox textBox;
PFont font = createFont("Georgia", 24, true);

int BASE_RADIUS = 60;

public void setup() {
  size(1500, 800);
  frameRate(500);
  // get parameters
  topic = Integer.parseInt(param("topic"));
  // connect to database
  pgsql = new PostgreSQL( this, host, database, user, pass );
  textFont(font, 50);
  if (!pgsql.connect()) {
    println("Could not connect to database");
  }
  // draw
  drawBackground();
  drawText();
  getCommentsFromDB(topic);
  // Create Text Box
  textBox = new TextBox(100, height-200, width - 200, 100);
}

public void draw() {
  drawBackground();
  drawText();
  for (Circle c : circles) {
    if(circleClicked == null || c.isDisplayed) {
      c.updatePos();
      c.display();
    }
  }
  textBox.display();
  smooth();

  // if the mouse is hovering over a circle, display the comment in the textbox
  for (Circle c : circles) {
    if (c.contains(mouseX, mouseY)) {
      String t = c.comment.commenter + ": " + c.comment.commentText;
      textBox.setText(t);
    }
  }

  checkForCollisions();
}

public void mousePressed() {

  // if click was not inside a circle, set circleClicked to null and update isDisplayed for it and its children
  if (circleClicked != null) {
    circleClicked.isDisplayed = false;
    if (childrenMap.get(circleClicked.comment.id) != null) {
      for (Circle child : childrenMap.get(circleClicked.comment.id)) {
        child.isDisplayed = false;
      }
    }
  }
  
  Circle newCircleClicked = null;
  // if click was inside a circle, only display that circle and its children
  for (Circle c : circles) {
    if (c.contains(mouseX, mouseY) && (c.isDisplayed || circleClicked == null)) {
      newCircleClicked = c;
      c.isDisplayed = true;
      if (childrenMap.get(c.comment.id) != null) {
        for (Circle child : childrenMap.get(c.comment.id)) {
          child.isDisplayed = true;
        }
      }
    }
  }

  circleClicked = newCircleClicked; 
}


/*
* Background methods
 */

void drawBackground() {
  background(155,215,213);
}

void drawBubblesBackground() {
  PImage b;
  b = loadImage("underwater.jpg");
  b.resize(width, height);
  background(b);
}

void drawItalianBackground() {
  // Green section
  for (int i = 0; i < width / 2; i++) {
    stroke(0, 255, 0, 400 - i);
    rect(i, 0, 1, height);
  }
  // Red section
  for (int i = width / 2; i < width; i++) {
    stroke(255, 0, 0, 400 - width + i);
    rect(i, 0, 1, height);
  }
}

void drawGreenRedBackground() {
  noStroke();
  // Green section
  fill(0, 255, 0);
  rect(0, 0, width / 2, height);
  // Red section
  fill(255, 0, 0);
  rect(width / 2, 0, width / 2, height);
}

private void drawText() {
  textFont(font, 50);
  
  // get title
  pgsql.query("SELECT title FROM topic WHERE id = " + topic);
  if(pgsql.next()) {
    topicName = pgsql.getString("title");
  }
  else {
    println("No topic name found.");
  }
  fill(0, 0, 0);
  
  textAlign(CENTER, TOP);
  text(topicName, width/2, 0);
}

/*
* Helper methods
 */

private void checkForCollisions() {
  for (int i = 0; i < circles.size(); i++) {
    Circle currCircle = circles.get(i);
    for (int j = 0; j < circles.size(); j++) {
      if (circles.get(j) != currCircle) {
        if (circlesCollide(currCircle.x, currCircle.y, currCircle.r/2, circles.get(j).x, circles.get(j).y, circles.get(j).r/2)) {
          currCircle.reverseDirection();
        }
      }
    }
  }
}

boolean circlesCollide(float x1, float y1, float r1, float x2, float y2, float r2) {
  float distanceBetweenCircles = dist(x1, y1, x2, y2);
  float sumOfRadii = r1+r2;
  return (distanceBetweenCircles < sumOfRadii);
}

private void getCommentsFromDB(int topic) {
  // get comments
  pgsql.query("SELECT * FROM comment WHERE topic = " + topic);
  while ( pgsql.next () )
  {
    Comment c = new Comment(pgsql.getInt("id"), pgsql.getInt("topic"), pgsql.getString("commenter"), pgsql.getString("text"), pgsql.getInt("level"), pgsql.getInt("parent"), pgsql.getString("agreeing"), pgsql.getString("sentiment"), pgsql.getFloat("rank"));
    // size of circle directly proportional to rank and inversely proportional to number of comments
    float r = BASE_RADIUS * (c.rank + 1) / (circles.size() + 1);
    float x, y;
    do {
      x = random(r, width - r);
      y = random(r, height);
    } while (!canCreateCircle (x, y, r));
    createCircle(x, y, r, c);
  }
}

// returns the Circle linked to the Comment with id id
Circle findComment(int id) {
  for (Circle c : circles) {
    if (c.comment.id == id) {
      return c;
    }
  }
  return null;
}

boolean isGreen(float x, float y) {
  return x > 0 && x < width / 2;
}

boolean canCreateCircle(float x, float y, float r) {
  // if inside an existing circle, return false
  for (int i = 0; i < circles.size(); i++) {
    Circle c = circles.get(i);
    if (circlesCollide(c.x, c.y, c.r, x, y, r)) {
      return false;
    }
  }
  return true;
}

void createCircle(float x, float y, float r, Comment comment) {
  // Create new circle
  Circle circle = new Circle(x, y, r, comment);
  circles.add(circle);
}

/*
*
 * Comment
 *
 */

class Comment {
  int id;
  int topic;
  String commenter;
  String commentText;
  int level;
  int parent;
  boolean agreeing = false;
  String sentiment;
  float rank;

  Comment(int tempId, int tempTopic, String tempCommenter, String tempCommentText, int tempLevel, int tempParent, String tempAgreeing, String tempSent, float tempRank) {
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
    rank = tempRank;
  }
} // end comment class


/*
*
 * Circle
 *
 */

double DEFAULT_X_SPEED = 3;
double DEFAULT_Y_SPEED = 6;

class Circle {
  float x, y; // location
  float r; // radius
  double xspeed = DEFAULT_X_SPEED;
  double yspeed = DEFAULT_Y_SPEED;
  int xdirection; // Left or Right
  int ydirection; // Top to Bottom
  float transparency = 150;
  Comment comment;
  boolean isDisplayed = false;

  Circle(float tempX, float tempY, float tempR, Comment tempC) {
    x = tempX;
    y = tempY;
    r = tempR;
    comment = tempC;

    ydirection = randomDirection();
    xdirection = randomDirection();

    // update childrenMap
    if (comment.parent != 0) {
      ArrayList<Circle> children = childrenMap.get(comment.parent);
      if (children == null) {
        children = new ArrayList<Circle>();
        childrenMap.put(comment.parent, children);
      }
      // add this comment as a child of its parent (into childrenMap)
      children.add(this);
    }
  }

  void display() {
    // change colour depending on sentiment
    fill(100, 200, 220, transparency);
    if (comment.sentiment.equals("positive")) {
      noStroke();
      fill(0, 255, 0, transparency);
    }
    else if (comment.sentiment.equals("negative")) {
      noStroke();
      fill(255, 0, 0, transparency);
    }
    else if (comment.sentiment.equals("neutral")) {
      transparency = 0;
      stroke(255, 255, 255);
      fill(100, 200, 220, transparency);
    }
    ellipse(x, y, r, r);
  }

  int randomDirection() {
    int direction = round(random(-1, 1));
    if (direction == 0) {
      direction = -1;
    }
    return direction;
  }

  void updatePos() {
    // Update the position of the shape
    x += xspeed * xdirection;
    y += yspeed * ydirection;

    // Test to see if the shape exceeds the boundaries of the screen
    // If it does, reverse its direction by multiplying it by -1
    if (x < r/2 || x > width) {
      xdirection *= -1;
    }
    if (y < r/2 || y > height) {
      ydirection *= -1;
    }
  }

  // return true if the circle contains the point with coordinates (tempX, tempY)
  boolean contains(int tempX, int tempY) {
    return dist(x, y, tempX, tempY) < r;
  }

  void reverseDirection() {
    xdirection *= -1;
    ydirection *= -1;
  }
} // end Circle class



/*
*
 * TextBox
 *
 */

public class TextBox {
  float w;
  float h;
  float x;
  float y;

  private String boxText = "Hover over a bubble to view the comment";

  private PFont font;

  public TextBox (float tempX, float tempY, float tempW, float tempH) {
    x = tempX;
    y = tempY;
    w = tempW;
    h = tempH;
    font = createFont("Georgia", 100, true);
  }

  void display() {
    drawBackground();
    drawText();
  }

  void drawBackground() {
    smooth();
    noStroke();
    fill(0, 90);
    rect(x, y, w, h);
  }

  void drawText() {
    textFont(this.font, 40);
    fill(255);
    textAlign(CENTER);
    text(boxText, x, y, x+w, y+h);
  }

  void setText(String tempBoxText) {
    boxText = tempBoxText;
  }
} // end TextBox class

