# Telegram Bot Java 
Uses the Steam Dota project to to pull dota game stats from the Steam API. Also uses the Yelp API to grab yelp data.
Allows for real time check of friends list on steam. 
Yelp functionality returns paginated result of any Yelp search. 
Dota functionality grabs latest game a player has played based on steamID


## Telegram Bot API
Built on top of the Java API telegram wrapper located here: https://github.com/rubenlagus/TelegramBots

**Note that this bot is meant for private use. The steam IDs for querying have been hard coded into the application. Adding and persisting extra steam IDs would require a database, which was not the original intent of this project. However, the telegram wrapper used does have support for databases in case anyone is interested in pulling this and adding their own persistence. 


## Adding bot on Telegram
Create a new group in Telegram and search for @BOYZbot
Invite to group





