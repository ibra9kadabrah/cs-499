#include <GLFW/glfw3.h>
#include "linmath.h"
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <vector>
#include <ctime>
#include <cmath>
#include <algorithm>


// replaced maginc numbers
const float DEG_TO_RAD = 3.14159f / 180.0f;
const int WINDOW_WIDTH = 480;
const int WINDOW_HEIGHT = 480;
const float CIRCLE_SPEED = 0.015f; 
const float CIRCLE_RADIUS_DEFAULT = 0.05f;

enum BRICKTYPE { REFLECTIVE, DESTRUCTABLE };
enum ONOFF { ON, OFF };

void processInput(GLFWwindow* window);
void updateCircles();
void drawScene();
void updatePaddle(GLFWwindow* window);

class Brick
{
private:
    float red;
    float green;
    float blue;
    float x;
    float y;
    float width;
    BRICKTYPE brick_type;
    ONOFF onoff;

public:
    // better naming
    Brick(BRICKTYPE type, float posX, float posY, float brickWidth, float r, float g, float b)
        : brick_type(type), x(posX), y(posY), width(brickWidth), red(r), green(g), blue(b), onoff(ON)
    {}

    // Method to draw the brick
    void drawBrick() const
    {
        if (onoff == ON)
        {
            float halfside = width / 2.0f;

            glColor3f(red, green, blue);
            glBegin(GL_POLYGON);

            glVertex2f(x + halfside, y + halfside);
            glVertex2f(x + halfside, y - halfside);
            glVertex2f(x - halfside, y - halfside);
            glVertex2f(x - halfside, y + halfside);

            glEnd();
        }
    }

    // Getter
    BRICKTYPE getBrickType() const { return brick_type; }

    ONOFF getOnOff() const { return onoff; }
    void setOnOff(ONOFF state) { onoff = state; }

    float getX() const { return x; }
    float getY() const { return y; }
    float getWidth() const { return width; }
};

// Circle Class
class Circle
{
public:
    float red;
    float green;
    float blue;
    float radius;
    float x;
    float y;
    float dx; 
    float dy;
    float speed;

    Circle(float posX, float posY, float rad, float velX, float velY, float spd, float r, float g, float b)
        : x(posX), y(posY), radius(rad), dx(velX), dy(velY), speed(spd), red(r), green(g), blue(b)
    {
        // normalize the velocity vector
        float length = sqrtf(dx * dx + dy * dy);
        if (length != 0.0f)
        {
            dx /= length;
            dy /= length;
        }
    }

    // significantlyy changed collisions
    void CheckCollision(Brick* brk)
    {
        if (brk->getOnOff() == OFF)
            return;

        // Get brick boundaries
        float bx = brk->getX();
        float by = brk->getY();
        float halfWidth = brk->getWidth() / 2.0f;
        float halfHeight = brk->getWidth() / 2.0f; // Assuming square bricks

        float left = bx - halfWidth;
        float right = bx + halfWidth;
        float top = by + halfHeight;
        float bottom = by - halfHeight;

        // Find the closest point on the rectangle to the circle's center
        float closestX = std::max(left, std::min(x, right));
        float closestY = std::max(bottom, std::min(y, top));

        // Compute the distance between circle's center and this closest point
        float distanceX = x - closestX;
        float distanceY = y - closestY;

        float distanceSquared = distanceX * distanceX + distanceY * distanceY;

        if (distanceSquared < radius * radius)
        {
            if (brk->getBrickType() == REFLECTIVE)
            {
                // collision normal
                float normalX = distanceX;
                float normalY = distanceY;

                float length = sqrtf(normalX * normalX + normalY * normalY);
                if (length != 0.0f)
                {
                    normalX /= length;
                    normalY /= length;
                }
                else
                {
                    normalX = 0.0f;
                    normalY = 0.0f;
                }

                // better direction bounce when hitting a brick
                float dot = dx * normalX + dy * normalY;
                dx = dx - 2.0f * dot * normalX;
                dy = dy - 2.0f * dot * normalY;

                // cahnged position to prevent sticking
                float distance = sqrtf(distanceSquared);
                float overlap = radius - distance;
                x += normalX * overlap;
                y += normalY * overlap;
            }
            else if (brk->getBrickType() == DESTRUCTABLE)
            {
                brk->setOnOff(OFF);
            }
        }
    }

    //  collision with  paddle
    void CheckCollisionWithPaddle(const class Paddle& paddle);

    // draw circle
    void DrawCircle() const
    {
        glColor3f(red, green, blue);
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, y); // center of circle
        for (int i = 0; i <= 360; i++) {
            float degInRad = i * DEG_TO_RAD;
            glVertex2f((cosf(degInRad) * radius) + x, (sinf(degInRad) * radius) + y);
        }
        glEnd();
    }
};

// Paddle Class 
class Paddle
{
public:
    float x; // Position on x-axis
    float y; // Fixed position on y-axis
    float width;
    float height;
    float red, green, blue;

    Paddle(float posX, float posY, float w, float h, float r, float g, float b)
        : x(posX), y(posY), width(w), height(h), red(r), green(g), blue(b)
    {}

    void drawPaddle() const;

    void updatePosition(GLFWwindow* window);
};


//  check collision with the paddle
void Circle::CheckCollisionWithPaddle(const Paddle& paddle)
{
    float halfWidth = paddle.width / 2.0f;
    float halfHeight = paddle.height / 2.0f;

    // circle is colliding with the paddle
    if (x + radius > paddle.x - halfWidth &&
        x - radius < paddle.x + halfWidth &&
        y - radius < paddle.y + halfHeight &&
        y + radius > paddle.y - halfHeight)
    {
        // Rereflect flect the ball 
        dy = fabsf(dy); 

        // adjust the x-direction 
        float hitPosition = (x - paddle.x) / halfWidth; // Range from -1 to 1
        dx = hitPosition; // Set dx based on hit position

        // normalize the velocity
        float length = sqrtf(dx * dx + dy * dy);
        if (length != 0.0f)
        {
            dx /= length;
            dy /= length;
        }
    }
}

void Paddle::drawPaddle() const
{
    float halfWidth = width / 2.0f;
    float halfHeight = height / 2.0f;

    glColor3f(red, green, blue);
    glBegin(GL_POLYGON);

    glVertex2f(x - halfWidth, y - halfHeight);
    glVertex2f(x + halfWidth, y - halfHeight);
    glVertex2f(x + halfWidth, y + halfHeight);
    glVertex2f(x - halfWidth, y + halfHeight);

    glEnd();
}

void Paddle::updatePosition(GLFWwindow* window)
{
    double xpos, ypos;
    glfwGetCursorPos(window, &xpos, &ypos);

    int width, height;
    glfwGetWindowSize(window, &width, &height);

    // Convert window coordinates to OpenGL coordinates (-1 to 1)
    x = ((float)xpos / width) * 2.0f - 1.0f;

    // Keep the paddle within the window boundaries
    float halfWidth = this->width / 2.0f;
    if (x - halfWidth < -1.0f)
        x = -1.0f + halfWidth;
    if (x + halfWidth > 1.0f)
        x = 1.0f - halfWidth;
}

std::vector<Circle> world;
std::vector<Brick> bricks;
bool gameOver = false;

Paddle paddle(0.0f, -0.8f, 0.3f, 0.05f, 1.0f, 1.0f, 1.0f);

//  process user input
void processInput(GLFWwindow* window)
{
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);
}

//  update paddle position
void updatePaddle(GLFWwindow* window)
{
    paddle.updatePosition(window);
}

//  update circle positions and handle collisions
void updateCircles()
{
    for (size_t i = 0; i < world.size(); i++)
    {
        Circle& circle = world[i];

        // Update position
        circle.x += circle.dx * circle.speed;
        circle.y += circle.dy * circle.speed;

        if (circle.x - circle.radius < -1.0f)
        {
            circle.x = -1.0f + circle.radius;
            circle.dx = -circle.dx;
        }
        else if (circle.x + circle.radius > 1.0f)
        {
            circle.x = 1.0f - circle.radius;
            circle.dx = -circle.dx;
        }

        if (circle.y + circle.radius > 1.0f)
        {
            circle.y = 1.0f - circle.radius;
            circle.dy = -circle.dy;
        }
        else if (circle.y - circle.radius < -1.0f)
        {
            // Ball fell below the bottom of the screen
            gameOver = true;
            return;
        }

        // Collision detection with bricks
        for (size_t j = 0; j < bricks.size(); j++)
        {
            circle.CheckCollision(&bricks[j]);
        }

        // Collision with paddle
        circle.CheckCollisionWithPaddle(paddle);
    }
}

// Function to draw all circles and bricks
void drawScene()
{
    paddle.drawPaddle();

    for (int i = 0; i < world.size(); i++)
    {
        world[i].DrawCircle();
    }

    // Draw bricks
    for (int i = 0; i < bricks.size(); i++)
    {
        bricks[i].drawBrick();
    }

    // If game over, display "Game Over"
    if (gameOver)
    {
        // Change background color to indicate game over
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f); // Red background
        glClear(GL_COLOR_BUFFER_BIT);
    }
}

int main(void)
{
    std::srand(static_cast<unsigned int>(std::time(NULL)));

    if (!glfwInit()) {
        std::cerr << "Failed to initialize GLFW." << std::endl;
        exit(EXIT_FAILURE);
    }
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
    GLFWwindow* window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Paddle Game", NULL, NULL);
    if (!window) {
        std::cerr << "Failed to create GLFW window." << std::endl;
        glfwTerminate();
        exit(EXIT_FAILURE);
    }
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);

    // Initialize bricks and add them to the bricks vector
    bricks.push_back(Brick(REFLECTIVE, 0.5f, -0.33f, 0.2f, 1.0f, 1.0f, 0.0f));
    bricks.push_back(Brick(DESTRUCTABLE, -0.5f, 0.33f, 0.2f, 0.0f, 1.0f, 0.0f));
    bricks.push_back(Brick(DESTRUCTABLE, -0.5f, -0.33f, 0.2f, 0.0f, 1.0f, 1.0f));
    bricks.push_back(Brick(REFLECTIVE, 0.0f, 0.0f, 0.2f, 1.0f, 0.5f, 0.5f));

    // Initialize the ball 
    float angle = 45.0f * DEG_TO_RAD; 
    float dx = cosf(angle);           
    float dy = sinf(angle);           

    Circle ball(0.0f, -0.7f, CIRCLE_RADIUS_DEFAULT, dx, dy, CIRCLE_SPEED, 1.0f, 1.0f, 1.0f);
    world.push_back(ball);

    while (!glfwWindowShouldClose(window)) {
        // Setup View
        int width, height;
        glfwGetFramebufferSize(window, &width, &height);
        glViewport(0, 0, width, height);
        if (!gameOver)
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
        glClear(GL_COLOR_BUFFER_BIT);

        processInput(window);

        if (!gameOver)
        {
            // Update paddle position
            updatePaddle(window);

            // Update circles and handle collisions
            updateCircles();
        }

        drawScene();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    glfwDestroyWindow(window);
    glfwTerminate();
    exit(EXIT_SUCCESS);
}