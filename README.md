# TempAlarm1
An app that enables users to have alarms to go off at set temperatures

Android app that allows a user to schedule alarms to specific temperatures by their ZIP Code or GPS location.
It would notify a user when temperatures fall below or exceed their specified temperatures by regularly comparing them to retrieved temps for their ZIP Code or GPS Location.
This helps protect against unnoticed changes in temperatures to prevent hypothermia, heat stroke, and energy inefficient household drafts.
The user can also set active times for the app stay alert and inactive times for the app to not disturb the user.
I hope to add warnings for emergencies and a Celsius option.

TODO:
 - app not able to send the alarm when app is closed/not-open (may not be an issue any more, need to test)
 - zip code on front page only updates up if we reload the app.
 - Fix alignment on pages (close, need to fix purple bar)
 - Fix all colors in .xml files (should all point to the defined theme)
 - Make night mode stuff work (theme)
 - Test if alarm actually works

TODO small tweaks:
 - consider making the api key into only thing in a single file, then only gitignore that one file. (rather than having a function def in gitignore)
 - consolidate .gitignore files (TempAlarm1\.idea\.gitignore, TempAlarm1\app\.gitignore, TempAlarm1\.gitignore)
