# Noé Bárkája – Transport Tycoon Style Simulation

A complex, 2.5D isometric transport management and simulation desktop application developed as a collaborative university project at Eötvös Loránd University (ELTE). 

The project simulates a dynamic transportation network where players manage vehicles, optimize routes, and handle economic resources under changing environments.

---

## 🛠️ Technology Stack & Architecture
*   **Language:** Java (JDK 17+)
*   **Build Tool:** Maven
*   **Testing Framework:** JUnit 5 (Automated unit testing)
*   **Version Control & Workflow:** Git / GitLab (Migrated to GitHub for portfolio showcase)

The system follows strict Object-Oriented Design (OOD) principles, decoupling the core simulation engine and business logic from the graphical user interface.

---

## 🚀 Key Engineering Highlights

### 💾 Custom Save/Load Architecture
One of the core technical achievements of the project was implementing a robust game-state persistence mechanism. 
*   Designed a multi-layered saving system that handles complex object graphs (vehicles, terrain, schedules, and finances).
*   Resolved deep serialization challenges, properly decoupling UI components and transient runtime execution threads from the core data models using `transient` structures and custom serialization lifecycles.
*   Ensured transactional data integrity, preventing corrupt save files upon failure.

### 👥 Collaborative Software Development
*   Developed within a **team of three engineers**.
*   Utilized Git branch management strategies to work simultaneously on features without merge conflicts.
*   Focused on writing clean, testable code validated through automated unit tests with **JUnit 5**.

---

