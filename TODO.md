TODO
====

* In Status Report Activity, Long-press status report entries to edit them
* A Carb Percentage library 
    - stores names of foods and their percentages, provided by the user (initially empty).
    - when using carb calculator, user can choose existing food from the local library
* Only enable record button if entry time is not in the future (at the moment the entry time was set)
* In Status Report Activity, display a NOTES entry next to a CP entry, if they are within ~1 second of each other 
* Confirmation dialog for BI if it's not the normal take-time or amount, or if it's weird somehow
* Add a way to record whether or not I've eaten anything, without estimating CP.
	- good for when i don't know/don't want to estimate CPs
* Create notifications if predicted resting BG is too low or high
* Create reminder to check BG again, ~15 minutes after a hypo
    - also remind to eat something, if no CPs were recorded during hypo
* For the Edit Numbers Dialog:
	- set a max number of recipients (5?), to prevent our app from being accused of spamming by the android watchdogs
	    * apps are only allowed to send so many texts per minute
	- add confirmation dialog when deleting numbers by long-tapping them
* Diabetes Analysis Activity
	- total daily dosage of insulin
	- injections per day
	- mealtime CP:QA ratio settings (editable)
	- next BI dosage (editable)
	- week average this week, last week
		* where 'this week' is the last 7 days, and 'last week' is the 7 days before that
	- hypos in the last 7 days
- In Status Report Activity, better layout/styling
* Reintroduce simpler cooldown bunching
    - Only bunch NOTES, QA and CP messages, **not** BG or KT
* Add ability to record exercise
    - AFAIK, there is no DAFNE-mandated way to estimate amounts of exercise
    - but a simple eventful tick may suffice; eg "I've just done some exercise"



Bugs
===
* When rotating Carb Calculator, fullscreen IME appears in landscape, 
	and when reverting to portrait, existing ingredients are wiped
	- fix: make activity portrait-only

Stashed (might not do)
===

* Edit Last Entry button in the menu
* Write a conversion method to parse and convert CSV data

Done
===
* Status Report activity
    * Create a Log Review screen, which displays the ~5 most recent entries
	* CP eaten in the last 4 hours
	* QA taken in the last 4 hours
	* last BG reading
	- last BI dosage - actually this needs to be BI in the last 24 hours, 
	in case people take overlapping doses 12 hours apart, or reconsider a lower dosage and top it up with a few units
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
* store CP values in SQL db as grams (x10), to avoid floating-point rounding error

* Adding together BG readings doesn't make sense, but they  currently are in the sms buncher
* entering the same data in the BG field causes a crash:
	-  crash is caused by trying to add 2 datapoints in the same field (eg, BG) with the exact same time.
	This occurs by not refreshing the event time, and submitting two BG readings with the same time. Incorrect usage,
	but shouldn't crash the app

Tried and Removed
=====
* Cooldown bunching algo
	- there's a MAXIMUM_EXTENSION, which is the maximum amount of time since the first entry, before we must send.
		This can be longer than the SMS_BUNCH_DELAY, because TEXTS will often be sent sooner than this maximum,
		so the only thing limiting the maximum time is the staleness of the earliet entry(s),
		and the corresponding inaccuracy of the single timestamp.
		= 300 seconds (5 minutes)
	- also COOLDOWN_TIME, which is the longest we'll wait to send, without any new data. 
	This makes it the minimum time before a text is sent (usually, except for clashes), 
	so it should be a bit shorter than SMS_BUNCH_DELAY,
	but still long enough that we have enough time to chain a few entries.
	= 30seconds
	- So every time we receive data,
		if the new data doesn't clash with any existing bunched data,
		or if the new data's timestamp isn't within RECENCY_WINDOW seconds of now,
			we store the data in the bunched values.
		otherwise,
			we send the new data immediately.

		if there's already data queued,
			if the time extension would put us past TIME_OF_EARLIEST_BUNCHED_DATA + MAXIMUM_EXTENSION,
				then extend to TIME_OF_EARLIEST_BUNCHED_DATA + MAXIMUM_EXTENSION.
				Or if the sendTime is already == TIME_OF_EARLIEST_BUNCHED_DATA + MAXIMUM_EXTENSION,
					do nothing
			otherwise,
				we extend the time-till-send to COOLDOWN_TIME seconds from now,
		otherwise,
			we start the timer thread thing, to run COOLDOWN_TIME seconds from now


Long-Term
---------

* Sync data across instances
* graph of BG, QA, CP, etc
