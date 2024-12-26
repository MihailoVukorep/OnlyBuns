# must do:
- delete account cron job after some time
- admin/accounts search needs paging
- remove all exceptions -- use web error codes

# to fix:
- use ratelimiter class to limit follow / reply ??
- when counting things: dont do collection.size() call repository.countSomething (ex. rep.countLikes...) - WHY: since .size() is int it won't overflow on large numbers
- fix paging on fyp

# NEW CSS NEEDED:
- popup
- comment form
- create/edit post

# look:
- fix error page navbar
- fix error page buttons

# could be nice:
- delete post frontend - confirmation dialogue (YES/NO)
- reply - add picture
- create / edit add ENTER evets to textbox
- all buttons need title="" with proper tooltip
- remove main.js if not needed

