# Cross-cutting Concepts
- Includes a user-friendly interface with menus, help pages, and automated calculation features to assist observers.
- Provides visualizations of measured data when connected to an automatic weather station, enhancing data interpretation.

## Navigation
- Main window contains the current weather observation
- Menu with multiple subitems that open additional windows with extra configuration or input forms
  - Frames and Dialogs are closed with:
  - ```java      
    setVisible(false);
    dispose();
    ```
- the `main.java` contains the menu buttons and the corresponding actions which are performed on click

## Persistence
### Configuration
The configuration settings are ex and importable and stored in a txt file

### Application state
Measurement values are only persisted in log files, once they are completed and then send.
During the filling out and user interaction, the state lives within many `public static ...` variables.

Context specific ones (user input) with in the java classes related to the forms, but the majority of the application state is stored within the main.java and its `public static ...` variables
When needed they are directly accessed.

## Tasks with side effect
To not block the main GUI thread with long running tasks and thus keeping the application responsive, SwingWorkers are widely used (~ 200 times).
See here for more details: https://stackoverflow.com/questions/782265/how-do-i-use-swingworker-in-java

## Communication
- Email
- Obs to server
- Obs to AWS

## Control flow
- Uses continue variable (doorgan)

## Data validation
Many field have a data validation, and also other required fields are checked. In an error case a dialog with a detailed help message is opened

## Styling
Due to different light situations and a usage around the whole day, the color theme can be set.
The main class contains a static variable `theme_mode` which is used to determine the current theme. This variable is often used in an `initComponentsX` function

## Initialization of components
- initComponents(); // Auto generated code from the Form Editor
- initComponents2(); // Additional initialization of field, values etc, which is not overwritten when the Form Editor is used
