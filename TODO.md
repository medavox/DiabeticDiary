TODO
====

* Edit Last Entry button in the menu
* Write a conversion method to parse and convert CSV data
* Create a Log Review screen, which displays the ~5 most recent entries
* create notifications if predicted resting BG is low or high
* For the Edit Numbers Dialog:
	- set a max number of recipients (5?), to prevent our app from being accused of spamming by the android watchdogs
	- add confirmation dialog when deleting numbers by long-tapping them
* Status Report activity
	- total daily dosage of insulin
	- injections per day
	- mealtime CP:QA ratio settings (editable)
	- next BI dosage (editable)
	- week average this week, last week
		* where 'this week' is the last 7 days, and 'last week' is the 7 days before that
	- hypos in the last 7 days
	- last BI dosage - actually this needs to be BI in the last 24 hours, 
	in case people take overlapping doses 12 hours apart, or reconsider a lower dosage and top it up with a few units
	- better layout/styling

bugfixes
----
* store CP values in SQL db as grams (*10), to avoid floating-point rounding error

Done
===
* Status Report activity
	* CP eaten in the last 4 hours
	* QA taken in the last 4 hours
	* last BG reading
* meal CP calculator
	- add ingredients, each with weight and carb per 100g
	- gives you the carb per 100g of the combination of ingredients
* Store log entries in both CSV SQLite. Not Realm: the company which makes it is pushing its new product too hard,
will probably disappear/get bought out soon
	- use 1 table per entry-field, each table with two columns: eventTime:long, and the entry content
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

Bugs Fixed
======
* Adding together BG readings doesn't make sense, but they  currently are in the sms buncher
* entering the same data in the BG field causes a crash:
	-  crash is caused by trying to add 2 datapoints in the same field (eg, BG) with the exact same time.
	This occurs by not refreshing the event time, and submitting two BG readings with the same time. Incorrect usage,
	but shouldn't crash the app

Long-Term
---------

* Sync data across instances
* graph of BG, QA, CP, etc