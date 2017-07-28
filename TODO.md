TODO
====

* Edit Last Entry button in the menu
* Also store log entries in SQLite. Not Realm: the company which makes it is pushing its new product too much, 
will probably disappear/get bought out soon
	- use 1 table per entry-field, each table with two columns: eventTime:long, and the entry content
	- write a conversion method to parse and convert CSV data
* Create a Log Review screen, which displays the ~5 most recent entries
* Status Report activity
	- total daily insulin dosage
	- mealtime CP:QA ratio settings (editable)
	- week average this week, last week
		* where 'this week' is the last 7 days, and 'last week' is the 7 days before that
* meal CP calculator
	- add ingredients, each with weight and carb per 100g
	- gives you the carb per 100g of the combination of ingredients
* create notifications if predicted resting BG is low or high
* For the Edit Numbers Dialog:
	- set a max number of recipients (5?), to prevent our app from being accused of spamming by the android watchdogs
	- add confirmation dialog when deleting numbers by long-tapping them
* add a button to reset the time value to Now - alongside the time display/button


Done
===

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