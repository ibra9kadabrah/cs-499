# CS-499 Capstone ePortfolio

## Professional assesment
I began my educational journey at Southern New Hampshire University (SNHU) in January 2023, after taking time off from my previous university to prioritize my mental health. I have been passionate about computers ever since I was a child and always loved to tinker with them. My passion for computers is the main reason I decided to pursue a bachelor's degree in computer science with the added goal of pursuing a masters degree to further my education in this field. I am mainly passionate about graphics, game development, and simulations, which I hope to contribute to in my advanced education and career. During my studies, I did not just learn technical skills, but also learned about personal growth.

“What a computer is to me is it's the most remarkable tool that we've ever come up with, and it's the equivalent of a bicycle for our minds.” — Steve Jobs

During my time at SNHU, I've learned important skills as researching and citing sources, organizing my thoughts into a structured argument, and developing time management skills, which are even more important in my case since I was doing double the course load for most of my terms.

Through my coursework such as CS-360 app development, I learned about native android development, which was overwhelming at first since it was a new architecture, new development enviornment and so on. I learned the importance of adhering to design guidlines, since different android phones have different hardware and software specs. Thankfully my prior experience with java, which I learned in a different SNHU course, was quite helpful. Another course Of note is CS-310 collaborative development, in which we worked as a class on a single project, where we had to learn how to merge, solve conflicts, and pass a code review. I believe this course gave me a glimpse into a real-world work scenario, with collaboration between teams in an async manner, which will be beneficial in professional enviornment. This course was more practical than other courses, which I enjoyed quite a bit. 

Another course is CS-330 Computational graphics, taught me the principles of graphics, with an emphasis on modelling, lighting and shaders. This is the sort of work I am passionate, yet it was still challenging. It required that I quickly learn OpenGL to solve the various hurdles in order to implement my projects. OpenGL is an established graphics API, but quite difficult, and learning it in order to deliver on my projects was a quite valuable, both for my career and future aspirations. I used one of this course's project for my enhancements below, which required I implement a more realistic collision-detection algorithim with performance in mind.
 
Lastly, CS465 Full-Stack Development course showed my the improtance of System architecture in web development, how choosing the right tech stack (like the MEAN stack) all have a direct impact on a programs's performance and security, for example how different architectures will require different authentication implementations. This course aslo required that I showcase my understanding in detail of the various intericate details of the project, by writing an extensive paper on its design, as well as create various diagrams to demonstrate my understanding.

During my time at SNHU, I believe I have improved my problem-solving skills, learning abilitiy (which I consider to be a seperate skill), all while being under time constraint, while also being an honor student. I also developed a security mindset, and a strong understanding of testing, in order to pass my coursework, as well as learning how important these factors are when a project grows in complexity. I have also gained an understanding of graphics, advanced web-development with a focus on architecture as well as learning HTML/CSS/JS/Java(Spring), and finally an understanding of operating systems and its various functions like system calls, assembly, systems architecture and developing with c/c++.

I hope my portfolio, showcases my hardwork at SNHU. I aim to pursue higher education with a focus on graphics, simulation modelling, and I believe the skills I have learned here at SNHU, as well my eportfolio, Will help me in that pursuit.


## Course Outcomes

Throughout my ePortfolio I hope to demonstrate some of my skills as well as  knowledge of computer science and meet the following outcomes:

* Employ strategies for building collaborative environments that enable diverse audiences to support organizational decision making in the field of computer science
* Design, develop, and deliver professional-quality oral, written, and visual communications that are coherent, technically sound, and appropriately adapted to specific audiences and contexts
* Design and evaluate computing solutions that solve a given problem using algorithmic principles and computer science practices and standards appropriate to its solution, while managing the trade-offs involved in design choices
* Demonstrate an ability to use well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals
* Develop a security mindset that anticipates adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources

## Informal Code Review

### Overview

My code review video contains all three origianl artifacts. I analyze each artifact separately for problems and enhancements.
The basis of my code review focuses on three critical elements:

- Existing functionality: A detailed walk-through of the existing code focused on the features and function of the current code.
- Code Analysis: Target areas of improvement in structure, logic, efficiency, functionality, security, testing, commenting, and documenting.
- Enhancements: A walk-through of planned enhancements that address issues raised in the code analysis.

### Code Review Video

<iframe width="560" height="315" src="https://www.youtube.com/embed/4MZ7GAVOqXw" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

## Artifact 1: Software Engineering and Design

### Introduction

The artifact is an enhanced version of a project from the CS330 Computer Visualizations course. Originally created as part of my coursework. 
it's a graphics programming project using OpenGL and GLFW to render dynamic graphical elements such as circles (balls), bricks. My enhancement includes a user-controlled controlled paddle, which adds interactivity to the game as well as improving the codebase OOP design. This significantly expands on the original coursework.

[Software Engineering & Design Narrative](https://github.com/ibra9kadabrah/cs-499/tree/main/Narratives/Software%20engineering%20%26%20Design%20Narrative.docx)

### Original Artifact
- [Original Build Files](https://github.com/ibra9kadabrah/cs-499/tree/main/ArtifactOne/original)

![Original](./images/original.png)
*Figure 1 - Original

![Original](./images/original2.png)
*Figure 2 - Original-balls flying around


### Enhanced Artifact
- [Final Build Files](https://github.com/ibra9kadabrah/cs-499/tree/main/ArtifactOne/enhanced)

![Enhanced](./images/ArtifactOne-Paddle.png)
*Figure 3 - User-controlled paddle

### Conclusion

The project went from something with barely any interactivity, bad OOP due to lack of encapsulation, primitive collision detection, to having a user controlled object, a lose condition, a more realistic collision detection system as well as better maintainability.

## Artifact 2: Algorithms and Data Structure

### Introduction

The artifact is the same as in the previous enhancement. It’s a 2D-graphical program from cs330 computer visualization course, then further enhanced in my former enhancement. After enhancement, it went from a simple 2d graphical program into an interactive brick-breaking game implemented using classes like Brick, Circle (representing the ball), and Paddle

[Algorithms and Data Structures Narrative](https://github.com/ibra9kadabrah/cs-499/tree/main/Narratives/Algorithms%20and%20Data%20Structures%20Narrative.docx)

### Enhanced Artifact
- [Final Build Files](https://github.com/ibra9kadabrah/cs-499/tree/main/ArtifactTwo/enhanced)


![Current Enhancement](./images/ArtifactTwo.png)
*Figure 4 - A randomally generated brick layout

![Current Enhancement](./images/ArtifactTwo-lvl2.png)
*Figure 4 - A randomally generated brick layout Level 2

### Conclusion

The project became a truly procedurally generated game, with multiple levels, difficulty adjuster, record keeping, significantly enhanced algorithms, more complex, new data structures as well as improvements to collision detection accuracy and performance. This was a very enjoyable experience as well as a great learning opportunity


## Artifact 3: Databases

## Introduction

The Artifact is an android weight tracker app I made for CS360 course. Users are able to set a weight goal, enter daily weight as well as view last 7 days of data. There is also an interactive web-interface.
Use Juypter to interact with web-interface. It is hosted on http://127.0.0.1:8050/ by default
[Database Narrative](https://github.com/ibra9kadabrah/cs-499/tree/main/Narratives/Database%20Narrative.docx)
### Enhanced Artifact
- [Final Build Files](https://github.com/ibra9kadabrah/cs-499/tree/main/ArtifactThree/enhanced)


### Conclusion

With this enhancement, the weight tracker app, went from an app that used a simple sqllite databaes to using firebase firestore. It also used plain text local authnetication, while now it uses firebase authentication with encryption in storage and in flight. I also added a user interface so you can view users weight, weight changes, as well as weight trends over time.