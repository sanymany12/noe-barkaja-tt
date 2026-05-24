# Noé Bárkája – Transport Tycoon 

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

## 🚀 Key Features & Technical Challenges

### 🌲 Environment & 2.5D Graphics
* **Isometric Rendering Engine:** Developed a custom 2.5D visual system to represent the game world, including specialized rendering logic for diverse terrain types and **dynamic forest systems**.
* **Layered Visuals:** Implemented depth-sorting to ensure correct overlapping of vehicles, buildings, and environmental assets within the isometric perspective.

### 🗺️ Navigation & UI
* **Real-time Minimap:** Engineered a live-syncing minimap system that provides a strategic overview of the transportation network, helping players track vehicle movements across large maps.

### 💾 Persistence & Data Integrity 
* **Robust Save/Load System:** Implemented a custom serialization architecture to persist the entire game state.
* **Complex Object Mapping:** Solved deep nesting issues in the object graph (Roads, Stations, Vehicles) to ensure 100% data integrity during save-reload cycles.

### 🛠️ Quality Assurance & DevOps
* **Automated Testing:** Covered core business logic with **JUnit 5** to ensure stability during the development of complex features like routing and economic calculations.
* **CI/CD Pipeline:** Utilized GitLab CI for automated build verification and testing, ensuring that every contribution met the project's quality standards.

### 🏗️ Software Architecture & Design
* **Class Diagram & System Design:** The entire application was meticulously planned using UML class diagrams before implementation, ensuring a highly decoupled and maintainable codebase.
* **Centralized Asset Management:** Implemented a robust **AssetManager** to handle the asynchronous loading and caching of textures, sounds, and map data, preventing memory leaks and optimizing performance during runtime.
* **Full-Stack Involvement:** Contributed to multiple domains of the project, from core engine logic to UI/UX refinements and data persistence.

### 👥 Collaborative Software Development
*   Developed within a **team of three engineers**.
*   Utilized Git branch management strategies to work simultaneously on features without merge conflicts.
*   Focused on writing clean, testable code validated through automated unit tests with **JUnit 5**.
