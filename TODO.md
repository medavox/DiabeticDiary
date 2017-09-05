TODO
====



* Edit Last Entry button in the menu
* Also store log entries in SQLite. Not Realm: the company which makes it is pushing its new product too hard,
will probably disappear/get bought out soon
	- use 1 table per entry-field, each table with two columns: eventTime:long, and the entry content
	- write a conversion method to parse and convert CSV data
* Create a Log Review screen, which displays the ~5 most recent entries
* meal CP calculator
	- add ingredients, each with weight and carb per 100g
	- gives you the carb per 100g of the combination of ingredients
* create notifications if predicted resting BG is low or high
* For the Edit Numbers Dialog:
	- set a max number of recipients (5?), to prevent our app from being accused of spamming by the android watchdogs
	- add confirmation dialog when deleting numbers by long-tapping them
* Status Report activity
	- total daily insulin dosage
	- injections per day
	- mealtime CP:QA ratio settings (editable)
	- next BI dosage (editable)
	- week average this week, last week
		* where 'this week' is the last 7 days, and 'last week' is the 7 days before that
	- hypos in the last 7 days
	- last BI dosage

bugfixes
----
* store CP values in SQL db as grams (*10), to avoid floating-point rounding errors
* Adding together BG readings doesn't make sense, but they  currently are in the sms buncher
* entering the same data in the BG field causes a crash:
	-  crash is caused by trying to add 2 datapoints in the same field (eg, BG) with the exact same time.
	This occurs by not refreshing the event time, and submitting two BG readings with the same time. Incorrect usage,
	but shouldn'tcrash the app

```
FATAL EXCEPTION: main
                                                                           Process: com.medavox.diabeticdiary, PID: 15045
                                                                           android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed: bg_table.event_time (code 1555)
                                                                               at android.database.sqlite.SQLiteConnection.nativeExecuteForLastInsertedRowId(Native Method)
                                                                               at android.database.sqlite.SQLiteConnection.executeForLastInsertedRowId(SQLiteConnection.java:780)
                                                                               at android.database.sqlite.SQLiteSession.executeForLastInsertedRowId(SQLiteSession.java:788)
                                                                               at android.database.sqlite.SQLiteStatement.executeInsert(SQLiteStatement.java:86)
                                                                               at android.database.sqlite.SQLiteDatabase.insertWithOnConflict(SQLiteDatabase.java:1471)
                                                                               at android.database.sqlite.SQLiteDatabase.insertOrThrow(SQLiteDatabase.java:1367)
                                                                               at com.medavox.diabeticdiary.writers.SqliteWriter.write(SqliteWriter.java:28)
                                                                               at com.medavox.diabeticdiary.MainActivity.clickRecordButton(MainActivity.java:205)
                                                                               at com.medavox.diabeticdiary.MainActivity_ViewBinding$2.doClick(MainActivity_ViewBinding.java:47)
                                                                               at butterknife.internal.DebouncingOnClickListener.onClick(DebouncingOnClickListener.java:22)
```


Done
===

* add a button to reset the time value to Now - alongside the time display/button
* Make the entry-time editable
* Store entries locally
	- probably in a file
* Automatically tick a field when data is entered, and automatically untick it when it becomes blank
* Use a dialog to specify a list of phone numbers to share the entries with via SMS
	- validate entered numbers are real phone numbers
* Add freeform 'notes', tweetsize or less
	- the max size should depend on the size of a single text, varying on how much other data (QA, CP etc) we're sending.
* In Edit Numbers Dialog
	- add a way to delete an/all entry(ies)
	- format the extant numbers so they look better (eg font)

Long-Term
---------

* Sync data across instances
* graph of BG, QA, CP, etc