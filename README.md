## Introduction

The target audience for this application is people who enjoy discovering beautiful, interesting, and pleasant **restaurants** and dining destinations. The app is aimed at both men and women between the ages of 18 and 65. It is assumed that people over the age of 65 are generally less likely to use mobile devices extensively while traveling or dining out.

### Description of the App

Discover new restaurants with our interactive map! Find unique, cozy, and exciting places to eat, get inspiration for your next culinary experience, and explore the world of dining in a simple and engaging way. Perfect for anyone who loves food, travel, and discovering new restaurants!

---

## User Guide

Users can navigate the interactive map to discover restaurants they might want to visit. The map can be explored by dragging with one finger to move and pinching with two fingers to zoom in and out. When a user finds an interesting restaurant, they can tap the marker to view more detailed information about the location — such as its name, type of cuisine, and a brief description.

Users can also access additional views such as **“Settings”** and **“About the App”** via the navigation bar menu (opened by tapping the menu icon).

* In **“Settings”**, users can enable or disable location access to display their current position on the map.
* In **“About the App”**, users can find information about the app version and its purpose.
* Within both **“Settings”** and **“About the App”**, users can navigate back to the main map view using the back button.

The app has been designed with the target audience in mind, featuring large and clear map markers to ensure accessibility and ease of use across different devices.

---

## Description of the Solution

Since the main purpose of the app is to show users different **restaurants**, the map was chosen as the central part of the application. This is reflected in the app’s structure — the main screen is the interactive map itself. When users navigate to other sections of the app, the side menu is no longer accessible, and users can only return to the main page (the map).

Within **MainActivity**, navigation between the different parts of the main view has been implemented. Users can open a side menu (drawer) by clicking the menu icon in the navigation bar. **MainActivity** handles navigation to other sections of the app, except when users choose to navigate back — in those cases, the fragment handles it.
Both **MainActivity**, **SettingsFragment**, and **AboutFragment** use a shared **NavController**, connected to the **NavHostFragment** that is loaded into MainActivity’s **FragmentContainerView**. This ensures that all parts of the app share the same navigation structure.
*(See Figure 1 for an overview of the app’s navigation.)*

**Figure 1:** Overview of the navigation structure in the application.

A **database** has also been implemented to manage the different restaurant destinations. This functionality begins in **MapFragment**, which uses **MainActivity** (not shown in the diagram below) to create an instance of **DestinationViewModel**. This ViewModel provides access to the restaurant data for all fragments within the app.

When accessed for the first time, the database is initialized with a preloaded dataset containing a selection of restaurant locations. The **DestinationViewModel** uses the database initialization provided by **DestinationDao**, where various queries are defined to retrieve, delete, or insert restaurant data. Currently, only retrieval operations are used at this stage.
*(See Figure 2 for an overview of the database classes in the application.)*

**Figure 2:** Overview of the database classes in the app.

---

## Ethics and Security

It is important not to collect unnecessary personal data. This has been implemented by using a simple **Restaurant** model that only contains essential fields: `latitude`, `longitude`, `name`, and `description`. No user-related data (such as phone number, contacts, or device ID) is collected.

Before accessing the user’s location, explicit **permission** is required. The app handles various possible scenarios around this process to ensure that users have full control over location sharing.

To avoid unnecessary data transmission, the app is designed to function **entirely locally** using **Room/SQLite**, meaning that no data is sent outside the application.

Additionally, third-party code can pose a security risk if its purpose is unclear. I have carefully reviewed all external libraries used to ensure that they behave as expected. I also consulted the course supervisors and received approval to use **OpenStreetMap** as the map provider.
