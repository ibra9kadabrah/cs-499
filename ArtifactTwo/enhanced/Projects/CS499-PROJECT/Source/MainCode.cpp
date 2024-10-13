#include <GLFW/glfw3.h>
#include "linmath.h"
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <vector>
#include <ctime>
#include <cmath>
#include <algorithm>
#include <fstream>
#include <tuple>

// constants
const float DEG_TO_RAD = 3.14159f / 180.0f;
const int WINDOW_WIDTH = 480;
const int WINDOW_HEIGHT = 480;
const float INITIAL_SPEED = 0.01f;         // starting speed
const float SPEED_INCREMENT = 0.001f;       // speed increase per level
const float CIRCLE_RADIUS_DEFAULT = 0.05f;

//  brick Types and states
enum BRICKTYPE { REFLECTIVE, DESTRUCTABLE };
enum ONOFF { ON, OFF };

// game states
enum GAMESTATE { PLAYING, LEVEL_COMPLETED, GAME_OVER };
GAMESTATE gameState = PLAYING;


int currentLevel = 1;

double startTime = 0.0;   
double endTime = 0.0;     
double duration = 0.0;    
double recordTime = 0.0;  


void processInput(GLFWwindow* window);
void updateCircles();
void drawScene();
void updatePaddle(GLFWwindow* window);
void generateBricks(int level);
double readRecord();
void writeRecord(double newRecord);

// brick Class 
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
    // Constructor
    Brick(BRICKTYPE type, float posX, float posY, float brickWidth, float r, float g, float b)
        : brick_type(type), x(posX), y(posY), width(brickWidth), red(r), green(g), blue(b), onoff(ON)
    {}

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

    BRICKTYPE getBrickType() const { return brick_type; }
    ONOFF getOnOff() const { return onoff; }
    void setOnOff(ONOFF state) { onoff = state; }
    float getX() const { return x; }
    float getY() const { return y; }
    float getWidth() const { return width; }
};

class Paddle;

// circle (Ball) class 
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

    // Constructor
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

    void CheckCollision(Brick* brk)
    {
        if (brk->getOnOff() == OFF)
            return;

        // Get brick boundaries
        float bx = brk->getX();
        float by = brk->getY();
        float halfWidth = brk->getWidth() / 2.0f;
        float halfHeight = brk->getWidth() / 2.0f;

        float left = bx - halfWidth;
        float right = bx + halfWidth;
        float top = by + halfHeight;
        float bottom = by - halfHeight;

        float closestX = std::max(left, std::min(x, right));
        float closestY = std::max(bottom, std::min(y, top));

        // calculate the distance between circle's center and this closest point
        float distanceX = x - closestX;
        float distanceY = y - closestY;

        float distanceSquared = distanceX * distanceX + distanceY * distanceY;

        if (distanceSquared < radius * radius)
        {
            if (brk->getBrickType() == REFLECTIVE)
            {
                
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

                // Reflect the velocity vector
                float dot = dx * normalX + dy * normalY;
                dx = dx - 2.0f * dot * normalX;
                dy = dy - 2.0f * dot * normalY;

                // adjust position to prevent sticking, it is a problem i encountered with the ball when testing.
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

    void CheckCollisionWithPaddle(const Paddle& paddle);

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

// paddle class
class Paddle
{
public:
    float x; 
    float y; 
    float width;
    float height;
    float red, green, blue;

    // Constructor
    Paddle(float posX, float posY, float w, float h, float r, float g, float b)
        : x(posX), y(posY), width(w), height(h), red(r), green(g), blue(b)
    {}

    void drawPaddle() const
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

    // update paddle pos based on mouse
    void updatePosition(GLFWwindow* window)
    {
        double xpos, ypos;
        glfwGetCursorPos(window, &xpos, &ypos);

        int windowWidth, windowHeight;
        glfwGetWindowSize(window, &windowWidth, &windowHeight);

        // convert window coordinates to OpenGL coordinates
        x = ((float)xpos / windowWidth) * 2.0f - 1.0f;

        // keep paddle in window
        float halfWidth = this->width / 2.0f;
        if (x - halfWidth < -1.0f)
            x = -1.0f + halfWidth;
        if (x + halfWidth > 1.0f)
            x = 1.0f - halfWidth;
    }
};

void Circle::CheckCollisionWithPaddle(const Paddle& paddle)
{
    float halfWidth = paddle.width / 2.0f;
    float halfHeight = paddle.height / 2.0f;

    if (x + radius > paddle.x - halfWidth &&
        x - radius < paddle.x + halfWidth &&
        y - radius < paddle.y + halfHeight &&
        y + radius > paddle.y - halfHeight)
    {
        // reflect the ball vertically
        dy = fabsf(dy);

        // horizontal reflection
        float hitPosition = (x - paddle.x) / halfWidth; // Range from -1 to 1
        dx = hitPosition; // Set dx based on hit position

        // Normalize the velocity vector
        float length = sqrtf(dx * dx + dy * dy);
        if (length != 0.0f)
        {
            dx /= length;
            dy /= length;
        }
    }
}

std::vector<Circle> world; 
std::vector<Brick> bricks;  
bool gameOver = false;    

Paddle paddle(0.0f, -0.8f, 0.3f, 0.05f, 1.0f, 1.0f, 1.0f);

// read user record from file
double readRecord() {
    std::ifstream infile("record.txt");
    double record = 0.0;
    if (infile.is_open()) {
        infile >> record;
        infile.close();
    }
    return record;
}

// write user record to file
void writeRecord(double newRecord) {
    std::ofstream outfile("record.txt");
    if (outfile.is_open()) {
        outfile << newRecord;
        outfile.close();
    }
}

void processInput(GLFWwindow* window)
{
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);

    if (gameState == GAME_OVER && glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS)
    {
        currentLevel = 1;
        gameState = PLAYING;
        generateBricks(currentLevel);

        world.clear();
        float angle = 45.0f * DEG_TO_RAD;
        float dx = cosf(angle);
        float dy = sinf(angle);
        Circle ball(0.0f, -0.7f, CIRCLE_RADIUS_DEFAULT, dx, dy, INITIAL_SPEED, 1.0f, 1.0f, 1.0f);
        world.push_back(ball);

        startTime = glfwGetTime();
    }
}

void updatePaddle(GLFWwindow* window)
{
    paddle.updatePosition(window);
}

// procedural brick generation
void generateBricks(int level) {
    bricks.clear(); 

    // Define the grid dimensions
    const int rows = 5;
    const int cols = 8;
    const float gridWidth = 1.8f;
    const float gridHeight = 0.6f;
    const float startX = -0.9f;
    const float startY = 0.7f;

    float brickWidth = gridWidth / cols;
    float brickHeight = gridHeight / rows;

    int numBricks;
    if (level == 1) {
        numBricks = 3;
    }
    else {
        numBricks = 3 + (level - 1) * 2; // 3, 5, 7, 9, 11 for levels 1-5
    }
    numBricks = std::min(numBricks, rows * cols); // Cap at grid size

    //  all possible brick pos
    std::vector<std::pair<int, int>> positions;
    for (int r = 0; r < rows; ++r) {
        for (int c = 0; c < cols; ++c) {
            positions.emplace_back(r, c);
        }
    }

    // change the positions to randomize brick placement
    std::random_shuffle(positions.begin(), positions.end());

    int destructibleCount = 0;

    std::vector<std::tuple<float, float, float>> reflectiveColors = {
        {1.0f, 0.0f, 0.0f},   // Red
        {0.0f, 0.0f, 1.0f},   // Blue
        {1.0f, 1.0f, 0.0f},   // Yellow
        {1.0f, 0.0f, 1.0f},   // Magenta
        {0.0f, 1.0f, 1.0f},   // Cyan
        {1.0f, 0.5f, 0.0f},   // Orange
        {0.5f, 0.0f, 0.5f},   // Purple
        {0.5f, 0.5f, 0.5f}    // Grey
    };


    for (int i = 0; i < numBricks; i++) {
        int row = positions[i].first;
        int col = positions[i].second;

        float x = startX + (col + 0.5f) * brickWidth;
        float y = startY - (row + 0.5f) * brickHeight;

        // ensure bricks do not go outside the vertical bounds
        if (y + brickHeight / 2.0f > 1.0f - 0.1f) 
            y = 1.0f - 0.1f - brickHeight / 2.0f;
        if (y - brickHeight / 2.0f < -0.5f) 
            y = -0.5f + brickHeight / 2.0f;

        BRICKTYPE type;
        if (level <= 3) {
            // In early levels, ensure a certain distribution (i noticed layout too repetitive early on)
            if (i % 3 == 0)
                type = DESTRUCTABLE;
            else
                type = REFLECTIVE;
        }
        else {
            // In higher levels, assign types with increasing destructible probability
            type = (rand() % 100 < (30 + level * 2)) ? DESTRUCTABLE : REFLECTIVE;
        }

        if (type == DESTRUCTABLE)
            destructibleCount++;

        // assign colors based on brick type
        float r, g, b;
        if (type == REFLECTIVE) {
            int colorIndex = rand() % reflectiveColors.size();
            r = std::get<0>(reflectiveColors[colorIndex]);
            g = std::get<1>(reflectiveColors[colorIndex]);
            b = std::get<2>(reflectiveColors[colorIndex]);

            if (g > 0.5f && r < 0.2f && b < 0.2f) {
                colorIndex = rand() % reflectiveColors.size();
                r = std::get<0>(reflectiveColors[colorIndex]);
                g = std::get<1>(reflectiveColors[colorIndex]);
                b = std::get<2>(reflectiveColors[colorIndex]);
            }
        }
        else if (type == DESTRUCTABLE) {
            r = 0.0f;
            g = 1.0f;
            b = 0.0f;
        }

        bricks.emplace_back(type, x, y, brickWidth * 0.9f, r, g, b);
    }
}

void updateCircles()
{
    for (size_t i = 0; i < world.size(); i++)
    {
        Circle& circle = world[i];

        circle.x += circle.dx * circle.speed;
        circle.y += circle.dy * circle.speed;

        // collision with left and right walls
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

        // collision with top wall
        if (circle.y + circle.radius > 1.0f)
        {
            circle.y = 1.0f - circle.radius;
            circle.dy = -circle.dy;
        }
        // ball falls below the paddle
        else if (circle.y - circle.radius < -1.0f)
        {
            // check if the user has completed 5 levels
            if (currentLevel > 5) {
                gameState = GAME_OVER;
                endTime = glfwGetTime();
                duration = endTime - startTime;

                recordTime = readRecord();

                std::cout << "Congratulations! You completed 5 levels in " << duration << " seconds." << std::endl;

                if (duration < recordTime || recordTime == 0.0) {
                    std::cout << "New Record! Well done!" << std::endl;
                    writeRecord(duration);
                }
                else {
                    std::cout << "Your record: " << recordTime << " seconds." << std::endl;
                }
            }
            else {
                // user failed before completing 5 levels
                gameState = GAME_OVER;
                endTime = glfwGetTime();
                duration = endTime - startTime;

                std::cout << "Game Over! You lasted " << duration << " seconds and completed " << (currentLevel - 1) << " levels." << std::endl;

            }
            return;
        }

        for (size_t j = 0; j < bricks.size(); j++)
        {
            circle.CheckCollision(&bricks[j]);
        }

        circle.CheckCollisionWithPaddle(paddle);
    }
}

void drawScene()
{
    if (gameState == PLAYING)
    {
        paddle.drawPaddle();

        for (int i = 0; i < world.size(); i++)
        {
            world[i].DrawCircle();
        }

        for (int i = 0; i < bricks.size(); i++)
        {
            bricks[i].drawBrick();
        }
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

    generateBricks(currentLevel);

    // initialize the ball
    float angle = 45.0f * DEG_TO_RAD;
    float dx = cosf(angle);
    float dy = sinf(angle);

    Circle ball(0.0f, -0.7f, CIRCLE_RADIUS_DEFAULT, dx, dy, INITIAL_SPEED, 1.0f, 1.0f, 1.0f);
    world.push_back(ball);

    startTime = glfwGetTime();

    // game loop
    while (!glfwWindowShouldClose(window)) {
        
        int width, height;
        glfwGetFramebufferSize(window, &width, &height);
        glViewport(0, 0, width, height);

        if (gameState == PLAYING)
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        else if (gameState == LEVEL_COMPLETED)
            glClearColor(0.0f, 1.0f, 0.0f, 1.0f); 
        else if (gameState == GAME_OVER)
            glClearColor(1.0f, 0.0f, 0.0f, 1.0f); 
        glClear(GL_COLOR_BUFFER_BIT);

        // Process User Input
        processInput(window);

        if (gameState == PLAYING)
        {
            updatePaddle(window);

            updateCircles();

            if (gameState != PLAYING) 
                continue;

            // check if won
            bool allBricksDestroyed = true;
            for (const auto& brick : bricks) {
                if (brick.getOnOff() == ON && brick.getBrickType() == DESTRUCTABLE) {
                    allBricksDestroyed = false;
                    break;
                }
            }

            if (allBricksDestroyed) {
                if (currentLevel == 5) {
                    // user  completed level 5
                    gameState = GAME_OVER;
                    endTime = glfwGetTime();
                    duration = endTime - startTime;

                    // read the existing record
                    recordTime = readRecord();

                    std::cout << "Congratulations! You completed 5 levels in " << duration << " seconds." << std::endl;

                    // check if new record
                    if (duration < recordTime || recordTime == 0.0) {
                        std::cout << "New Record! Well done!" << std::endl;
                        writeRecord(duration);
                    }
                    else {
                        std::cout << "Your record: " << recordTime << " seconds." << std::endl;
                    }
                }
                else {
                    gameState = LEVEL_COMPLETED;
                }
            }
        }
        else if (gameState == LEVEL_COMPLETED)
        {
            // users clicks space to go next level
            if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
                // Increment level
                currentLevel++;

                float newSpeed = INITIAL_SPEED + SPEED_INCREMENT * (currentLevel - 1);

                // generate bricks with new procedural generation algorithm
                generateBricks(currentLevel);

                world.clear();
                float newAngle = 45.0f * DEG_TO_RAD;
                float newDx = cosf(newAngle);
                float newDy = sinf(newAngle);
                Circle newBall(0.0f, -0.7f, CIRCLE_RADIUS_DEFAULT, newDx, newDy, newSpeed, 1.0f, 1.0f, 1.0f);
                world.push_back(newBall);

                gameState = PLAYING;
            }
        }

        drawScene();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    glfwDestroyWindow(window);
    glfwTerminate();
    exit(EXIT_SUCCESS);
}

 