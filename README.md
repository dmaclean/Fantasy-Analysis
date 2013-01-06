Fantasy-Analysis
================

Application that provides a platform for analysis and mining of sports statistics.  The goal is to be able to analyze fantasy teams in order to make draft and trade recommendations.

Current components are:
- Standalone Java application for fetching statistics.


Fetching Statistics
-------------------
This component provides automated fetching of Yahoo Fantasy Football statistics.  For any given year (2001 - present) we can fetch all players that participated that year.  From here, the players information can be stored in the database.  Then, their season and weekly stats are fetched and also stored in the database.  This action only needs to be performed once.
