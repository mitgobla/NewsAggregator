# News Aggregator Application

[![Video](https://img.youtube.com/vi/KCaG5id5O8g/0.jpg)](https://www.youtube.com/watch?v=KCaG5id5O8g)

## Overview

This project was created for the CSC306 Mobile Application Development module at Swansea University. The aim of the project was to create a news aggregator app in Android Studio using Kotlin. The app uses the [GNews API](https://gnews.io/) to get the latest news articles from various sources. My application is called Newsify. Newsify is an app that allows users to stay updated with their favorite topics. The app pulls articles from these topics and notifies the user when new articles are added.

## Features

1. **User Profile**: Users can create an account by logging in with Google. Once logged in, they are directed to the user profile page.
2. **Topics**: Users can select their favorite topics by starring them on the topics page. The API pulls articles from these topics. If the API is unable to fetch articles, no articles will be displayed due to API limits.
3. **Notifications**: Users can set notifications for each of their favorite topics by clicking on the bell icon. A background worker periodically fetches articles every 30 minutes. If a new article is added, a notification intent is sent to the user.
4. **Article Viewing**: Clicking on an article loads up a new activity where the article header, image, and content are displayed.
5. **Commenting on Articles**: Users can comment on articles if they are logged in. They can add a comment by clicking 'Add Comment' and then 'Post'. The comment is then added to the article.
6. **Bookmarking Articles**: Users can bookmark articles for later reading.
7. **Sharing Articles**: Users can share articles with others.
8. **Metrics**: Users can track the number of articles they've read for each topic. This feature can be accessed from the profile page.
9. **Accessing Bookmarks**: Users can access their bookmarked articles from the profile page. Clicking on a bookmark loads the article, along with any comments made on it.
